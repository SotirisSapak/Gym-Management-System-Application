package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments.HomeFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments.MessagesFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments.OpeningHoursFragment;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.ViewPagerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static FirebaseApp app;
    private FirebaseFirestore firebaseFirestore;
    private int selectedTab = 0;
    private ViewPager viewPager;

    private HomeFragment homeFragment;
    private MessagesFragment messagesFragment;
    private OpeningHoursFragment openingHoursFragment;

    private SmartTabLayout tabLayout;

    private TextView toolbarTitle;
    private ImageButton userButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = FirebaseApp.initializeApp(this);

        init();

        userButton.setOnClickListener(v->
                logOut());

        setToolbarUserName();
        setUpViewPagerAndTabLayout();
        changeSelectedTab();

    }

    private void logOut() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        InitActivity.memoryHandler.removeUserFromDatabase();
        startActivity(loginActivityIntent);
        finish();
    }

    private void setToolbarUserName() {
        String currentUserID = InitActivity.memoryHandler.getUserIDFromStorage();

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                            if(document.getId().equalsIgnoreCase(currentUserID)){
                                // get first name and last name from this account
                                String firstName = Objects.requireNonNull(
                                        document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME))
                                        .toString();
                                String lastName = Objects.requireNonNull(
                                        document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME))
                                        .toString();
                                toolbarTitle.setText(String.format("%s %s", firstName, lastName));
                            }
                        }
                    }
                });
    }

    private void init(){
        toolbarTitle = findViewById(R.id.title);
        userButton = findViewById(R.id.toolbarDashboardIcon);

        firebaseFirestore = FirebaseFirestore.getInstance();
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
    }

    private void setUpViewPagerAndTabLayout(){
        ViewPagerAdapter adapter    = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment                = new HomeFragment();
        messagesFragment            = new MessagesFragment();
        openingHoursFragment        = new OpeningHoursFragment();
        // add fragments
        adapter.addFrag(homeFragment,          "Receipts");
        adapter.addFrag(openingHoursFragment,  "Opening hours");
        adapter.addFrag(messagesFragment,      "Messages");
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
    }

    private void changeSelectedTab(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectedTab = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}