package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Adapters;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.InitActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.InternetConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserPreviewActivity extends AppCompatActivity {

    private TextInputEditText firstNameET, lastNameET, emailET, passwordET;
    private MaterialButton applyChangesMB;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        init();

        getAllData();

        applyChangesMB.setOnClickListener(v -> saveNewData());

    }

    private void init(){
        firstNameET     = findViewById(R.id.register_firstNameTIET);
        lastNameET      = findViewById(R.id.register_lastNameTIET);
        emailET         = findViewById(R.id.register_emailTIET);
        passwordET      = findViewById(R.id.register_passwordTIET);
        applyChangesMB  = findViewById(R.id.accountUpdateApplyChangesMBT);

        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = InitActivity.memoryHandler.getUserIDFromStorage();
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
    }

    private void getAllData(){
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                document(userID).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String tempFirstName = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).
                                get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME)).toString();
                        String tempLastName = Objects.requireNonNull(task.getResult().
                                get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME)).toString();
                        String tempEmail = Objects.requireNonNull(task.getResult().
                                get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL)).toString();
                        String tempPassword = Objects.requireNonNull(task.getResult().
                                get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_PASSWORD)).toString();

                        firstNameET.setText(tempFirstName);
                        lastNameET.setText(tempLastName);
                        emailET.setText(tempEmail);
                        passwordET.setText(tempPassword);

                    }
                });
    }

    private void saveNewData(){

        if (InternetConnection.isOnline(this)){
            if(validate()){
                CollectionReference usersCollection =
                        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS);
                DocumentReference documentReference = usersCollection.document(userID);

                Map<String, Object> updatableFields = new HashMap<>();
                updatableFields.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME,
                        firstNameET.getEditableText().toString());
                updatableFields.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME,
                        lastNameET.getEditableText().toString());
                documentReference.update(updatableFields);

                showRestartAppAlertDialog();

            }
        }else{
            Toast.makeText(this, "Enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRestartAppAlertDialog(){
        AlertDialog.Builder alertRestart = new AlertDialog.Builder(UserPreviewActivity.this);
        alertRestart.setMessage("You must restart app");
        alertRestart.setTitle("Restart");
        alertRestart.setCancelable(false);
        alertRestart.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertRestart.show();
    }

    private boolean validate(){

        if(firstNameET.getText() == null || firstNameET.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(lastNameET.getText() == null || lastNameET.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


}