package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.RecyclerViewUserSelected;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.ThemeManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class UsersRecyclerViewAdapter extends
    RecyclerView.Adapter<UsersRecyclerViewAdapter.MyViewHolder>{

    private final Activity activity;
    private final ArrayList<User> users;
    public boolean[] clicked;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView    materialCardView;
        private final TextView            userTitleTextView;

        public MyViewHolder(View view) {
            super(view);
            userTitleTextView   = view.findViewById(R.id.recyclerViewAdapter_user);
            materialCardView    = view.findViewById(R.id.recyclerViewAdapter_cardView);
        }

    }

    public UsersRecyclerViewAdapter(Activity activity, ArrayList<User> users){
        this.activity   = activity;
        this.users      = users;
        clicked = new boolean[users.size()];
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_recycler_view_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int textColor = ThemeManager.themeColorControlNormal(activity);
        int backgroundColor = ThemeManager.themeColorPrimary(activity);

        holder.userTitleTextView.setText(String.format("%s %s",
                users.get(position).getFirstName(), users.get(position).getLastName()));

        holder.materialCardView.setOnClickListener(v -> {
            if (clicked[position]){
                holder.userTitleTextView.setTextColor(textColor);
                holder.materialCardView.setStrokeWidth(3);
                holder.materialCardView.setStrokeColor(textColor);
                clicked[position] = false;
            }else {
                RecyclerViewUserSelected recyclerViewUserSelected = new RecyclerViewUserSelected();
                recyclerViewUserSelected.setPosition(position);
                recyclerViewUserSelected.setUserID(users.get(position).getID());

                holder.userTitleTextView.setTextColor(backgroundColor);
                holder.materialCardView.setStrokeWidth(6);
                holder.materialCardView.setStrokeColor(backgroundColor);
                clicked[position] = true;

            }

        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }



}
