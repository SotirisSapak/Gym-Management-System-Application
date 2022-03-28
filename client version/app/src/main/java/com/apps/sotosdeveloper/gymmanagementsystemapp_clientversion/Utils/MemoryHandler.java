package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.AppConstants;

public class MemoryHandler {

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public MemoryHandler(Activity activity){
        preferences = activity.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getUserIDFromStorage(){
        if(preferences.getString(AppConstants.APP_PREFERENCE_USER_ID, "") == null ||
                preferences.getString(AppConstants.APP_PREFERENCE_USER_ID, "").
                        equalsIgnoreCase("")){
            return "";
        }
        return preferences.getString(AppConstants.APP_PREFERENCE_USER_ID, "");
    }

    public void saveUserIDToStorage(String userID){
        editor.putString(AppConstants.APP_PREFERENCE_USER_ID, userID);
        editor.apply();
    }

    public void removeUserFromDatabase(){
        editor.clear();
        editor.apply();
    }

}
