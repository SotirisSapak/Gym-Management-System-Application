package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;

import java.util.Objects;

public class AlertDialogUserToolbarIcon {

    public static void openDialog(Activity activity){
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_user_toolbar_item,
                null);
        builder.setCancelable(true);
        builder.setView(dialogView);


        final android.app.AlertDialog dialog = builder.create();


        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        dialog.show();
    }

}
