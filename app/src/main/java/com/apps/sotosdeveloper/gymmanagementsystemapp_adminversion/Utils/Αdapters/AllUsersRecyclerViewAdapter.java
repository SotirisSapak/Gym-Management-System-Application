package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.AlertDialogs.AlertDialogCreateReceipt;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class AllUsersRecyclerViewAdapter
        extends RecyclerView.Adapter<AllUsersRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<User> users;
    public int selectedUserPos = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView firstNameTV;
        private final TextView lastNameTV;
        private final MaterialCardView card;

        public MyViewHolder(View view){
            super(view);
            firstNameTV         = view.findViewById(R.id.allUsersRecyclerView_firstName);
            lastNameTV          = view.findViewById(R.id.allUsersRecyclerView_lastName);
            card                = view.findViewById(R.id.cardHolder);
        }

    }

    public AllUsersRecyclerViewAdapter(ArrayList<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_users_recycler_view_item, parent, false);
        return new AllUsersRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.firstNameTV.setText(users.get(position).getFirstName());
        holder.lastNameTV.setText(users.get(position).getLastName());

        holder.card.setOnClickListener(v -> {
            selectedUserPos = position;
            AlertDialogCreateReceipt.selectedUserTV.setText(String.format("%s %s",
                    holder.firstNameTV.getText(), holder.lastNameTV.getText()));
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
