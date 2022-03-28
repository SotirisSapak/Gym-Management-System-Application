package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.InternetConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton registerBT, loginBT;
    private TextInputEditText email, password;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        goToRegisterActivity();
        loginBT.setOnClickListener(v -> {
            if (InternetConnection.isOnline(this)) tryLogin();
            else Toast.makeText(this, "Enable internet connection",
                    Toast.LENGTH_SHORT).show();
        });

    }

    private void tryLogin() {
        if(validation()){
            Intent intentMainActivity = new Intent(this, MainActivity.class);
            // get data from user input (is not null)
            String passwordString = Objects.requireNonNull(password.getText()).toString();
            String emailString = Objects.requireNonNull(email.getText()).toString();

            firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                    get().
                    addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                                if(Objects.requireNonNull(document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE)).
                                        toString().equalsIgnoreCase(DatabaseConstants.FIREBASE_USER_TYPE_1)){
                                    // only for client users will search for verification purposes
                                    if(Objects.requireNonNull(document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL)).
                                            toString().equalsIgnoreCase(emailString)){
                                        // check for password too
                                        if(Objects.requireNonNull(document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_PASSWORD)).
                                                toString().equalsIgnoreCase(passwordString)){
                                            // found user
                                            InitActivity.memoryHandler.saveUserIDToStorage(document.getId());
                                            finish();
                                            startActivity(intentMainActivity);
                                        }else{
                                            Toast.makeText(getBaseContext(),
                                                    "Wrong credentials!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private boolean validation() {

        if(email.getText() == null || email.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText() == null || password.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void goToRegisterActivity() {
        Intent intentRegister = new Intent(this, RegisterActivity.class);
        registerBT.setOnClickListener(v->{
            startActivity(intentRegister);
            finish();
            overridePendingTransition(R.anim.slide_out_bottom, android.R.anim.fade_out);
        });
    }

    private void init(){

        firebaseFirestore = FirebaseFirestore.getInstance();

        registerBT = findViewById(R.id.buttonRegister);
        loginBT = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.login_emailTIET);
        password = findViewById(R.id.login_passwordTIET);

    }

}