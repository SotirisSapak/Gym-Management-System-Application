package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Adapters.RecyclerViewReceiptsAdapter;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.InitActivity;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private RecyclerViewReceiptsAdapter receiptsAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Receipt> receipts;

    private TextView paidTV, nonPaidTV, totalTV;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout noReceiptsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        getAllReceipts();
        swipeRefreshLayout.setOnRefreshListener(this::refreshAll);

        return view;
    }

    public void refreshAll(){
        receipts.clear();
        recyclerView.invalidate();
        recyclerView.removeAllViews();
        getAllReceipts();
    }

    private void init(View view){
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        totalTV = view.findViewById(R.id.totalReceiptMoney);
        paidTV = view.findViewById(R.id.paidTV);
        nonPaidTV = view.findViewById(R.id.nonPaidTV);

        receipts = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewReceipts);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        firebaseFirestore = FirebaseFirestore.getInstance();

        noReceiptsLayout = view.findViewById(R.id.layoutNoReceipts);
    }

    private void getAllReceipts(){

        final double[] paidAmount = {0};
        double[] totalAmount = {0};
        double[] nonPaidAmount = {0};

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS).
                get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    // enter every receipt into listView adapter
                    Receipt receiptTemp = new Receipt(
                            document.getId(),
                            Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_ID)).toString(),
                            Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE)).toString(),
                            Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_TO_USER)).toString(),
                            Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT)).toString(),
                            document.getBoolean(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID));

                    // filter - for only this user
                    if(receiptTemp.getToUser().
                            equalsIgnoreCase(InitActivity.memoryHandler.getUserIDFromStorage())){
                        receipts.add(receiptTemp);
                    }

                }

                // check if arrayList is empty or not
                if (receipts.isEmpty()){
                    setEmptyStateLayout(true);
                }else{
                    setEmptyStateLayout(false);
                    receiptsAdapter = new RecyclerViewReceiptsAdapter(getActivity(), receipts);

                    for (Receipt temp: receipts){
                        if(temp.isReceiptPaid()){
                            paidAmount[0] += Double.parseDouble(temp.getAmount());
                        }else{
                            nonPaidAmount[0] += Double.parseDouble(temp.getAmount());
                        }
                        totalAmount[0] += Double.parseDouble(temp.getAmount());
                    }

                    totalTV.setText(String.format(Locale.ENGLISH,"%.2f€", totalAmount[0]));
                    paidTV.setText(String.format(Locale.ENGLISH,"%.2f€", paidAmount[0]));
                    nonPaidTV.setText(String.format(Locale.ENGLISH,"%.2f€", nonPaidAmount[0]));

                    recyclerView.setAdapter(receiptsAdapter);
                }
                swipeRefreshLayout.setRefreshing(false);

            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    private void setEmptyStateLayout(boolean emptyState){
        if (emptyState){
            noReceiptsLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noReceiptsLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}