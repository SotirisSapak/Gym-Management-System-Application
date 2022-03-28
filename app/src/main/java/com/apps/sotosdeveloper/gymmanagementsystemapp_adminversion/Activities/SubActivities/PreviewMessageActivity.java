package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities;

import android.content.Intent;
import android.os.Bundle;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.AppConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;
import java.util.Objects;

public class PreviewMessageActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;

    private TextView dateTV;
    private TextView summaryTV;
    private TextView usersTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_message_acitivty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // get from MessagesFragment messageID to find and preview Message
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String messageID = extras.
                getString(AppConstants.PREVIEW_MESSAGE_ACTIVITY_MESSAGE_ID_EXTRAS);
        findMessage(messageID);
    }

    private void init(){
        firebaseFirestore = FirebaseFirestore.getInstance();

        summaryTV   = findViewById(R.id.previewMessageSummaryTV);
        dateTV      = findViewById(R.id.previewMessageDateTV);
        usersTV     = findViewById(R.id.previewMessageSelectedUsersTV);
    }

    private void findMessage(String messageID){
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES).document(messageID).
                get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        // toolbar title
                        Objects.requireNonNull(getSupportActionBar()).
                                setTitle(task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE).toString());

                        summaryTV.setText(task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY).toString());
                        dateTV.setText(
                                String.format("Message created: %s", task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_DATE).toString()));

                        //get each user
                        Map<String, Boolean> allUsers = (Map<String, Boolean>)
                                task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TO_USER);

                        findUsers(allUsers);
                    }
                });
    }

    private void findUsers(Map<String, Boolean> allUsers){
        StringBuilder stringBuilder = new StringBuilder();

        final String[] firstName = new String[1];
        final String[] lastName = new String[1];

        for (String ids: allUsers.keySet()){
            // in database only a user id will be saved
            // so, extra code to find first and last name of this user id.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                    get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String id = document.getId();
                        if(id.equalsIgnoreCase(ids)){
                            firstName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME).
                                    toString();
                            lastName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME).
                                    toString();


                            //String formatting...
                            stringBuilder.append("✓ ");
                            stringBuilder.append(String.format("%s %s", firstName[0], lastName[0]));
                            stringBuilder.append("\n");

                        }
                    }
                    usersTV.setText(stringBuilder.toString());
                }
            });
        }

    }


}