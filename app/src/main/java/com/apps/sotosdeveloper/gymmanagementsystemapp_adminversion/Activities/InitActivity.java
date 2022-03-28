package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.AppDataHandler;

public class InitActivity extends AppCompatActivity {

    public static AppDataHandler appDataHandler;
    private Intent intentMain, intentLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        chooseActivity();
    }

    private void init(){
        appDataHandler = new AppDataHandler(this);
        intentLogin = new Intent(this, LoginActivity.class);
        intentMain  = new Intent(this, HomeActivity.class);
    }

    private void chooseActivity(){
        if(appDataHandler.hasLoggedIn()){
            startActivity(intentMain);
        }else{
            startActivity(intentLogin);
        }
        finish();
    }

}