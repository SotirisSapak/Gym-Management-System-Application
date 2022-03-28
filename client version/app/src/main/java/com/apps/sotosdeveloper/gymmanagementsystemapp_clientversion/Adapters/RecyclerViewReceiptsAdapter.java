package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Utils.ThemeManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RecyclerViewReceiptsAdapter extends
        RecyclerView.Adapter<RecyclerViewReceiptsAdapter.MyViewHolder>{

    private final Activity activity;
    private final ArrayList<Receipt> receipts;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardAmount;
        private final TextView title;
        private final TextView date;
        private final TextView amount;

        public MyViewHolder(View view){
            super(view);

            cardAmount = view.findViewById(R.id.item_card_amount);
            title = view.findViewById(R.id.item_title);
            date = view.findViewById(R.id.item_date);
            amount = view.findViewById(R.id.item_amount);
        }

    }

    public RecyclerViewReceiptsAdapter(Activity activity, ArrayList<Receipt> receipts){
        this.activity = activity;
        this.receipts = receipts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_receipts_item, parent, false);
        return new RecyclerViewReceiptsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.title.setText(receipts.get(position).getReceiptID());
        holder.date.setText(receipts.get(position).getDate());
        holder.amount.setText(String.format("%s€", receipts.get(position).getAmount()));

        if(receipts.get(position).isReceiptPaid()) {
            holder.cardAmount.setCardBackgroundColor(ThemeManager.themeColorGreen(activity));
        }else{
            holder.cardAmount.setCardBackgroundColor(ThemeManager.themeColorRed(activity));
        }

    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }
}
