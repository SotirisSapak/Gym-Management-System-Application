package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.AppConstants;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class PreviewMessageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText title, body;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_message);

        initUI();

        // toolbar functionality
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.preview_message));
        toolbar.setNavigationOnClickListener(v -> finish());

        // set all data to panel
        setData();

    }

    private void initUI(){
        toolbar = findViewById(R.id.toolbar);

        title = findViewById(R.id.previewMessageTitleTIET);
        body = findViewById(R.id.previewMessageBodyTIET);
        date = findViewById(R.id.previewMessageDateTV);
    }

    private void setData(){
        // get data from recycler view item
        String titleString;
        String bodyString;
        String dateString;

        Bundle extras = getIntent().getExtras();
        titleString = extras.getString(AppConstants.PREVIEW_MESSAGE_EXTRAS_TITLE);
        bodyString = extras.getString(AppConstants.PREVIEW_MESSAGE_EXTRAS_BODY);
        dateString = extras.getString(AppConstants.PREVIEW_MESSAGE_EXTRAS_DATE);

        // set these date to panel
        title.setText(titleString);
        body.setText(bodyString);
        date.setText(dateString);
    }

}