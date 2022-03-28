package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion;

import android.content.Intent;
import android.os.Bundle;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.InternetConnection;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText firstNameET, lastNameET, emailET, passwordET;
    private FirebaseFirestore firebaseFirestore;

    private final ArrayList<String> allEmails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Register");

        init();

        // get all user emails in order not to create a new user with the same email
        getAllCurrentUserEmails();

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(validateEntries(view)){
                // all entries are ok!
                // try to store all user info in online database (check internet connection first)
                if(InternetConnection.isOnline(this)) createUserToDatabase();
                else Toast.makeText(this, "Enable internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        firstNameET     = findViewById(R.id.register_firstNameTIET);
        lastNameET      = findViewById(R.id.register_lastNameTIET);
        emailET         = findViewById(R.id.register_emailTIET);
        passwordET      = findViewById(R.id.register_passwordTIET);

        firebaseFirestore = FirebaseFirestore.getInstance();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intentLogin = new Intent(this, LoginActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_login) {
            startActivity(intentLogin);
            finish();
            overridePendingTransition(0, R.anim.slide_in_bottom);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
        overridePendingTransition(0, R.anim.slide_in_bottom);
    }

    private void getAllCurrentUserEmails(){
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                            allEmails.add(Objects.requireNonNull(document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL)).
                                    toString());
                        }
                    }
                });
    }

    private boolean validateEntries(View view){

        // first name entry is empty
        if(firstNameET.getText() == null ||
                firstNameET.getText().toString().equalsIgnoreCase("")){
            Snackbar.make(view, "First name is empty", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }

        // last name entry is empty
        if(lastNameET.getText() == null ||
                lastNameET.getText().toString().equalsIgnoreCase("")){
            Snackbar.make(view, "Last name is empty", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }

        // email entry is empty
        if(emailET.getText() == null ||
                emailET.getText().toString().equalsIgnoreCase("")){
            Snackbar.make(view, "Email is empty", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }

        // password entry is empty
        if(passwordET.getText() == null ||
                emailET.getText().toString().equalsIgnoreCase("")){
            Snackbar.make(view, "Password is empty", Snackbar.LENGTH_SHORT).
                    setAction("Action", null).show();
            return false;
        }

        return true;

    }

    private void createUserToDatabase(){

        if(allEmails.isEmpty()){
            Toast.makeText(this, "An error occurred. Try again", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent mainActivityIntent = new Intent(this, MainActivity.class);

        Map<Object, Object> userMap = new HashMap<>();
        userMap.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME,   firstNameET.getEditableText().toString());
        userMap.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME,    lastNameET.getEditableText().toString());
        userMap.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL,        emailET.getEditableText().toString());
        userMap.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_PASSWORD,     passwordET.getEditableText().toString());
        userMap.put(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE,         DatabaseConstants.FIREBASE_USER_TYPE_1);

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                add(userMap).
                addOnSuccessListener(documentReference -> {
                    // store user id in local database
                    InitActivity.memoryHandler.saveUserIDToStorage(documentReference.getId());
                    startActivity(mainActivityIntent);
                    finish();
                }).addOnFailureListener(e -> {
                    // cannot store user info in online database
                    Toast.makeText(RegisterActivity.this,
                            "Cannot create new user", Toast.LENGTH_SHORT).show();
                    Log.e("USER-CREATE-ERROR", e.getMessage());
                });
    }

}