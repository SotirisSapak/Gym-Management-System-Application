package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities.PreferencesActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments.DashboardFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments.MessagesFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments.OpeningHoursFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments.ReceiptViewerFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs.AlertDialogCreateMessage;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs.AlertDialogCreateReceipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs.AlertDialogUserToolbarIcon;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.AppDataHandler;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import hari.bounceview.BounceView;

public class HomeActivity extends AppCompatActivity {

    private AppDataHandler appDataHandler;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private int selectedTab = R.id.navigation_dashboard;

    @SuppressLint("StaticFieldLeak")
    public static ReceiptViewerFragment receiptViewerFragment;
    @SuppressLint("StaticFieldLeak")
    public static MessagesFragment messagesFragment;
    @SuppressLint("StaticFieldLeak")
    public static DashboardFragment dashboardFragment;

    private ExtendedFloatingActionButton fabCreate;

    // for preferences
    private SharedPreferences preferences;

    private ImageButton userToolbarIcon, preferencesToolbarIcon, logOutToolbarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        userToolbarIcon.setOnClickListener(v -> AlertDialogUserToolbarIcon.openDialog(this));

        Intent settingsIntent = new Intent(this, PreferencesActivity.class);
        preferencesToolbarIcon.setOnClickListener(v -> startActivity(settingsIntent));

        Intent loginActivityIntent =
                new Intent(HomeActivity.this, LoginActivity.class);
        logOutToolbarIcon.setOnClickListener(v -> {
            appDataHandler.logout();
            startActivity(loginActivityIntent);
            finish();
        });

        createTabs();

        BounceView.addAnimTo(fabCreate);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(preferences.getBoolean("preference_admin_options_dashboard",
                false)){
            // hide fab
            fabCreate.hide();
        }else{
            selectedTab = R.id.navigation_receipts;
            fabCreate.setText(R.string.add_new_receipt);
        }

        handleFabClickListener();
        viewPagerScrollListener();

    }

    private void init(){
        userToolbarIcon = findViewById(R.id.toolbarUserIcon);
        preferencesToolbarIcon = findViewById(R.id.toolbarSettingsIcon);
        logOutToolbarIcon = findViewById(R.id.toolbarLogout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appDataHandler = InitActivity.appDataHandler;

        fabCreate = findViewById(R.id.fab_create);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    }

    private void createTabs(){
        // tab for dashboard - check preferences for visibility
        if(preferences.getBoolean("preference_admin_options_dashboard", false)){
            dashboardFragment = new DashboardFragment();
            viewPagerAdapter.addFrag(dashboardFragment,
                    getResources().getString(R.string.title_dashboard));
        }
        // tab for receipts
        receiptViewerFragment = new ReceiptViewerFragment();
        viewPagerAdapter.addFrag(receiptViewerFragment,
                getResources().getString(R.string.all_receipts));
        // tab for messages
        messagesFragment = new MessagesFragment();
        viewPagerAdapter.addFrag(messagesFragment,
                getResources().getString(R.string.messages));
        // tab for opening hours
        OpeningHoursFragment openingHoursFragment = new OpeningHoursFragment();
        viewPagerAdapter.addFrag(openingHoursFragment,
                getResources().getString(R.string.opening_hours));

        // add adapter to viewpager
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
    }

    private void handleFabClickListener(){
        fabCreate.setOnClickListener(v -> {
            // set different fab states
            if(selectedTab == R.id.navigation_receipts){
                AlertDialogCreateReceipt.openAlertDialog(this);
            }
            if(selectedTab == R.id.navigation_messages) {
                AlertDialogCreateMessage.openAlertDialog(this);
            }
        });
    }

    private void viewPagerScrollListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    selectedTab = R.id.navigation_dashboard;
                }
                if(position == viewPagerAdapter.getFragmentPosition(R.id.navigation_receipts)) {
                    selectedTab = R.id.navigation_receipts;
                }
                if(position == viewPagerAdapter.getFragmentPosition(R.id.navigation_messages)) {
                    selectedTab = R.id.navigation_messages;
                }
                if(position == viewPagerAdapter.getFragmentPosition(R.id.navigation_opening_hours)) {
                    selectedTab = R.id.navigation_opening_hours;
                }

                if( selectedTab == R.id.navigation_dashboard ||
                        selectedTab == R.id.navigation_opening_hours){
                    fabCreate.hide();
                } else {
                    if(selectedTab == R.id.navigation_receipts) fabCreate.setText(R.string.add_new_receipt);
                    if(selectedTab == R.id.navigation_messages) fabCreate.setText(R.string.add_message);
                    fabCreate.show();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (selectedTab != R.id.navigation_receipts){
            viewPager.setCurrentItem(1);
            selectedTab = R.id.navigation_receipts;
        }else{
            super.onBackPressed();
        }
    }

}