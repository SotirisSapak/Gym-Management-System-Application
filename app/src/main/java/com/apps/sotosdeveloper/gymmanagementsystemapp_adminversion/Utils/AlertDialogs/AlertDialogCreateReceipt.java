package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.HomeActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.AppConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.Filters;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.ThemeManager;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.AllUsersRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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

public class AlertDialogCreateReceipt {

    private static Activity activity;

    // ----------------- NAV BUTTONS -------------------
    private static MaterialButton closeBT;
    private static MaterialButton saveBT;
    // ----------------- UI COMPONENTS -----------------
    public static TextView selectedUserTV;
    private static TextInputEditText receiptTitleET;
    private static TextInputEditText receiptAmountET;
    // ----------------- FOR USERS ---------------------
    private static FirebaseFirestore firebaseFirestore;
    private static ArrayList<User> usersAL;
    private static AllUsersRecyclerViewAdapter addReceiptUsersAdapter;
    private static RecyclerView recyclerViewUsers;
    // ----------------- FOR MODES ---------------------
    private static MaterialCardView receiptSingle, receiptMonthly, receiptAnnually;
    private static String mode = DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_SINGLE;
    // ----------------- METHODS -----------------------

    private static void initUI(View view){
        closeBT             = view.findViewById(R.id.cancel_action);
        saveBT              = view.findViewById(R.id.save_action);
        selectedUserTV      = view.findViewById(R.id.addReceiptSelectedUserEntryTV);
        receiptTitleET      = view.findViewById(R.id.addReceiptIDET);
        receiptAmountET     = view.findViewById(R.id.addReceiptEnterAmountET);
        recyclerViewUsers   = view.findViewById(R.id.recyclerViewAdapter_users);

        receiptSingle       = view.findViewById(R.id.receipt_mode_simple);
        receiptMonthly      = view.findViewById(R.id.receipt_mode_monthly);
        receiptAnnually     = view.findViewById(R.id.receipt_mode_annually);

        recyclerViewUsers.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewUsers.setLayoutManager(layoutManager);
    }

    private static void setReceiptSingle_clickListener(){
        receiptSingle.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_SINGLE));
    }
    private static void setReceiptMonthly_clickListener(){
        receiptMonthly.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_MONTHLY));
    }
    private static void setReceiptAnnually_clickListener(){
        receiptAnnually.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_ANNUALLY));
    }

    private static void filterClick(String filter){
        switch (filter){
            case Filters.RECEIPT_MODE_SINGLE:
                receiptSingle.setCardElevation(8f);
                receiptSingle.setStrokeWidth(3);
                receiptSingle.setStrokeColor(ThemeManager.themeColorPrimary(activity));

                receiptMonthly.setCardElevation(0f);
                receiptMonthly.setStrokeColor(0);
                receiptAnnually.setCardElevation(0f);
                receiptAnnually.setStrokeColor(0);
                mode = DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_SINGLE;
                break;
            case Filters.RECEIPT_MODE_MONTHLY:
                receiptMonthly.setCardElevation(8f);
                receiptMonthly.setStrokeWidth(3);
                receiptMonthly.setStrokeColor(ThemeManager.themeColorPrimary(activity));

                receiptAnnually.setCardElevation(0f);
                receiptAnnually.setStrokeColor(0);
                receiptSingle.setCardElevation(0f);
                receiptSingle.setStrokeColor(0);
                mode = DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_MONTHLY;
                break;
            case Filters.RECEIPT_MODE_ANNUALLY:
                receiptAnnually.setCardElevation(8f);
                receiptAnnually.setStrokeWidth(3);
                receiptAnnually.setStrokeColor(ThemeManager.themeColorPrimary(activity));

                receiptMonthly.setCardElevation(0f);
                receiptMonthly.setStrokeColor(0);
                receiptSingle.setCardElevation(0f);
                receiptSingle.setStrokeColor(0);
                mode = DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_ANNUALLY;
                break;
        }
    }

    private static String getSystemDate(){
        return new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                Locale.getDefault()).format(new Date());
    }

    private static String getSelectedUserID(){
        if(addReceiptUsersAdapter.selectedUserPos == -1){
            return null;
        }
        return usersAL.get(addReceiptUsersAdapter.selectedUserPos).getID();
    }

    private static void handleNavButtonsClickListener(AlertDialog dialog){
        closeBT.setOnClickListener(v -> dialog.dismiss());
        saveBT.setOnClickListener(v -> createAndSaveReceipt(v, dialog));
    }

    private static void createAndSaveReceipt(View v, AlertDialog dialog){
        if(NetworkConnection.isOnline(activity)){
            // only if internet connection is enabled
            if(validation(v)){
                Map<Object, Object> receipt = new HashMap<>();
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_ID,
                        receiptTitleET.getEditableText().toString());
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE, getSystemDate());
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_TO_USER, getSelectedUserID());
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT,
                        receiptAmountET.getEditableText().toString());
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID, false);
                receipt.put(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE, mode);

                firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS).
                        add(receipt)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(activity,
                                    "Created a new receipt", Toast.LENGTH_SHORT).show();
                            HomeActivity.receiptViewerFragment.refreshList();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> dialog.dismiss());
            }
        }
    }

    private static void getAllUsers(){
        usersAL = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    // enter every user into listView adapter
                    // and pass the data
                    User userTemp =
                            new User(document.getId(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME).toString(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME).toString(),
                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL).toString());

                    String typeTemp = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE).toString();
                    if(typeTemp.equalsIgnoreCase(DatabaseConstants.FIREBASE_USER_TYPE_1)){
                        usersAL.add(userTemp);
                    }
                }
                // check if arrayList is empty or not
                if (usersAL.isEmpty()){
                    Toast.makeText(activity,
                            "Cannot find any user", Toast.LENGTH_SHORT).show();
                }else{
                    addReceiptUsersAdapter = new AllUsersRecyclerViewAdapter(
                            usersAL);
                    recyclerViewUsers.setAdapter(addReceiptUsersAdapter);
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    private static boolean validation(View view){

        if(receiptTitleET.getText() == null || receiptTitleET.getText().toString().equals("")){
            Snackbar.make(view,
                    "Receipt require a title", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        if(receiptAmountET.getText() == null || receiptAmountET.getText().toString().equals("")){
            Snackbar.make(view,
                    "Receipt require the amount", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        if(getSelectedUserID() == null || getSelectedUserID().equals("")){
            Snackbar.make(view,
                    "Select a client to add the receipt", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        return true;

    }

    public static void openAlertDialog(Activity activity){

        if (NetworkConnection.isOnline(activity)){
            AlertDialogCreateReceipt.activity = activity;
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_create_receipt,
                    null);
            builder.setCancelable(true);
            builder.setView(dialogView);

            initUI(dialogView);

            setReceiptSingle_clickListener();
            setReceiptMonthly_clickListener();
            setReceiptAnnually_clickListener();
            getAllUsers();

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
