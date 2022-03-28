package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class ThemeManager {

    @TargetApi(Build.VERSION_CODES.O)
    public static void lightStatusAndNavigationBar(Activity activity){

        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TypedValue a = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            int color = a.data;
            activity.getWindow().setStatusBarColor(color);
            activity.getWindow().setNavigationBarColor(color);
        } else {
            // windowBackground is not a color, probably a drawable
            Drawable d = activity.getResources().getDrawable(a.resourceId);
            int color;
            if (d instanceof ColorDrawable){
                color = ((ColorDrawable) d).getColor();
                activity.getWindow().setStatusBarColor(color);
                activity.getWindow().setNavigationBarColor(color);
            }
        }
    }

    public static void enableViaAndroidVersion(Activity activity){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //if android version of the device is over android Oreo or equal do your stuff..
            lightStatusAndNavigationBar(activity);
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //if android version of the device is over android Marshmallow or equal do your stuff..
            lightStatusBar(activity);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void lightStatusBar(Activity activity) {
        activity.getWindow().getDecorView().
                setSystemUiVisibility(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TypedValue a = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            int color = a.data;
            activity.getWindow().setStatusBarColor(color);
        } else {
            // windowBackground is not a color, probably a drawable
            Drawable d = activity.getResources().getDrawable(a.resourceId);
            int color;
            if (d instanceof ColorDrawable){
                color = ((ColorDrawable) d).getColor();
                activity.getWindow().setStatusBarColor(color);
            }
        }

    }


    /**
     * Theme color - colorPrimary -> Method to return the colorPrimary color of appTheme.
     * Detect every colorPrimary on each applied theme.
     * @param context: Activity context to apply theme
     * @return colorPrimary value
     */
    public static int themeColorPrimary(@NonNull final Context context){
        int attributeColor = R.attr.colorPrimary;
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (attributeColor, value, true);
        return value.data;
    }

    /**
     * Theme color - colorPrimaryDark -> Method to return the colorPrimaryDark color of appTheme.
     * Detect every colorPrimaryDark on each applied theme.
     * @param context: Activity context to apply theme
     * @return colorPrimaryDark value
     */
    public static int themeColorPrimaryDark(@NonNull final Context context){
        int attributeColor = R.attr.colorPrimaryDark;
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (attributeColor, value, true);
        return value.data;
    }

    /**
     * Theme color - colorPrimaryVariant -> Method to return the colorPrimaryVariant color of appTheme.
     * Detect every colorPrimaryVariant on each applied theme.
     * @param context: Activity context to apply theme
     * @return colorPrimaryVariant value
     */
    public static int themeColorGreen(@NonNull final Context context){
        int attributeColor = R.attr.colorPrimaryVariant;
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (attributeColor, value, true);
        return value.data;
    }

    /**
     * Theme color - colorSecondaryVariant -> Method to return the colorSecondaryVariant color of appTheme.
     * Detect every colorSecondaryVariant on each applied theme.
     * @param context: Activity context to apply theme
     * @return colorSecondaryVariant value
     */
    public static int themeColorRed(@NonNull final Context context){
        int attributeColor = R.attr.colorSecondaryVariant;
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (attributeColor, value, true);
        return value.data;
    }

    /**
     * Theme color - colorControlNormal -> Method to return the colorControlNormal color of appTheme.
     * Detect every colorControlNormal on each applied theme.
     * @param context: Activity context to apply theme
     * @return colorControlNormal value
     */
    public static int themeColorControlNormal(@NonNull final Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int color = ContextCompat.getColor(context, typedValue.resourceId);
        return color;
    }

}
