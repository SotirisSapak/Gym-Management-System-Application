package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import android.app.Activity;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Activities.HomeActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.NetworkConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ReceiptsRecyclerViewAdapter
        extends RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.MyViewHolder>{

    private final Activity activity;
    private ArrayList<Receipt> receipts;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        // init item in layout
        private final TextView tvReceiptID;
        private final TextView tvDate;
        private final TextView tvToUser;
        private final TextView tvAmount;
        private final RelativeLayout rlAmount;
        private final ImageView imagePaid;
        private final RelativeLayout cardReceiptMCV;

        public MyViewHolder(View view){
            super(view);
            // init item in layout
            tvReceiptID         = view.findViewById(R.id.listViewAdapter_title);
            tvDate              = view.findViewById(R.id.listViewAdapter_date);
            tvToUser            = view.findViewById(R.id.listViewAdapter_toUser);
            tvAmount            = view.findViewById(R.id.listviewAdapter_amount);
            rlAmount            = view.findViewById(R.id.layoutReceiptAmount);
            imagePaid           = view.findViewById(R.id.listviewAdapter_iconPaid);
            cardReceiptMCV      = view.findViewById(R.id.cardReceiptRL);
        }

    }

    public ReceiptsRecyclerViewAdapter(Activity activity, ArrayList<Receipt> receipts){
        this.activity = activity;
        this.receipts = receipts;
    }

    @NonNull
    @Override
    public ReceiptsRecyclerViewAdapter.MyViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_recycler_view_item, parent, false);
        return new ReceiptsRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Receipt receipt = receipts.get(position);

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
                        holder.tvToUser.setText(String.format("For %s %s", firstName[0], lastName[0]));
                    }
                }

            }
        });

        // set data to items
        holder.tvReceiptID.setText(receipt.getReceiptID());
        holder.tvDate.setText(receipt.getDate());

        // for amount check if is paid
        holder.tvAmount.setText(String.format("%s€", receipt.getAmount()));
        setAmountColorAndBackground(this.activity, receipt,
                holder.rlAmount, holder.imagePaid);

        holder.cardReceiptMCV.setOnClickListener(v ->
                alertDialogShowMoreReceiptOptions(receipts.get(position).getReceiptAutoCreatedID(),
                receipts.get(position).getReceiptID()));
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    private void setAmountColorAndBackground(Activity activity,
                                             Receipt receipt,
                                             RelativeLayout layout, ImageView imageIcon){
        if(receipt.isReceiptPaid()){
            layout.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.rounded_corners_receipt_paid_layout));
            imageIcon.setVisibility(View.VISIBLE);
        }else{
            layout.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.rounded_corners_receipt_not_paid_layout));
            imageIcon.setVisibility(View.GONE);
        }
    }

    public void setFilter(ArrayList<Receipt> filteredMessages){
        receipts = new ArrayList<>();
        receipts.addAll(filteredMessages);
        notifyDataSetChanged();
    }

    public void alertDialogShowMoreReceiptOptions(String firebaseID, String receiptTitle){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.listview_receipt_more_options,
                null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.alertDialogReceipt_receiptID);
        title.setText(receiptTitle);


        MaterialButton cancelBT = dialogView.findViewById(R.id.cancel_action);

        RelativeLayout checkRL = dialogView.findViewById(R.id.relativeCheckReceipt);
        RelativeLayout deleteRL = dialogView.findViewById(R.id.relativeDeleteReceipt);

        final android.app.AlertDialog dialog = builder.create();

        checkRL.setOnClickListener(v -> {
            if(NetworkConnection.isOnline(activity)){
                CollectionReference receiptsCollection =
                    firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS);
                DocumentReference documentReference = receiptsCollection.document(firebaseID);
                documentReference.update(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID, true);

                dialog.dismiss();
                HomeActivity.receiptViewerFragment.refreshList();
            }else{
                Toast.makeText(activity, "Please enable internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
        deleteRL.setOnClickListener(v -> {
            if (NetworkConnection.isOnline(activity)){
                firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS)
                    .document(firebaseID)
                    .delete()
                    .addOnSuccessListener(aVoid -> Snackbar.make(v, "Receipt deleted successfully!", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show())
                    .addOnFailureListener(e -> Snackbar.make(v, "Fail to delete receipt!", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show());

                dialog.dismiss();
                HomeActivity.receiptViewerFragment.refreshList();
            }else{
                Toast.makeText(activity, "Please enable internet connection",
                        Toast.LENGTH_SHORT).show();
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

}
