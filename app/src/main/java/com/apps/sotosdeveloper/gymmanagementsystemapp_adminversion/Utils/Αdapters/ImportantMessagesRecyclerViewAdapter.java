package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.HomeActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.SubActivities.PreviewMessageActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.AppConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.RecyclerViewUserSelected;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ImportantMessagesRecyclerViewAdapter extends
        RecyclerView.Adapter<ImportantMessagesRecyclerViewAdapter.MyViewHolder>{

    private final Activity activity;
    private final ArrayList<Message> messages;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView titleTV;
        private final TextView summaryTV;
        private final TextView toUserTV;
        private final RelativeLayout cardHolder;

        public MyViewHolder(View view){
            super(view);
            // init item in layout

            titleTV     = view.findViewById(R.id.title);
            summaryTV   = view.findViewById(R.id.summary);
            toUserTV    = view.findViewById(R.id.users_counter);
            cardHolder  = view.findViewById(R.id.item_holder);
        }

    }

    public ImportantMessagesRecyclerViewAdapter(Activity activity, ArrayList<Message> messages){
        this.activity = activity;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ImportantMessagesRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_vertical_recycler_view_item, parent, false);
        return new ImportantMessagesRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImportantMessagesRecyclerViewAdapter.MyViewHolder holder,
                                 int position) {

        Message message = messages.get(position);

        holder.titleTV.setText(message.getTitle());
        holder.summaryTV.setText(message.getSummary());

        holder.toUserTV.setText(String.format
                (Locale.ENGLISH,"+%d", message.getTo_users().size()));

        if(NetworkConnection.isOnline(activity)) cardClickListener(message.getID(), holder);
        else Toast.makeText(activity, "Please enable internet connection",
                Toast.LENGTH_SHORT).show();

    }

    private void cardClickListener(String id, ImportantMessagesRecyclerViewAdapter.MyViewHolder holder){
        Intent intentPreviewMessage = new Intent(activity, PreviewMessageActivity.class);
        intentPreviewMessage.putExtra(AppConstants.PREVIEW_MESSAGE_ACTIVITY_MESSAGE_ID_EXTRAS, id);
        holder.cardHolder.setOnClickListener(v -> activity.startActivity(intentPreviewMessage));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
