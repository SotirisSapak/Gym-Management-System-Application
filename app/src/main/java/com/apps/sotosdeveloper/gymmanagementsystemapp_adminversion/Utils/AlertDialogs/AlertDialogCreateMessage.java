package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.HomeActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.UsersRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class AlertDialogCreateMessage {

    private static Activity activity;

    // -------------- NAV BUTTONS ------------------
    private static MaterialButton closeBT;
    private static MaterialButton saveBT;
    // -------------- FOR USERS --------------------
    private static RecyclerView recyclerViewUsers;
    private static UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    private static final ArrayList<User> usersAL = new ArrayList<>();
    private static RecyclerView usersRecyclerView;
    // -------------- FOR MESSAGE ------------------
    // add selected user id in this arrayList when user press "save message" button
    private static final ArrayList<String> selectedUserIDs = new ArrayList<>();
    // for layout components
    private static TextInputEditText titleEditText;
    private static TextInputEditText summaryEditText;
    private static SwitchCompat importantMessage;
    // -------------- FOR GENERAL PURPOSES ---------
    private static SharedPreferences preferences;


    // -------------- METHODS ----------------------
    private static void initUI(View view){
        saveBT              = view.findViewById(R.id.save_action);
        closeBT             = view.findViewById(R.id.cancel_action);

        titleEditText       = view.findViewById(R.id.addMessageTitleTIET);
        summaryEditText     = view.findViewById(R.id.addMessageSummaryTIET);
        importantMessage    = view.findViewById(R.id.switchImportantMessage);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        usersRecyclerView   = view.findViewById(R.id.recyclerViewAdapter_users);
        usersRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        usersRecyclerView.setLayoutManager(layoutManager);

        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

    }

    private static void handleNavButtonsClickListener(AlertDialog dialog){
        closeBT.setOnClickListener(v -> dialog.dismiss());
        saveBT.setOnClickListener(v -> createAndSaveMessage(v, dialog));
    }

    private static void createMessage(Map<Object, Object> message, AlertDialog dialog){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String messageSummaryFinal;
        messageSummaryFinal = summaryEditText.getEditableText().toString();
        if(preferences.getBoolean("preference_messages_enable_disable_signature",
                false)){
            String stringBuilder = messageSummaryFinal +
                    "\n" +
                    "\n" +
                    preferences.getString("preference_messages_auto_signature",
                            "");
            message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY,
                    stringBuilder);
        }else{
            message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY,
                    messageSummaryFinal);
        }
        // add data to map object
        message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE,
                titleEditText.getEditableText().toString());

        message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_DATE, getSystemDate());
        message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_IS_IMPORTANT,
                importantMessage.isChecked());
        // add another map to hold users
        Map<String, Boolean> allSelectedUsers = new HashMap<>();
        for (String tempUser: selectedUserIDs){
            allSelectedUsers.put(tempUser, true);
        }
        message.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TO_USER, allSelectedUsers);

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES).
                add(message).
                addOnSuccessListener(documentReference -> {
                    Toast.makeText(activity,
                            "Created a new message", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    HomeActivity.messagesFragment.refreshList();
                }).
                addOnFailureListener(e -> {
                    Toast.makeText(activity,
                            "Fail to create a new message", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
    }

    private static String getSystemDate(){
        return new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()).format(new Date());
    }

    private static void createAndSaveMessage(View view, AlertDialog dialog){
        Map<Object, Object> message = new HashMap<>();
        if(NetworkConnection.isOnline(activity)){
            // do your stuff if only has internet connection
            if(validation(view)){
                // only here finalize selected users if passed verification test!
                finalizeSelectedUsers();
                createMessage(message, dialog);
            }
        }else{
            Snackbar.make(view, activity.getResources().getString(R.string.internet_connection_error),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private static void setUpUsers(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        usersAL.clear();

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().
                addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    // enter every user into recycler view adapter
                    // and pass the data
                    User userTemp =
                            new User(document.getId(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME).
                                            toString(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME).
                                            toString(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL).
                                            toString());

                    String typeTemp = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE).
                            toString();
                    // only CLIENT users will enter in recycler view adapter
                    if(typeTemp.equalsIgnoreCase(DatabaseConstants.FIREBASE_USER_TYPE_1)){
                        usersAL.add(userTemp);
                    }
                }
                // check if arrayList is empty or not
                if (usersAL.isEmpty()){
                    Toast.makeText(activity,
                            "Cannot find any user", Toast.LENGTH_SHORT).show();
                }else{
                    // create the recycler view adapter
                    usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(activity,
                            usersAL);
                    usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    private static void finalizeSelectedUsers(){
        selectedUserIDs.clear();
        for (int i = 0; i < usersRecyclerViewAdapter.clicked.length; i++) {
            if(usersRecyclerViewAdapter.clicked[i]){
                selectedUserIDs.add(usersAL.get(i).getID());
            }
        }
    }

    private static boolean validation(View view){

        //check if any user selected
        int notSelectedUsers = 0;
        for (int i = 0; i < usersRecyclerViewAdapter.clicked.length; i++) {
            if(!usersRecyclerViewAdapter.clicked[i]) notSelectedUsers++;
        }
        // if notSelectedUsers variable is equal with clicked.length table means that nobody was selected
        if(notSelectedUsers == usersRecyclerViewAdapter.clicked.length) {
            Snackbar.make(view, "Not any user selected", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }

        // check if edit texts are empty or not
        if(titleEditText.getText() == null || titleEditText.getText().toString().equals("")) {
            Snackbar.make(view, "Title Required", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }
        if(summaryEditText.getText() == null || summaryEditText.getText().toString().equals("")) {
            Snackbar.make(view, "Message body Required", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }
        return true;
    }

    public static void openAlertDialog(Activity activity){

        if (NetworkConnection.isOnline(activity)){
            AlertDialogCreateMessage.activity = activity;
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_create_message,
                    null);
            builder.setCancelable(true);
            builder.setView(dialogView);

            initUI(dialogView);
            setUpUsers();

            final android.app.AlertDialog dialog = builder.create();

            handleNavButtonsClickListener(dialog);

            Objects.requireNonNull(dialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);

            if (dialog.getWindow() != null)
                dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            dialog.show();

        }

    }

}
