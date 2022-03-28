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
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
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
import java.util.concurrent.atomic.AtomicInteger;

import hari.bounceview.BounceView;

public class MessagesRecyclerViewAdapter extends
        RecyclerView.Adapter<MessagesRecyclerViewAdapter.MyViewHolder>{

    private final Activity activity;
    private ArrayList<Message> messages;
    private ArrayList<Message> filteredMessages;
    private final boolean isImportant;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView titleTV;
        private final TextView summaryTV;
        private final TextView dateTV;
        private final TextView toUserTV;
        private final ImageView importantIV;
        private final MaterialCardView cardHolder;

        public MyViewHolder(View view){
            super(view);
            // init item in layout

            titleTV     = view.findViewById(R.id.recyclerViewAdapterMessages_title);
            summaryTV   = view.findViewById(R.id.recyclerViewAdapterMessages_summary);
            dateTV      = view.findViewById(R.id.recyclerViewAdapterMessages_date);
            importantIV = view.findViewById(R.id.ic_important);
            toUserTV    = view.findViewById(R.id.users_counter);
            cardHolder  = view.findViewById(R.id.item_holder);
        }

    }

    public MessagesRecyclerViewAdapter(Activity activity, ArrayList<Message> messages){
        this.activity = activity;
        this.messages = messages;
        isImportant = false;
    }

    public MessagesRecyclerViewAdapter(Activity activity, ArrayList<Message> messages,
                                       boolean isImportant){
        this.activity = activity;
        this.messages = messages;
        this.isImportant = isImportant;
    }

    @NonNull
    @Override
    public MessagesRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_recycler_view_item, parent, false);
        return new MessagesRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerViewAdapter.MyViewHolder holder,
                                 int position) {

        Message message = messages.get(position);

        holder.titleTV.setText(message.getTitle());
        holder.summaryTV.setText(message.getSummary());
        holder.dateTV.setText(message.getDate());

        if(!isImportant){
            if(message.isImportantMessage()){
                holder.importantIV.setVisibility(View.VISIBLE);
            }else{
                holder.importantIV.setVisibility(View.GONE);
            }
        }else{
            holder.importantIV.setVisibility(View.GONE);
        }

        holder.toUserTV.setText(String.format
                (Locale.ENGLISH,"+%d", message.getTo_users().size()));

        if(NetworkConnection.isOnline(activity)) cardClickListener(message.getID(), holder);
        else Toast.makeText(activity, "Please enable internet connection",
                Toast.LENGTH_SHORT).show();


        if(NetworkConnection.isOnline(activity)) cardOnLongClickListener(holder);
        else Toast.makeText(activity, "Please enable internet connection",
                Toast.LENGTH_SHORT).show();

    }

    public void setFilter(ArrayList<Message> filteredMessages){
        messages = new ArrayList<>();
        messages.addAll(filteredMessages);
        notifyDataSetChanged();
    }

    private void cardClickListener(String id, MyViewHolder holder){
        Intent intentPreviewMessage = new Intent(activity, PreviewMessageActivity.class);
        intentPreviewMessage.putExtra(AppConstants.PREVIEW_MESSAGE_ACTIVITY_MESSAGE_ID_EXTRAS, id);
        holder.cardHolder.setOnClickListener(v -> activity.startActivity(intentPreviewMessage));
    }

    private void cardOnLongClickListener(MyViewHolder holder){
        holder.cardHolder.setOnLongClickListener(v -> {
            alertDialogMoreOptions(holder.getLayoutPosition());
            return true;
        });
    }

    private void alertDialogMoreOptions(int position){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_message_more_options,
                null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.messageTitleAlertDialog);
        title.setText(messages.get(position).getTitle());

        RelativeLayout editMessage = dialogView.findViewById(R.id.relativeEditMessage);
        RelativeLayout deleteMessage = dialogView.findViewById(R.id.relativeDeleteMessage);
        MaterialButton cancelBT = dialogView.findViewById(R.id.alertCancel);

        final android.app.AlertDialog dialog = builder.create();

        editMessage.setOnClickListener(v -> {
            if(NetworkConnection.isOnline(activity)){
                dialog.dismiss();
                alertDialogEditMessageInfo(position);
            }else{
                Toast.makeText(activity, "Please enable internet connection",
                        Toast.LENGTH_SHORT).show();
            }

        });
        deleteMessage.setOnClickListener(v -> {
            if(NetworkConnection.isOnline(activity)){
                firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES).
                        document(messages.get(position).getID()).
                        delete().
                        addOnSuccessListener(aVoid -> Snackbar.make(v, "Message deleted successfully!", Snackbar.LENGTH_SHORT)
                                .show()).addOnFailureListener(e -> Snackbar.make(v, "Fail to delete message!", Snackbar.LENGTH_SHORT)
                        .show());

                dialog.dismiss();
                HomeActivity.messagesFragment.refreshList();
            }else{
                Toast.makeText(activity, "Please enable internet connection", Toast.LENGTH_SHORT).show();
            }

        });

        cancelBT.setOnClickListener(v -> dialog.dismiss());

        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        dialog.show();
    }

    private void alertDialogEditMessageInfo(int position){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_edit_message_option,
                null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        TextInputEditText titleEditText = dialogView.findViewById(R.id.messageTitleTIET);
        TextInputEditText summaryEditText = dialogView.findViewById(R.id.messageSummaryTIET);

        // init input
        final String[] title = new String[1];
        final String[] summary = new String[1];
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES).
                document(messages.get(position).getID()).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        title[0] = Objects.requireNonNull(Objects.requireNonNull
                                (task.getResult()).get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE))
                                .toString();
                        summary[0] = Objects.requireNonNull(Objects.requireNonNull
                                (task.getResult()).get(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY))
                                .toString();
                        titleEditText.setText(title[0]);
                        summaryEditText.setText(summary[0]);
                    }
                });



        MaterialButton saveBT   = dialogView.findViewById(R.id.save_action);
        MaterialButton cancelBT = dialogView.findViewById(R.id.cancel_action);

        final android.app.AlertDialog dialog = builder.create();

        Map<String, Object> updatableParameters = new HashMap<>();

        saveBT.setOnClickListener(v -> {
            if(NetworkConnection.isOnline(activity)){
                if(verifyUpdateMessageInputs(titleEditText, summaryEditText)){
                    CollectionReference reference = firebaseFirestore.
                            collection(DatabaseConstants.FIREBASE_COLLECTION_MESSAGES);
                    DocumentReference document = reference.document(messages.get(position).getID());
                    updatableParameters.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_TITLE,
                            titleEditText.getEditableText().toString());
                    updatableParameters.put(DatabaseConstants.FIREBASE_DOCUMENT_MESSAGES_SUMMARY,
                            summaryEditText.getEditableText().toString());
                    document.update(updatableParameters).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) Toast.makeText(activity.getApplicationContext(),
                                "Message info updated successfully",
                                Toast.LENGTH_SHORT).show();
                        else Toast.makeText(activity.getApplicationContext(),
                                "Cannot update message info",
                                Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        HomeActivity.messagesFragment.refreshList();
                    });

                }
            }
        });

        cancelBT.setOnClickListener(v -> dialog.dismiss());

        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        dialog.show();
    }

    private boolean verifyUpdateMessageInputs(TextInputEditText title, TextInputEditText body){
        if(title.getText() == null || title.getEditableText().toString().equals("")) return false;
        return body.getText() != null && !body.getEditableText().toString().equals("");
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
