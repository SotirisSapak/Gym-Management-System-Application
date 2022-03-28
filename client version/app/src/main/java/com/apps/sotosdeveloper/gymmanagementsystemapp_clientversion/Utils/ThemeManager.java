package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;

public class ThemeManager {


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
        int attributeColor = R.attr.colorSecondary;
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

    public static int themeLightColorForTabs(@NonNull final Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true);
        int color = ContextCompat.getColor(context, typedValue.resourceId);
        return color;
    }

    public static int themeBackgroundColor(@NonNull final Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = ContextCompat.getColor(context, typedValue.resourceId);
        return color;
    }

}
