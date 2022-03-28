package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class ReceiptsAdapter extends ArrayAdapter<Receipt> {

    private Activity activity;

    public ReceiptsAdapter(@NonNull Context context, ArrayList<Receipt> receipts, Activity activity) {
        super(context, 0, receipts);
        this.activity = activity;
    }

    public ReceiptsAdapter(@NonNull Context context){
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Receipt receipt = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.receipts_listview_item, parent, false);
        }

        // init item in layout
        TextView tvReceiptID        = convertView.findViewById(R.id.listViewAdapter_title);
        TextView tvDate             = convertView.findViewById(R.id.listViewAdapter_date);
        TextView tvToUser           = convertView.findViewById(R.id.listViewAdapter_toUser);
        TextView tvAmount           = convertView.findViewById(R.id.listviewAdapter_amount);
        RelativeLayout rlAmount     = convertView.findViewById(R.id.layoutReceiptAmount);
        ImageView imagePaid         = convertView.findViewById(R.id.listviewAdapter_iconPaid);

        final String[] firstName = new String[1];
        final String[] lastName = new String[1];

        // in database only a user id will be saved
        // so, extra code to find first and last name of this user id.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String id = document.getId();
                            if(id.equalsIgnoreCase(receipt.getToUser())){
                                firstName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME).
                                        toString();
                                lastName[0] = document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME).
                                        toString();
                                tvToUser.setText(String.format("For %s %s", firstName[0], lastName[0]));
                            }
                        }

                    }
                });

        // set data to items
        tvReceiptID.setText(receipt.getReceiptID());
        tvDate.setText(receipt.getDate());

        // for amount check if is paid
        tvAmount.setText(String.format("%s€", receipt.getAmount()));
        setAmountColorAndBackground(this.activity, receipt, tvAmount, rlAmount, imagePaid);

        return convertView;
    }

    private void setAmountColorAndBackground(Activity activity, Receipt receipt, TextView amountTV, RelativeLayout layout, ImageView imageIcon){
        if(receipt.isReceiptPaid()){
            amountTV.setTextColor(activity.getResources().getColor(R.color.receipt_paid_green));
            layout.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corners_receipt_paid_layout));
            imageIcon.setVisibility(View.VISIBLE);
        }else{
            amountTV.setTextColor(activity.getResources().getColor(R.color.receipt_not_paid_red));
            layout.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corners_receipt_not_paid_layout));
            imageIcon.setVisibility(View.GONE);
        }
    }

}
