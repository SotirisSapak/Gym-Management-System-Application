package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.Filters;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.Receipt;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters.ReceiptsRecyclerViewAdapter;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.ThemeManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ReceiptViewerFragment extends Fragment {

    public ReceiptViewerFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerViewReceipts;

    private ReceiptsRecyclerViewAdapter receiptsAdapter;

    private ArrayList<Receipt> allReceipts;
    private ArrayList<Receipt> singleReceipts;
    private ArrayList<Receipt> monthlyReceipts;
    private ArrayList<Receipt> annuallyReceipts;

    // filters
    private MaterialCardView receiptAll, receiptSingle, receiptMonthly, receiptAnnually;
    private TextView text1, text2, text3, text4;


    private RelativeLayout noReceiptsLayout, receiptsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receipt_viewer, container, false);

        init(view);
        getAllReceipts();
        swipeRefreshListView();

        setReceiptAll_clickListener();
        setReceiptSingle_clickListener();
        setReceiptMonthly_clickListener();
        setReceiptAnnually_clickListener();

        return view;
    }

    private void setUpFilterLayout(){

        if(allReceipts.isEmpty()) receiptAll.setVisibility(View.GONE);
        else receiptAll.setVisibility(View.VISIBLE);

        if(singleReceipts.isEmpty()) receiptSingle.setVisibility(View.GONE);
        else receiptSingle.setVisibility(View.VISIBLE);

        if(monthlyReceipts.isEmpty()) receiptMonthly.setVisibility(View.GONE);
        else receiptMonthly.setVisibility(View.VISIBLE);

        if(annuallyReceipts.isEmpty()) receiptAnnually.setVisibility(View.GONE);
        else receiptAnnually.setVisibility(View.VISIBLE);

    }

    private void swipeRefreshListView(){

    }

    private void init(View view){
        recyclerViewReceipts = view.findViewById(R.id.recyclerViewReceipts);

        noReceiptsLayout = view.findViewById(R.id.relativeNoReceipts);
        receiptsLayout = view.findViewById(R.id.relativeReceipts);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewReceipts.setHasFixedSize(true);

        receiptAll = view.findViewById(R.id.receipt_mode_all);
        receiptSingle = view.findViewById(R.id.receipt_mode_simple);
        receiptMonthly = view.findViewById(R.id.receipt_mode_monthly);
        receiptAnnually = view.findViewById(R.id.receipt_mode_annually);

        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        text3 = view.findViewById(R.id.text3);
        text4 = view.findViewById(R.id.text4);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);
        recyclerViewReceipts.setLayoutManager(layoutManager);

        singleReceipts = new ArrayList<>();
        monthlyReceipts = new ArrayList<>();
        annuallyReceipts = new ArrayList<>();
        allReceipts = new ArrayList<>();
    }

    private void setReceiptSingle_clickListener(){
        receiptSingle.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_SINGLE));
    }
    private void setReceiptMonthly_clickListener(){
        receiptMonthly.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_MONTHLY));
    }
    private void setReceiptAnnually_clickListener(){
        receiptAnnually.setOnClickListener(v -> filterClick(Filters.RECEIPT_MODE_ANNUALLY));
    }
    private void setReceiptAll_clickListener(){
        receiptAll.setOnClickListener(v -> filterClick(Filters.RECEIPT_FILTER_ALL));
    }


    private void filterClick(String filter){
        switch (filter){
            case Filters.RECEIPT_FILTER_ALL:
                receiptAll.setStrokeWidth(3);
                receiptAll.setStrokeColor(ThemeManager.themeColorPrimary(requireContext()));

                text1.setTextColor(ThemeManager.themeColorPrimary(requireContext()));
                text2.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text3.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text4.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));

                receiptSingle.setCardElevation(0f);
                receiptSingle.setStrokeColor(0);

                receiptMonthly.setCardElevation(0f);
                receiptMonthly.setStrokeColor(0);

                receiptAnnually.setCardElevation(0f);
                receiptAnnually.setStrokeColor(0);

                receiptsAdapter.setFilter(allReceipts);
                break;
            case Filters.RECEIPT_MODE_SINGLE:
                receiptSingle.setStrokeWidth(3);
                receiptSingle.setStrokeColor(ThemeManager.themeColorPrimary(requireContext()));

                text1.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text2.setTextColor(ThemeManager.themeColorPrimary(requireContext()));
                text3.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text4.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));

                receiptAll.setCardElevation(0f);
                receiptAll.setStrokeColor(0);
                receiptMonthly.setCardElevation(0f);
                receiptMonthly.setStrokeColor(0);
                receiptAnnually.setCardElevation(0f);
                receiptAnnually.setStrokeColor(0);

                receiptsAdapter.setFilter(singleReceipts);
                break;
            case Filters.RECEIPT_MODE_MONTHLY:
                receiptMonthly.setStrokeWidth(3);
                receiptMonthly.setStrokeColor(ThemeManager.themeColorPrimary(requireContext()));

                text1.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text2.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text3.setTextColor(ThemeManager.themeColorPrimary(requireContext()));
                text4.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));

                receiptAll.setCardElevation(0f);
                receiptAll.setStrokeColor(0);
                receiptAnnually.setCardElevation(0f);
                receiptAnnually.setStrokeColor(0);
                receiptSingle.setCardElevation(0f);
                receiptSingle.setStrokeColor(0);

                receiptsAdapter.setFilter(monthlyReceipts);
                break;
            case Filters.RECEIPT_MODE_ANNUALLY:
                receiptAnnually.setStrokeWidth(3);
                receiptAnnually.setStrokeColor(ThemeManager.themeColorPrimary(requireContext()));

                text1.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text2.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text3.setTextColor(ThemeManager.themeColorControlNormal(requireContext()));
                text4.setTextColor(ThemeManager.themeColorPrimary(requireContext()));

                receiptAll.setCardElevation(0f);
                receiptAll.setStrokeColor(0);
                receiptMonthly.setCardElevation(0f);
                receiptMonthly.setStrokeColor(0);
                receiptSingle.setCardElevation(0f);
                receiptSingle.setStrokeColor(0);

                receiptsAdapter.setFilter(annuallyReceipts);
                break;
        }
    }

    public void refreshList(){
        recyclerViewReceipts.invalidate();
        receiptsAdapter.notifyDataSetChanged();

        allReceipts.clear();
        singleReceipts.clear();
        monthlyReceipts.clear();
        annuallyReceipts.clear();
        getAllReceipts();

        filterClick(Filters.RECEIPT_FILTER_ALL);
    }

    private void getAllReceipts(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

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
                            allReceipts.add(receiptTemp);
                            if(Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE)).toString().
                                    equalsIgnoreCase(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_SINGLE)){
                                singleReceipts.add(receiptTemp);
                            }
                            if(Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE)).toString().
                                    equalsIgnoreCase(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_MONTHLY)){
                                monthlyReceipts.add(receiptTemp);
                            }
                            if(Objects.requireNonNull(document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE)).toString().
                                    equalsIgnoreCase(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_MODE_ANNUALLY)){
                                annuallyReceipts.add(receiptTemp);
                            }
                        }
                        // check if arrayList is empty or not
                        if (allReceipts.isEmpty()){
                            receiptsLayout.setVisibility(View.GONE);
                            noReceiptsLayout.setVisibility(View.VISIBLE);
                            receiptsAdapter = new ReceiptsRecyclerViewAdapter(getActivity(), null);
                        }else{
                            receiptsLayout.setVisibility(View.VISIBLE);
                            noReceiptsLayout.setVisibility(View.GONE);
                            receiptsAdapter = new ReceiptsRecyclerViewAdapter(getActivity(), allReceipts);
                            recyclerViewReceipts.setAdapter(receiptsAdapter);
                            setUpFilterLayout();
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

}