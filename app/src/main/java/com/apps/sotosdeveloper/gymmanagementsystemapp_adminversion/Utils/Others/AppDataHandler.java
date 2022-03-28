package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.AppConstants;

/**
 * Store info for offline use
 */
public class AppDataHandler {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public AppDataHandler(Activity activity){
        String MY_PREFERENCES = "app_preferences";
        sharedPreferences = activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Store to app internal database the email that admin has!
     * @param email: admin's email
     */
    public void storeLoginSuccess(String email){
        editor.putBoolean(AppConstants.APP_PREFERENCE_HAS_LOGGED_IN, true);
        editor.putString(AppConstants.APP_PREFERENCE_EMAIL, email);
        editor.apply();
    }

    public boolean hasLoggedIn(){
        return sharedPreferences.
                getBoolean(AppConstants.APP_PREFERENCE_HAS_LOGGED_IN, false);
    }

    public void logout(){
        editor.clear();
        editor.apply();
    }

}
