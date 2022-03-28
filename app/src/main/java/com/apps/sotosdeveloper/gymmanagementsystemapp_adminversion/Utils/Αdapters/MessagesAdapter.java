package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private Activity activity;

    public MessagesAdapter(@NotNull Context context, ArrayList<Message> messages, Activity activity){
        super(context, 0, messages);
        this.activity = activity;
    }

    public MessagesAdapter(@NonNull Context context){
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.messages_listview_item, parent, false);
        }

        // init items from layout
        TextView titleTV    = convertView.findViewById(R.id.listViewAdapterMessages_title);
        TextView summaryTV  = convertView.findViewById(R.id.listViewAdapterMessages_summary);
        TextView toUserTV   = convertView.findViewById(R.id.listViewAdapterMessages_toUser);
        TextView dateTV     = convertView.findViewById(R.id.listViewAdapterMessages_date);

        final String[] firstName = new String[1];
        final String[] lastName = new String[1];

        ArrayList<String> allUsers = new ArrayList<>();
        allUsers.addAll(message.getTo_users().keySet());

        StringBuilder stringBuilder = new StringBuilder();

        for (String ids: allUsers){
            // in database only a user id will be saved
            // so, extra code to find first and last name of this user id.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                    get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String id = document.getId();
                        if(id.equalsIgnoreCase(ids)){
                            firstName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME).
                                    toString();
                            lastName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME).
                                    toString();

                            if(allUsers.size() >= 2){
                                toUserTV.setText(String.format("For: %s %s (+%d more)",
                                        firstName[0], lastName[0], allUsers.size() - 1));
                            }else{
                                toUserTV.setText(String.format("For: %s %s",
                                        firstName[0], lastName[0]));
                            }
                        }
                    }

                }
            });
        }


        titleTV.setText(message.getTitle());
        summaryTV.setText(message.getSummary());
        dateTV.setText(message.getDate());

        return convertView;
    }
}
