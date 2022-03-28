package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.MainActivityFragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OpeningHoursFragment extends Fragment {

    public OpeningHoursFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout workingDayCurrentDayLayout, dayOffCurrentDayLayout;
    private TextView currentDayOpenTV, currentDayCloseTV, currentDayTV;

    private TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.
                inflate(R.layout.fragment_opening_hours_fragment, container, false);

        init(view);

        currentDayTV.setText(getCurrentSystemDateToString());

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        setDataToScheduleCardCurrentDay(getCurrentSystemDate());
        setDataToScheduleCardOtherDays();

        return view;
    }

    public void refresh(){
        setDataToScheduleCardCurrentDay(getCurrentSystemDate());
        setDataToScheduleCardOtherDays();
    }

    private String getCurrentSystemDateToString(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        Date d = new Date();
        return sdf.format(d);
    }
    private TextView getCurrentDateTextView(int currentDay){
        switch (currentDay){
            case Calendar.SUNDAY:
                return sunday;
            case Calendar.MONDAY:
                return monday;
            case Calendar.TUESDAY:
                return tuesday;
            case Calendar.WEDNESDAY:
                return wednesday;
            case Calendar.THURSDAY:
                return thursday;
            case Calendar.FRIDAY:
                return friday;
            case Calendar.SATURDAY:
                return saturday;
            default: return monday;
        }
    }
    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }
    private int getCurrentSystemDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
    private int translateStringDayToInt(String day){
        switch (day) {
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SUNDAY:
                return Calendar.SUNDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_MONDAY:
                return Calendar.MONDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_TUESDAY:
                return Calendar.TUESDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_WEDNESDAY:
                return Calendar.WEDNESDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_THURSDAY:
                return Calendar.THURSDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_FRIDAY:
                return Calendar.FRIDAY;
            case DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SATURDAY:
                return Calendar.SATURDAY;
        }
        return Calendar.SATURDAY;
    }

    private void setDataToScheduleCardCurrentDay(int currentDay){

        String dayToString = getCurrentSystemDateToString();

        Log.e("Current day", "Current day -> " + dayToString);

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_OPENING_HOURS).
                document(dayToString).
                get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                // change color of current day (textView component)
                getCurrentDateTextView(getCurrentSystemDate()).
                        setTextColor(getThemeAccentColor(requireContext()));
                // change size
                getCurrentDateTextView(getCurrentSystemDate()).setTextSize(16f);
                // change style
                getCurrentDateTextView(getCurrentSystemDate()).setTypeface(Typeface.DEFAULT_BOLD);

                String openTime = task.getResult().
                        get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_OPEN).toString();
                String closeTime = task.getResult().
                        get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_CLOSED).toString();

                // if day is day off then show to user ...
                if(task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF) == null){
                    // weekday!
                    getCurrentDateTextView(currentDay).
                            setText(String.format("%s \nfrom %s - to %s",
                                    getCurrentSystemDateToString(),
                                    openTime, closeTime));

                }else{
                    if((Boolean) task.getResult().
                            get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF)){
                        getCurrentDateTextView(getCurrentSystemDate()).
                                setText(String.format("%s - %s", getCurrentSystemDateToString(), "Closed!"));

                        setCurrentDayState(true);

                    }else{
                        // is weekend and open
                        getCurrentDateTextView(currentDay).
                                setText(String.format("%s \nfrom %s - to %s",
                                        getCurrentSystemDateToString(),
                                        openTime, closeTime));

                        setCurrentDayState(false);
                    }
                }

                currentDayOpenTV.setText(openTime);
                currentDayCloseTV.setText(closeTime);

            }
        });
    }
    private void setDataToScheduleCardOtherDays(){

        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_OPENING_HOURS).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){

                            if(!document.getId().equalsIgnoreCase(getCurrentSystemDateToString())){

                                int dayFromIntToString = translateStringDayToInt(document.getId());

                                // if day found is not the current day!
                                // handled by other method
                                String open = document.get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_OPEN).toString();
                                String close = document.get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_CLOSED).toString();

                                // if day is day off then show to user ...
                                if(document.get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF) == null){
                                    // weekday!
                                    Log.e("day", String.valueOf(dayFromIntToString));
                                    getCurrentDateTextView(dayFromIntToString).
                                            setText(String.format("%s \n%s - %s",
                                                    document.getId(),
                                                    open, close));

                                }else{
                                    if((Boolean) document.
                                            get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF)){
                                        getCurrentDateTextView(dayFromIntToString).
                                                setText(String.format("%s - %s", document.getId(), "Closed!"));
                                    }else{
                                        // is weekend and open
                                        getCurrentDateTextView(dayFromIntToString).
                                                setText(String.format("%s \n%s - %s",
                                                        document.getId(),
                                                        open, close));
                                    }
                                }

                            }

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void init(View view){
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        firebaseFirestore = FirebaseFirestore.getInstance();

        workingDayCurrentDayLayout = view.findViewById(R.id.todayScheduleWorkingDay);
        dayOffCurrentDayLayout = view.findViewById(R.id.todayScheduleDayOff);

        currentDayTV = view.findViewById(R.id.currentDay);
        currentDayOpenTV = view.findViewById(R.id.openHourTV);
        currentDayCloseTV = view.findViewById(R.id.closeHourTV);

        // for schedule
        monday = view.findViewById(R.id.Monday);
        tuesday = view.findViewById(R.id.Tuesday);
        wednesday = view.findViewById(R.id.Wednesday);
        thursday = view.findViewById(R.id.Thursday);
        friday = view.findViewById(R.id.Friday);
        saturday = view.findViewById(R.id.Saturday);
        sunday = view.findViewById(R.id.Sunday);
    }

    private void setCurrentDayState(boolean isClosed){
        if(isClosed){
            workingDayCurrentDayLayout.setVisibility(View.GONE);
            dayOffCurrentDayLayout.setVisibility(View.VISIBLE);
        }else{
            dayOffCurrentDayLayout.setVisibility(View.GONE);
            workingDayCurrentDayLayout.setVisibility(View.VISIBLE);
        }
    }

}