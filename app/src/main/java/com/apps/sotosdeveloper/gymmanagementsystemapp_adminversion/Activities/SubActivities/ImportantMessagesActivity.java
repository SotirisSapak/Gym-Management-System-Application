package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.ImportantMessagesRecyclerViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ImportantMessagesActivity extends AppCompatActivity {

    private ArrayList<Message> messages;
    private ImportantMessagesRecyclerViewAdapter messagesAdapter;

    private RecyclerView itemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).
                setTitle(getResources().getString(R.string.only_important));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        initComponents();
        getImportantMessages();
    }

    private void initComponents() {
        messages = new ArrayList<>();

        itemsRecyclerView = findViewById(R.id.recyclerViewImportantMessages);
        itemsRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false);
        itemsRecyclerView.setLayoutManager(layoutManager);
    }

    private void getImportantMessages(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        messages = new ArrayList<>();

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES).
                get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Message tempMessage;

                    tempMessage = document.toObject(Message.class);
                    tempMessage.setID(document.getId());
                    tempMessage.setImportantMessage((Boolean)
                            document.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_IS_IMPORTANT));
                    tempMessage.setTitle((String) document.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE));
                    tempMessage.setSummary((String) document.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY));
                    tempMessage.setDate((String) document.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_DATE));
                    tempMessage.setTo_user((Map<String, Boolean>)
                            document.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TO_USER));

                    if(tempMessage.isImportantMessage()){
                        messages.add(tempMessage);
                    }

                }

                messagesAdapter = new ImportantMessagesRecyclerViewAdapter(this, messages);
                itemsRecyclerView.setAdapter(messagesAdapter);

            }else{
                Log.w(TAG, "Error getting message documents.", task.getException());
            }
        });
    }
}