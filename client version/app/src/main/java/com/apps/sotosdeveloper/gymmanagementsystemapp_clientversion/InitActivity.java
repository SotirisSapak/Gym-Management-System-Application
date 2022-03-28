package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.MemoryHandler;

public class InitActivity extends AppCompatActivity {

    public static MemoryHandler memoryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        init();

        chooseActivityByUserID();

    }

    private void init(){
        memoryHandler = new MemoryHandler(this);
    }

    private void chooseActivityByUserID(){

        Intent intentLoginActivity = new Intent(this, LoginActivity.class);
        Intent intentMainActivity = new Intent(this, MainActivity.class);

        if(memoryHandler.getUserIDFromStorage() == null
                || memoryHandler.getUserIDFromStorage().equalsIgnoreCase("")){
            // at this state no user id has saved in local storage
            // go to register activity
            startActivity(intentLoginActivity);
            finish();
        }

        if(!memoryHandler.getUserIDFromStorage().isEmpty()){
            // at this state user id has saved in local storage - go to main activity
            // main activity will get userID by memory handler class
            startActivity(intentMainActivity);
            finish();
        }

    }

}