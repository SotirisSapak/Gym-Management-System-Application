package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities.ImportantMessagesActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.MessagesRecyclerViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import static android.content.ContentValues.TAG;

public class MessagesFragment extends Fragment {

    public MessagesFragment() {}

    private RecyclerView recyclerViewMessages;
    private RecyclerView recyclerViewImportantMessages;

    private MessagesRecyclerViewAdapter messagesAdapter;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<Message> messages;
    private ArrayList<Message> importantMessages;

    // to connect with AddMessageActivity
    public static ArrayList<String> allUserIds;


    private RelativeLayout layoutImportantCategory;
    private RelativeLayout layoutMessages;
    private RelativeLayout layoutNoMessagesState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        init(view);
        getAllMessages();

        Intent intentImportantMessages = new Intent(getActivity(), ImportantMessagesActivity.class);
        layoutImportantCategory.setOnClickListener(v -> startActivity(intentImportantMessages));

        return view;
    }

    private void init(View view){
        layoutImportantCategory = view.findViewById(R.id.layoutImportantCategory);
        layoutNoMessagesState   = view.findViewById(R.id.relativeNoMessages);
        layoutMessages          = view.findViewById(R.id.layoutMessages);

        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        recyclerViewImportantMessages = view.findViewById(R.id.recyclerViewImportantMessages);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewMessages.setHasFixedSize(false);
        recyclerViewImportantMessages.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMessages.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManager1 =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImportantMessages.setLayoutManager(layoutManager1);

        messages = new ArrayList<>();
        importantMessages = new ArrayList<>();
        allUserIds = new ArrayList<>();
    }

    private void layoutStates(){
        if(messages.isEmpty()){
            // no messages here
            layoutMessages.setVisibility(View.GONE);
            layoutImportantCategory.setVisibility(View.GONE);
            layoutNoMessagesState.setVisibility(View.VISIBLE);
        }
        else{
            // there are some messages
            if(importantMessages.isEmpty()){
                // but no important messages
                layoutMessages.setVisibility(View.VISIBLE);
                layoutImportantCategory.setVisibility(View.GONE);
                layoutNoMessagesState.setVisibility(View.GONE);
            }else {
                layoutMessages.setVisibility(View.VISIBLE);
                layoutImportantCategory.setVisibility(View.VISIBLE);
                layoutNoMessagesState.setVisibility(View.GONE);
            }
        }
    }

    public void refreshList(){
        recyclerViewMessages.invalidate();
        recyclerViewImportantMessages.invalidate();
        messagesAdapter.notifyDataSetChanged();
        messages.clear();
        importantMessages.clear();

        allUserIds.clear();
        getAllMessages();
    }

    private void getAllMessages(){
        firebaseFirestore = FirebaseFirestore.getInstance();

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

                            messages.add(tempMessage);
                            if(tempMessage.isImportantMessage()){
                                importantMessages.add(tempMessage);
                            }

                        }

                        if (messages.isEmpty()){
                            messagesAdapter = new MessagesRecyclerViewAdapter(getActivity(), null);
                        }else {
                            messagesAdapter = new MessagesRecyclerViewAdapter(getActivity(),
                                    messages, false);
                            recyclerViewMessages.setAdapter(messagesAdapter);

                            if(importantMessages.size() > 5){
                                for (int i=importantMessages.size(); i>5; i--){
                                    importantMessages.remove(i-1);
                                }
                            }
                            messagesAdapter = new MessagesRecyclerViewAdapter(getActivity(),
                                    importantMessages, true);
                            recyclerViewImportantMessages.setAdapter(messagesAdapter);
                        }

                        layoutStates();

                    }else{
                        Log.w(TAG, "Error getting message documents.", task.getException());
                    }
                });

    }

}