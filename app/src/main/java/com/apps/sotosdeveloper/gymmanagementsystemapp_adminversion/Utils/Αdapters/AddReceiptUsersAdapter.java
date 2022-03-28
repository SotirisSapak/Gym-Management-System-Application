package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;

import java.util.ArrayList;

public class AddReceiptUsersAdapter extends ArrayAdapter<User> {

    public AddReceiptUsersAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.add_receipt_listview_all_users_item, parent, false);
        }
        // init item in layout
        TextView tvUserName    = convertView.findViewById(R.id.addReceiptUserModelUserNameTV);
        tvUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

        return convertView;
    }
}
