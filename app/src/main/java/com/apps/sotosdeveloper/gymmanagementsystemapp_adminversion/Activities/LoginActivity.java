package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities;

import android.content.Intent;
import android.os.Bundle;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.AppDataHandler;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText username, password;
    private MaterialButton loginButton;

    private String usernameString, passwordString;
    private AppDataHandler appDataHandler;
    private Intent mainActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        getAdminInfo();

        loginButton.setOnClickListener(v -> {
            if(NetworkConnection.isOnline(this)){
                if(validation(v)) {
                    tryLogin();
                }
            }else{
                Toast.makeText(this, "Please enable internet connection",
                        Toast.LENGTH_SHORT).show();
            }
            
        });

    }

    private void init(){
        firebaseFirestore = FirebaseFirestore.getInstance();

        appDataHandler = InitActivity.appDataHandler;
        mainActivityIntent = new Intent(this, HomeActivity.class);

        username    = findViewById(R.id.loginUsernameTIET);
        password    = findViewById(R.id.loginPasswordTIET);
        loginButton = findViewById(R.id.loginMB);
    }

    private void getAdminInfo(){
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                            if(Objects.equals(document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE),
                                    DatabaseConstants.FIREBASE_USER_TYPE_2)){
                                usernameString = document.getString(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL);
                                passwordString = document.getString(DatabaseConstants.FIREBASE_DOCUMENT_USERS_PASSWORD);
                            }
                        }
                    }
                });
    }

    private void tryLogin(){

        if(usernameString == null || usernameString.equalsIgnoreCase("")) {
            return;
        }
        if(passwordString == null || passwordString.equalsIgnoreCase("")) {
            return;
        }

        if(usernameString.equalsIgnoreCase(Objects.requireNonNull(username.getText()).toString())){
            if(passwordString.equalsIgnoreCase(Objects.requireNonNull(password.getText()).toString())){
                // both entries are correct
                appDataHandler.storeLoginSuccess(usernameString);
                Toast.makeText(this,
                        "Login Success", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(mainActivityIntent);
            }else{
                Toast.makeText(this,
                        "Wrong input fields!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,
                    "Wrong input fields!", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validation(View view){
        if(username.getText() == null ||
                username.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(view, "Must insert username", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if(password.getText() == null ||
                password.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(view, "Must insert password", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}