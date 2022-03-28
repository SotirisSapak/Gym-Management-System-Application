package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;

import java.util.Objects;

public class PreferencesActivity extends AppCompatActivity {

    private static boolean requestRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            if(requestRefresh){
                alertDialogRequestRefresh();
            }else{
                super.onBackPressed();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MySettingsFragment())
                .commit();

    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @Override
    public void onBackPressed() {
        if(requestRefresh){
            alertDialogRequestRefresh();
        }else{
            super.onBackPressed();
        }
    }

    private void alertDialogRequestRefresh(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart...");
        builder.setMessage("The changes you made require app restart");
        builder.setPositiveButton("Restart now",
                (dialog, which) -> triggerRebirth(getApplicationContext()));
        builder.setCancelable(false);
        builder.create().show();
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            EditTextPreference signaturePreference =
                    findPreference("preference_messages_auto_signature");
            assert signaturePreference != null;
            signaturePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if(newValue == null || newValue.toString().equalsIgnoreCase("")){
                    Toast.makeText(requireContext(), "Cannot set empty signature",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    return true;
                }
            });

            SwitchPreference signatureEnableDisablePreference =
                    findPreference("preference_messages_enable_disable_signature");
            assert signatureEnableDisablePreference != null;
            signatureEnableDisablePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean value = (Boolean) newValue;
                // if user enable signature feature, enable signature preference. Disable, otherwise.
                signaturePreference.setEnabled(value);
                return true;
            });

            // check if user enable or disable message signature to change the state of the option
            signaturePreference.setEnabled(signatureEnableDisablePreference.isChecked());

            // for dashboard ----------------------
            SwitchPreference dashboardPreference = findPreference("preference_admin_options_dashboard");
            assert dashboardPreference      != null;

            dashboardPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                requestRefresh = true;
                return true;
            });

        }
    }
}