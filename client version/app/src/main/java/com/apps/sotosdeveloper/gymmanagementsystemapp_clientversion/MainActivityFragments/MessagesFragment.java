package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Adapters.RecyclerViewMessagesAdapter;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.InitActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.ThemeManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private RecyclerViewMessagesAdapter adapter;

    private ArrayList<Message> allMessages;
    private ArrayList<Message> importantMessages;
    private ArrayList<Message> nonImportantMessages;

    private String userID;

    private MaterialCardView cardAll, cardImportant;

    private TextView noMessagesTV;
    private RelativeLayout noMessagesLayout;

    private SwipeRefreshLayout swipeRefreshLayout;

    private int selectedFilter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        init(view);

        userID = InitActivity.memoryHandler.getUserIDFromStorage();
        getAllMessagesFilterByUser(userID);
        changeMessagesState();
        setCategoryFilterAppearance();

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        return view;
    }

    private void init(View view){
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewMessages);

        cardAll = view.findViewById(R.id.allMessagesMCV);
        cardImportant = view.findViewById(R.id.importantMessagesMCV);

        recyclerView.setHasFixedSize(true);

        noMessagesTV = view.findViewById(R.id.no_message_textView_editable);
        noMessagesLayout = view.findViewById(R.id.layoutNoMessages);

        allMessages = new ArrayList<>();
        importantMessages = new ArrayList<>();
        nonImportantMessages = new ArrayList<>();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void changeMessagesState(){

        cardAll.setOnClickListener(v -> {
            if(allMessages.isEmpty()){
                // empty state image - no messages for all messages
                setEmptyStateLayout(true, 1);
            }else{
                setEmptyStateLayout(false, -1);
                adapter.setFilter(allMessages);
            }
            selectedFilter = 0;
            setCategoryFilterAppearance();
        });

        cardImportant.setOnClickListener(v -> {
            if (importantMessages.isEmpty()){
                // empty state image - no messages for important messages
                setEmptyStateLayout(true, 2);
            }else {
                setEmptyStateLayout(false, -1);
                adapter.setFilter(importantMessages);
            }
            selectedFilter = 1;
            setCategoryFilterAppearance();
        });
    }

    /**
     *
     * @param emptyState: if want empty state layout
     * @param mode:     1: MODE_ALL_MESSAGES
     *                  2: MODE_IMPORTANT_MESSAGES
     *              other: MODE_UNDEFINED
     */
    private void setEmptyStateLayout(boolean emptyState, int mode){
        if(emptyState){
            noMessagesLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            // check for mode
            switch (mode){
                case 1:
                    // MODE_ALL_MESSAGES
                    noMessagesTV.setText(requireActivity().getResources().getString(R.string.there_are_no_messages));
                    break;
                case 2:
                    // MODE_IMPORTANT_MESSAGES
                    noMessagesTV.setText(requireActivity().getResources().getString(R.string.there_are_no_important_messages));
                    break;
                default:
                    // MODE_UNDEFINED
                    Log.e("NO_MESSAGES_ERROR", "MODE UNDEFINED");
            }
        }else{
            noMessagesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getAllMessagesFilterByUser(String userID){
        final int[] totalMessages = {0};

        CollectionReference collectionReference = firebaseFirestore.
                collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES);

        collectionReference.
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: task.getResult()){

                            DocumentReference documentReference = collectionReference.document(document.getId());
                            documentReference.get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    DocumentSnapshot document1 = task1.getResult();
                                    assert document1 != null;
                                    if (document1.exists()) {
                                        Map<String, Boolean> users = (Map<String, Boolean>)
                                                document1.get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TO_USER);
                                        // at this point we have all users for this message
                                        for (String user: users.keySet()){
                                            if (user.equalsIgnoreCase(userID)){
                                                //found user!!!
                                                Message tempMessage = new Message();
                                                tempMessage.setID(document.getId());
                                                tempMessage.setTitle(document1.
                                                        get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE).toString());
                                                tempMessage.setDate(document1.
                                                        get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_DATE).toString());
                                                tempMessage.setImportantMessage((Boolean) document1.
                                                        get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGE_IS_IMPORTANT));
                                                tempMessage.setSummary(document1.
                                                        get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY).toString());
                                                allMessages.add(tempMessage);
                                                if(tempMessage.isImportantMessage()){
                                                    importantMessages.add(tempMessage);
                                                }else{
                                                    nonImportantMessages.add(tempMessage);
                                                }
                                                totalMessages[0]++;
                                            }
                                        }
                                    }
                                    // check if arrayList is empty or not
                                    if (allMessages.isEmpty()){
                                        Toast.makeText(getContext(), "Not any message found", Toast.LENGTH_SHORT).show();
                                        setEmptyStateLayout(true, 1);
                                    }
                                    else{
                                        setEmptyStateLayout(false, -1);
                                        adapter = new RecyclerViewMessagesAdapter(getActivity(), allMessages);
                                        recyclerView.setAdapter(adapter);
                                    }
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }
                });
    }

    private void setCategoryFilterAppearance(){
        if(selectedFilter == 0){
            cardAll.setCardBackgroundColor
                    (ThemeManager.themeLightColorForTabs(requireActivity()));
            cardImportant.setCardBackgroundColor(
                    ThemeManager.themeBackgroundColor(requireActivity()));
            cardAll.setCardElevation(6f);
            cardImportant.setCardElevation(0f);
        }else if(selectedFilter == 1){
            cardAll.setCardBackgroundColor
                    (ThemeManager.themeBackgroundColor(requireActivity()));
            cardImportant.setCardBackgroundColor(
                    ThemeManager.themeLightColorForTabs(requireActivity()));
            cardAll.setCardElevation(0f);
            cardImportant.setCardElevation(6f);
        }
    }

    public void refresh(){
        allMessages.clear();
        importantMessages.clear();
        recyclerView.invalidate();
        recyclerView.removeAllViews();
        selectedFilter = 0;
        setCategoryFilterAppearance();
        getAllMessagesFilterByUser(userID);
    }

}