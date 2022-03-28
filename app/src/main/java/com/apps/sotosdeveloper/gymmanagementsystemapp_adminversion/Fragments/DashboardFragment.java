package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects.User;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Others.ThemeManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import static android.content.ContentValues.TAG;

public class DashboardFragment extends Fragment implements View.OnClickListener{

    public DashboardFragment() {
        // Required empty public constructor
    }

    private final MaterialCardView[] monthsCards = new MaterialCardView[12];
    private MaterialCardView month_unfocused;
    private final int[] cardID = {  R.id.cardJan, R.id.cardFeb, R.id.cardMar, R.id.cardApr, R.id.cardMay,
                                    R.id.cardJune, R.id.cardJuly, R.id.cardAug, R.id.cardSep, R.id.cardOct,
                                    R.id.cardNov, R.id.cardDec  };
    private int selectedMonth = 0;
    private int selectedYear = 1;

    private boolean isMonthBodyOpen = false;
    private boolean isAnnualBodyOpen = false;

    private FirebaseFirestore firebaseFirestore;

    // for monthly amount
    private RelativeLayout monthTitleLayout, monthBodyLayout;
    private TextView monthlyAmount, monthlyAmountExpected, monthlyAmountNonPaid;
    private Spinner spinnerMonthUsers;

    // for annual amount
    private RelativeLayout yearTitleLayout, yearBodyLayout;
    private TextView annualAmount, annualAmountExpected, annualAmountNonPaid;
    private Spinner spinnerAnnualYears;
    private SpinnerAdapter adapterYears;

    // for annual amount per user
    private Spinner spinnerAnnualUsers;
    private ArrayList<User> users;
    private ArrayList<String> availableYears;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard,
                container, false);

        // first of all, create the UI elements
        initUI(view);
        // init other components (Libraries, arrayLists etc.)
        initComponentsAndLibraries();

        getAndInitUsersToSpinners();
        getAndSetAllAvailableYears();

        // callable methods
        onMonthlyUsersSpinnerChangeItemListener();
        onAnnualUsersSpinnerChangeItemListener();
        onAnnualYearsSpinnerChangeItemListener();

        setMonthlyBodyLayoutVisibility();
        setAnnualBodyLayoutVisibility();

        setCurrentMonthFilter();

        return view;
    }

    /**
     * Init all ui components
     * @param v: fragment requires a View to find any component by ID
     */
    private void initUI(View v){
        initLayouts(v);
        initTextViews(v);
        initSpinners(v);
        // create the month cards (each month at monthly stats card)
        initMonthsCards(v);
    }
    /**
     * For month and annual stats layout.
     * @param v: current view
     */
    private void initLayouts(View v){
        monthTitleLayout    = v.findViewById(R.id.relativeMonthlyStats);
        monthBodyLayout     = v.findViewById(R.id.relativeMonthlyStatsBody);
        yearTitleLayout     = v.findViewById(R.id.relativeTitleAnnualStats);
        yearBodyLayout      = v.findViewById(R.id.relativeBodyAnnualStats);
    }
    /**
     * For monthly stats. This method will initialize months at monthly stats card.
     * @param v: View
     */
    private void initMonthsCards(View v){
        for(int i = 0; i < monthsCards.length; i++){
            monthsCards[i] = v.findViewById(cardID[i]);
            monthsCards[i].setOnClickListener(this);
        }
        month_unfocused = monthsCards[0];
    }
    /**
     * Init all textViews here...
     * @param v: View
     */
    private void initTextViews(View v){
        monthlyAmount                   = v.findViewById(R.id.revenueCardAmountTV);
        monthlyAmountExpected           = v.findViewById(R.id.revenueCardAmountTV_editable);
        monthlyAmountNonPaid            = v.findViewById(R.id.nonPaidCardAmountTV_editable);

        annualAmount                    = v.findViewById(R.id.revenueCardAmountForAnnualStatsTV);
        annualAmountExpected            = v.findViewById(R.id.revenueCardAmountForAnnualStatsTV_editable);
        annualAmountNonPaid             = v.findViewById(R.id.nonPaidCardAmountForAnnualStatsTV_editable);
    }
    /**
     * Init all spinners here...
     * @param v: View
     */
    private void initSpinners(View v){
        spinnerMonthUsers               = v.findViewById(R.id.dashboardSpinnerUsers);
        spinnerAnnualYears              = v.findViewById(R.id.dashboardSpinnerYears);
        spinnerAnnualUsers              = v.findViewById(R.id.dashboardSpinnerUsersAnnualStats);
    }
    /**
     * Other libraries like FirebaseFirestore etc.
     */
    private void initComponentsAndLibraries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    /**
     * Retrieve all available users from database and store them (FirstName and LastName) into the spinners
     */
    private void getAndInitUsersToSpinners(){

        if (users != null && !users.isEmpty()){
            users.clear();
        }
        // we will use these to set users
        users = new ArrayList<>();
        ArrayList<String> usersToString = new ArrayList<>();
        // one element array to handle data in Asynchronous tasks line addOnCompleteListener!
        final SpinnerAdapter[] adapter = new SpinnerAdapter[1];

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_USERS).
                get().
                addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    // enter every user into adapter
                    // and pass the data
                    User userTemp =
                            new User(document.getId(),
                                    Objects.requireNonNull
                                            (document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_FIRST_NAME)).
                                            toString(),
                                    Objects.requireNonNull
                                            (document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_LAST_NAME)).
                                            toString(),
                                    Objects.requireNonNull
                                            (document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_EMAIL)).
                                            toString());

                    String typeTemp = Objects.requireNonNull(
                            document.get(DatabaseConstants.FIREBASE_DOCUMENT_USERS_TYPE)).toString();

                    // only CLIENT users will enter in adapter
                    if(typeTemp.equalsIgnoreCase(DatabaseConstants.FIREBASE_USER_TYPE_1)){
                        users.add(userTemp);
                        usersToString.add(String.format("%s %s",
                                userTemp.getFirstName(), userTemp.getLastName()));
                    }
                }

                // check if any user found and change first position
                // of spinner items to: --- FROM EVERYONE ---
                // if not any user found change first position to: NOT ANY USER item
                if(!usersToString.isEmpty()) usersToString.add(0, "--- FROM EVERYONE ---");
                else usersToString.add("NOT ANY USER");

                adapter[0] = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, usersToString);
                spinnerMonthUsers.setAdapter(adapter[0]);
                // set also the users spinner of annual stats card
                spinnerAnnualUsers.setAdapter(adapter[0]);

                // calculate monthly stats only when at least one user is a member
                if(usersToString.get(0).equalsIgnoreCase("--- FROM EVERYONE ---")){
                    monthlyStatsCalculator("--- FROM EVERYONE ---");
                }

            }
            else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });


    }
    /**
     * Search in receipt creation dates and find all available years!
     * Example: 2 current receipts - 1 created at 2021 and the other one at 2022.
     *          All available years are 2021 and 2022. Set to spinner these two years only.
     */
    private void getAndSetAllAvailableYears(){

        ArrayList<String> dates = new ArrayList<>();
        dates.add("--- FROM EVERY YEAR ---");
        availableYears = new ArrayList<>();

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){

                            String date = (String) document.
                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE);
                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                                    Locale.ENGLISH);
                            try {
                                assert date != null;
                                Date dateString = format.parse(date);
                                Calendar cal = Calendar.getInstance();
                                assert dateString != null;
                                cal.setTimeInMillis(dateString.getTime());

                                int finalDate = cal.get(Calendar.YEAR);

                                if(availableYears.isEmpty()){
                                    availableYears.add(String.valueOf(finalDate));
                                    dates.add(String.valueOf(finalDate));
                                }else{
                                    // if this list is not empty means that maybe
                                    // the same year is already in this list
                                    // DO SOME SEARCH PLEASE
                                    boolean found = false;
                                    for(String temp: availableYears){
                                        if(temp.equalsIgnoreCase(String.valueOf(finalDate))) found = true;
                                    }
                                    if(!found) {
                                        availableYears.add(String.valueOf(finalDate));
                                        dates.add(String.valueOf(finalDate));
                                    }
                                }

                                // check if arrayList is empty or not
                                if (availableYears.isEmpty()){
                                    dates.clear();
                                    dates.add("ADD ANY RECEIPT");
                                }

                                Collections.sort(dates, String::compareToIgnoreCase);
                                Collections.sort(availableYears, String::compareToIgnoreCase);

                                adapterYears = new ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_spinner_dropdown_item, dates);
                                spinnerAnnualYears.setAdapter(adapterYears);

                            }catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    /**
     * Calculate monthly stats!
     * Error prevention also included!
     * @param userID: 2 possible userID's -> --- FROM EVERYONE ---, any userID from database!
     */
    private void monthlyStatsCalculator(String userID){
        final double[] totalAmount      =   {0.0};
        final double[] expectedAmount   =   {0.0};
        final double[] nonPaidAmount    =   {0.0};

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(userID.equalsIgnoreCase("--- FROM EVERYONE ---")){

                            for (QueryDocumentSnapshot document:
                                    Objects.requireNonNull(task.getResult())){

                                String date = (String) document.
                                        get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE);
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                                        Locale.ENGLISH);
                                try {
                                    assert date != null;
                                    Date dateString = format.parse(date);
                                    Calendar cal = Calendar.getInstance();
                                    assert dateString != null;
                                    cal.setTimeInMillis(dateString.getTime());

                                    int finalDate = cal.get(Calendar.MONTH);

                                    if(selectedMonth == finalDate) {

                                        String amountString = (String)
                                                document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                        assert amountString != null;
                                        double amount = Double.parseDouble(amountString);

                                        if((Boolean) document.
                                                get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                            // if receipt is paid then add amount to total and expected amount variables
                                            expectedAmount[0] += amount;
                                        }
                                        totalAmount[0] += amount;

                                    }

                                    nonPaidAmount[0] = Math.abs(expectedAmount[0] - totalAmount[0]);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // set textViews
                                monthlyAmount.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", expectedAmount[0]));
                                monthlyAmountExpected.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", totalAmount[0]));
                                monthlyAmountNonPaid.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", nonPaidAmount[0]));
                            }

                        }
                        else{
                            for (QueryDocumentSnapshot document:
                                    Objects.requireNonNull(task.getResult())){

                                String tempUserID = (String)
                                        document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_TO_USER);

                                assert tempUserID != null;

                                // if the ID of the user is the given ID then start the process
                                if (userID.equalsIgnoreCase(tempUserID)){
                                    String date = (String) document.
                                            get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE);
                                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                                            Locale.ENGLISH);
                                    try {
                                        assert date != null;
                                        Date dateString = format.parse(date);
                                        Calendar cal = Calendar.getInstance();
                                        assert dateString != null;
                                        cal.setTimeInMillis(dateString.getTime());

                                        int finalDate = cal.get(Calendar.MONTH);

                                        if(selectedMonth == finalDate) {

                                            String amountString = (String)
                                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                            assert amountString != null;
                                            double amount = Double.parseDouble(amountString);

                                            if((Boolean) document.
                                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                                // if receipt is paid then add amount to total and expected amount variables
                                                expectedAmount[0] += amount;
                                            }
                                            totalAmount[0] += amount;

                                        }

                                        nonPaidAmount[0] = Math.abs(expectedAmount[0] - totalAmount[0]);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // set textViews
                                monthlyAmount.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", expectedAmount[0]));
                                monthlyAmountExpected.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", totalAmount[0]));
                                monthlyAmountNonPaid.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", nonPaidAmount[0]));
                            }
                        }
                    }
                });


    }
    /**
     * Calculate annual stats!
     * Error prevention also included!
     * @param userID: 2 possible user id's -> --- FROM EVERYONE ---, any other user id
     * @param selectedYear: 2 possible selected years -> 1(all years), any other year (2021, 2022 etc.)
     */
    private void annuallyStatsCalculator(String userID, int selectedYear){
        final double[] totalAmount      =   {0.0};
        final double[] expectedAmount   =   {0.0};
        final double[] nonPaidAmount    =   {0.0};

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_RECEIPTS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(userID.equalsIgnoreCase("--- FROM EVERYONE ---")){

                            for (QueryDocumentSnapshot document:
                                    Objects.requireNonNull(task.getResult())){

                                String date = (String) document.
                                        get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE);
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                                        Locale.ENGLISH);
                                try {

                                    if(selectedYear == 1){
                                        String amountString = (String)
                                                document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                        assert amountString != null;
                                        double amount = Double.parseDouble(amountString);

                                        if((Boolean) document.
                                                get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                            // if receipt is paid then add amount to total and expected amount variables
                                            expectedAmount[0] += amount;
                                        }
                                        totalAmount[0] += amount;

                                    }else{

                                        assert date != null;
                                        Date dateString = format.parse(date);
                                        Calendar cal = Calendar.getInstance();
                                        assert dateString != null;
                                        cal.setTimeInMillis(dateString.getTime());

                                        int finalDate = cal.get(Calendar.YEAR);

                                        if(selectedYear == finalDate) {

                                            String amountString = (String)
                                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                            assert amountString != null;
                                            double amount = Double.parseDouble(amountString);

                                            if((Boolean) document.
                                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                                // if receipt is paid then add amount to total and expected amount variables
                                                expectedAmount[0] += amount;
                                            }
                                            totalAmount[0] += amount;

                                        }

                                    }

                                    nonPaidAmount[0] = Math.abs(expectedAmount[0] - totalAmount[0]);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // set textViews
                                annualAmount.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", expectedAmount[0]));
                                annualAmountExpected.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", totalAmount[0]));
                                annualAmountNonPaid.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", nonPaidAmount[0]));
                            }

                        }
                        else{

                            for (QueryDocumentSnapshot document:
                                    Objects.requireNonNull(task.getResult())){

                                String tempUserID = (String)
                                        document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_TO_USER);

                                assert tempUserID != null;

                                // if the ID of the user is the given ID then start the process
                                if (userID.equalsIgnoreCase(tempUserID)){
                                    String date = (String) document.
                                            get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_DATE);
                                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm",
                                            Locale.ENGLISH);
                                    try {

                                        if(selectedYear == 1){
                                            String amountString = (String)
                                                    document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                            assert amountString != null;
                                            double amount = Double.parseDouble(amountString);

                                            if((Boolean) document.
                                                    get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                                // if receipt is paid then add amount to total and expected amount variables
                                                expectedAmount[0] += amount;
                                            }
                                            totalAmount[0] += amount;

                                        }else{
                                            assert date != null;
                                            Date dateString = format.parse(date);
                                            Calendar cal = Calendar.getInstance();
                                            assert dateString != null;
                                            cal.setTimeInMillis(dateString.getTime());

                                            int finalDate = cal.get(Calendar.YEAR);

                                            if(selectedYear == finalDate) {

                                                String amountString = (String)
                                                        document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_AMOUNT);
                                                assert amountString != null;
                                                double amount = Double.parseDouble(amountString);

                                                if((Boolean) document.get(DatabaseConstants.FIREBASE_DOCUMENT_RECEIPT_PAID)){
                                                    // if receipt is paid then add amount to total and expected amount variables
                                                    expectedAmount[0] += amount;
                                                }
                                                totalAmount[0] += amount;

                                            }
                                        }

                                        nonPaidAmount[0] = Math.abs(expectedAmount[0] - totalAmount[0]);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // set textViews
                                annualAmount.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", expectedAmount[0]));
                                annualAmountExpected.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", totalAmount[0]));
                                annualAmountNonPaid.setText(String.format(Locale.ENGLISH,
                                        "%.2f€", nonPaidAmount[0]));
                            }
                        }
                    }
                });

    }

    /**
     * Spinner on item click listener method. Only for monthly stats
     */
    private void onMonthlyUsersSpinnerChangeItemListener(){
        spinnerMonthUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(users.isEmpty()) return;

                if(position == 0) monthlyStatsCalculator("--- FROM EVERYONE ---");
                else {
                    monthlyStatsCalculator(users.get(position - 1).getID());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /**
     * Spinner on item click listener method. Only for monthly stats
     */
    private void onAnnualUsersSpinnerChangeItemListener(){
        spinnerAnnualUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(users.isEmpty()) return;
                if(availableYears.isEmpty()) return;

                if(position == 0) annuallyStatsCalculator("--- FROM EVERYONE ---", selectedYear);
                else {
                    annuallyStatsCalculator(users.get(position - 1).getID(), selectedYear);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /**
     * Spinner on item click listener method. Only for monthly stats
     */
    private void onAnnualYearsSpinnerChangeItemListener(){
        spinnerAnnualYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(users.isEmpty()) return;
                if(availableYears.isEmpty()) return;

                if(position == 0) selectedYear = 1;
                else selectedYear = Integer.parseInt(availableYears.get(position - 1));

                spinnerAnnualUsers.setSelection(0, true);
                annuallyStatsCalculator("--- FROM EVERYONE ---", selectedYear);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Monthly stats -> month cards
     * This method will handle the ui for these cards when clicked!
     * @param btn_unfocused: Previous clicked card (month)!
     * @param btn_focus: The card (month) we want to choose
     */
    private void setFocus(MaterialCardView btn_unfocused, MaterialCardView btn_focus){
        if(btn_unfocused == btn_focus) return;
        btn_focus.setStrokeWidth(3);
        btn_focus.setStrokeColor(ThemeManager.themeColorPrimary(requireContext()));

        btn_unfocused.setCardElevation(0f);
        btn_unfocused.setStrokeColor(0);
        this.month_unfocused = btn_focus;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        // On click listener for months filters
        switch (v.getId()){
            case R.id.cardJan:  setFocus(month_unfocused, monthsCards[0]);  selectedMonth = 0;  break;
            case R.id.cardFeb:  setFocus(month_unfocused, monthsCards[1]);  selectedMonth = 1;  break;
            case R.id.cardMar:  setFocus(month_unfocused, monthsCards[2]);  selectedMonth = 2;  break;
            case R.id.cardApr:  setFocus(month_unfocused, monthsCards[3]);  selectedMonth = 3;  break;
            case R.id.cardMay:  setFocus(month_unfocused, monthsCards[4]);  selectedMonth = 4;  break;
            case R.id.cardJune: setFocus(month_unfocused, monthsCards[5]);  selectedMonth = 5;  break;
            case R.id.cardJuly: setFocus(month_unfocused, monthsCards[6]);  selectedMonth = 6;  break;
            case R.id.cardAug:  setFocus(month_unfocused, monthsCards[7]);  selectedMonth = 7;  break;
            case R.id.cardSep:  setFocus(month_unfocused, monthsCards[8]);  selectedMonth = 8;  break;
            case R.id.cardOct:  setFocus(month_unfocused, monthsCards[9]);  selectedMonth = 9;  break;
            case R.id.cardNov:  setFocus(month_unfocused, monthsCards[10]); selectedMonth = 10; break;
            case R.id.cardDec:  setFocus(month_unfocused, monthsCards[11]); selectedMonth = 11; break;
        }
        if(!users.isEmpty()) {
            refreshResults();
        }
    }
    /**
     * For monthly stats only!
     */
    private void refreshResults(){
        spinnerMonthUsers.setSelection(0);
        monthlyStatsCalculator("--- FROM EVERYONE ---");
    }
    private void setCurrentMonthFilter(){
        Calendar c = Calendar.getInstance();
        setFocus(month_unfocused, monthsCards[c.get(Calendar.MONTH)]);
        selectedMonth = c.get(Calendar.MONTH);
    }


    private void setMonthlyBodyLayoutVisibility(){
        monthTitleLayout.setOnClickListener(v -> {
            if(isMonthBodyOpen){
                isMonthBodyOpen = false;
                monthBodyLayout.setVisibility(View.GONE);
            }else{
                isMonthBodyOpen = true;
                isAnnualBodyOpen = false;
                monthBodyLayout.setVisibility(View.VISIBLE);
                yearBodyLayout.setVisibility(View.GONE);
            }
        });
    }
    private void setAnnualBodyLayoutVisibility(){
        yearTitleLayout.setOnClickListener(v -> {
            if(isAnnualBodyOpen){
                isAnnualBodyOpen = false;
                yearBodyLayout.setVisibility(View.GONE);
            }else{
                isAnnualBodyOpen = true;
                isMonthBodyOpen = false;
                yearBodyLayout.setVisibility(View.VISIBLE);
                monthBodyLayout.setVisibility(View.GONE);
            }
        });
    }

}