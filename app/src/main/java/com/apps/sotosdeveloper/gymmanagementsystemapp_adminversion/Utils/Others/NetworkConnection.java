package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class NetworkConnection {

    // check if user has internet connection
    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());

    }

}
