package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.AppConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects.Message;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.PreviewMessageActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;

import java.util.ArrayList;

public class RecyclerViewMessagesAdapter extends
        RecyclerView.Adapter<RecyclerViewMessagesAdapter.MyViewHolder>{

    private final Activity activity;
    private ArrayList<Message> messages;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout cardHolder;
        private final TextView title;
        private final TextView summary;
        private final TextView date;
        private final ImageView favourite;

        public MyViewHolder(View view){
            super(view);

            cardHolder = view.findViewById(R.id.item_holder);
            title = view.findViewById(R.id.item_title);
            date = view.findViewById(R.id.item_date);
            summary = view.findViewById(R.id.item_summary);
            favourite = view.findViewById(R.id.item_image_favourite);
        }

    }

    public RecyclerViewMessagesAdapter(Activity activity, ArrayList<Message> messages){
        this.activity = activity;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerViewMessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_message_item, parent, false);
        return new RecyclerViewMessagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMessagesAdapter.MyViewHolder holder, int position) {

        holder.title.setText(messages.get(position).getTitle());
        holder.date.setText(messages.get(position).getDate());
        holder.summary.setText(messages.get(position).getSummary());

        cardClickListener(holder, messages.get(position).getTitle(), messages.get(position).getSummary(),
                messages.get(position).getDate());

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);

        if(messages.get(position).isImportantMessage()) holder.favourite.setVisibility(View.VISIBLE);
        else holder.favourite.setVisibility(View.GONE);

    }

    public void setFilter(ArrayList<Message> filteredMessages){
        messages = new ArrayList<>();
        messages.addAll(filteredMessages);
        notifyDataSetChanged();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void cardClickListener(RecyclerViewMessagesAdapter.MyViewHolder holder, String title,
                                   String body, String date){
        Intent intentPreviewMessage = new Intent(activity, PreviewMessageActivity.class);

        intentPreviewMessage.putExtra(AppConstants.PREVIEW_MESSAGE_EXTRAS_TITLE, title);
        intentPreviewMessage.putExtra(AppConstants.PREVIEW_MESSAGE_EXTRAS_BODY, body);
        intentPreviewMessage.putExtra(AppConstants.PREVIEW_MESSAGE_EXTRAS_DATE, date);

        holder.cardHolder.setOnClickListener(v -> activity.startActivity(intentPreviewMessage));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
