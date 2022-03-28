package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants.DatabaseConstants;
import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OpeningHoursFragment extends Fragment {

    public OpeningHoursFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout layoutWeekdays;
    private TextView selectedDay;
    private String selectedDayString;
    // layout open hour
    private RelativeLayout layoutOpenHour;
    private TextView textOpenHour;
    // layout close hour
    private RelativeLayout layoutCloseHour;
    private TextView textCloseHour;

    private boolean hasDayOff = false;
    private MaterialButton buttonDayOffOrWorkingDay;
    private RelativeLayout layoutWeekendOptions;

    private Chip monChip, tuesChip, wedChip, thurChip, friChip, satChip, sunChip;
    private MaterialButton buttonSavePreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opening_hours, container, false);
        init(view);

        onPageFirstSelected();
        daysOnClickListener();

        addOnOpenHourClick();
        addOnCloseHourClick();
        addOnDayOffClick();
        addSavePreferencesOnClick();

        return view;
    }

    private void init(View view){
        firebaseFirestore = FirebaseFirestore.getInstance();

        layoutWeekdays = view.findViewById(R.id.workingDayLayout);

        selectedDay = view.findViewById(R.id.openingHoursSelectedDayTV);
        layoutOpenHour = view.findViewById(R.id.openHoursRelativeLayout);
        textOpenHour = view.findViewById(R.id.openHourTV);
        layoutCloseHour = view.findViewById(R.id.closeHoursRelativeLayout);
        textCloseHour = view.findViewById(R.id.closeHourTV);
        buttonDayOffOrWorkingDay = view.findViewById(R.id.dayOffWeekendMB);

        layoutWeekendOptions = view.findViewById(R.id.weekendOptionsRL);

        // chips
        monChip = view.findViewById(R.id.chip_monday);
        tuesChip = view.findViewById(R.id.chip_tuesday);
        wedChip = view.findViewById(R.id.chip_wednesday);
        thurChip = view.findViewById(R.id.chip_thursday);
        friChip = view.findViewById(R.id.chip_friday);
        satChip = view.findViewById(R.id.chip_saturday);
        sunChip = view.findViewById(R.id.chip_sunday);

        buttonSavePreference = view.findViewById(R.id.savePreferencesOpeningHoursMB);
    }

    /**
     * Non - super method
     */
    private void onPageFirstSelected(){
        selectedDayString = DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_MONDAY;
        // first day selected is monday
        // remove "day off" panel
        hasDayOff = false;
        hidePanel(layoutWeekendOptions);
        getHoursForDay(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_MONDAY);
    }

    private void hidePanel(RelativeLayout panel){
        panel.setVisibility(View.GONE);
    }
    private void showPanel(RelativeLayout panel){
        panel.setVisibility(View.VISIBLE);
    }

    private void daysOnClickListener(){
        monChip.setOnClickListener(v ->
            actionForWeekdays(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_MONDAY));
        tuesChip.setOnClickListener(v ->
                actionForWeekdays(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_TUESDAY));
        wedChip.setOnClickListener(v ->
                actionForWeekdays(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_WEDNESDAY));
        thurChip.setOnClickListener(v ->
                actionForWeekdays(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_THURSDAY));
        friChip.setOnClickListener(v ->
                actionForWeekdays(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_FRIDAY));

        satChip.setOnClickListener(v ->
                actionForWeekend(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SATURDAY));
        sunChip.setOnClickListener(v ->
                actionForWeekend(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SUNDAY));
    }

    private void actionForWeekdays(String day){
        selectedDayString = day;
        selectedDay.setText(day);
        hasDayOff = false;
        hidePanel(layoutWeekendOptions);
        showPanel(layoutWeekdays);
        buttonDayOffOrWorkingDay.setText(getActivity().getResources().getString(R.string.day_off));
        getHoursForDay(day);
    }
    private void actionForWeekend(String day){
        selectedDayString = day;
        selectedDay.setText(day);
        hasDayOff = false;
        showPanel(layoutWeekendOptions);
        getHoursForDay(day);
    }

    private void addOnOpenHourClick(){
        layoutOpenHour.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {
                if (selectedMinute <= 9) {
                    //convert to 12 hour view (use parenthesis)
                    if(selectedHour > 12)
                        textOpenHour.setText(String.format("%s:0%s (%s:0%s pm)",
                                selectedHour, selectedMinute, selectedHour - 12, selectedMinute));
                    else
                        textOpenHour.setText(String.format("%s:0%s",
                                selectedHour, selectedMinute));

                }
                else{
                    if(selectedHour > 12)
                        textOpenHour.setText(String.format("%s:%s (%s:%s pm)",
                                selectedHour, selectedMinute, selectedHour - 12, selectedMinute));
                    else
                        textOpenHour.setText(String.format("%s:%s",
                                selectedHour, selectedMinute));
                }

            }, hour, minute, true); //Yes 24 hour time
            mTimePicker.setTitle("Select Time (Open Gym)");
            mTimePicker.show();
        });
    }
    private void addOnCloseHourClick(){
        layoutCloseHour.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(requireContext(),
                    (timePicker, selectedHour, selectedMinute) -> {
                if (selectedMinute <= 9) {
                    //convert to 12 hour view (use parenthesis)
                    if(selectedHour > 12)
                        textCloseHour.setText(String.format("%s:0%s (%s:0%s pm)",
                                selectedHour, selectedMinute, selectedHour - 12, selectedMinute));
                    else
                        textCloseHour.setText(String.format("%s:0%s",
                                selectedHour, selectedMinute));
                }
                else{
                    if(selectedHour > 12)
                        textCloseHour.setText(String.format("%s:%s (%s:%s pm)",
                                selectedHour, selectedMinute, selectedHour - 12, selectedMinute));
                    else
                        textCloseHour.setText(String.format("%s:%s",
                                selectedHour, selectedMinute));
                }

            }, hour, minute, true); //Yes 24 hour time
            mTimePicker.setTitle("Select Time (Close Gym)");
            mTimePicker.show();
        });
    }

    private void addOnDayOffClick(){
        buttonDayOffOrWorkingDay.setOnClickListener(v -> {
            if(hasDayOff) {
                hasDayOff = false;
                showPanel(layoutWeekdays);
                buttonDayOffOrWorkingDay.setText(getActivity().getResources().getString(R.string.day_off));
            }else{
                hasDayOff = true;
                hidePanel(layoutWeekdays);
                buttonDayOffOrWorkingDay.setText(getActivity().getResources().getString(R.string.working_day));
            }
        });
    }

    private void getHoursForDay(String day){
        firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_OPENING_HOURS).
                document(day).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        // if selected day is day off
                        if(task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF) != null
                            && (Boolean) task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF)){
                            hidePanel(layoutWeekdays);
                            buttonDayOffOrWorkingDay.setText(getActivity().
                                    getResources().getString(R.string.working_day));
                            hasDayOff = true;
                        }

                        // if selected day is working day
                        if(task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF) != null
                                && !(Boolean) task.getResult().get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF)){
                            showPanel(layoutWeekdays);
                            buttonDayOffOrWorkingDay.setText(getActivity().
                                    getResources().getString(R.string.day_off));
                        }


                        textOpenHour.setText(task.getResult().
                                get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_OPEN).toString());
                        textCloseHour.setText(task.getResult().
                                get(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_CLOSED).toString());

                    }else {
                        Toast.makeText(getContext(),
                                "Cannot retrieve data from server!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setHoursForDay(boolean isWeekendDay){
        if(validation()){
            CollectionReference openHoursCollectionReference =
                    firebaseFirestore.collection(DatabaseConstants.FIREBASE_COLLECTION_OPENING_HOURS);
            DocumentReference dayDocument =
                    openHoursCollectionReference.document(selectedDayString);

            Map<String, Object> weekdaysMapValues   = new HashMap<>();
            Map<String, Object> weekendMapValues    = new HashMap<>();

            if(isWeekendDay){
                // weekend day
                weekendMapValues.put(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_OPEN,
                        textOpenHour.getText().toString());
                weekendMapValues.put(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_CLOSED,
                        textCloseHour.getText().toString());
                weekendMapValues.put(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF,
                        hasDayOff);
                dayDocument.update(weekendMapValues).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) Toast.makeText(getContext(), "Update time successfully",
                            Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getContext(), "Cannot update time",
                            Toast.LENGTH_SHORT).show();
                });
            }else{
                // weekdays
                weekdaysMapValues.put(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_OPEN,
                        textOpenHour.getText().toString());
                weekdaysMapValues.put(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_CLOSED,
                        textCloseHour.getText().toString());
                dayDocument.update(weekdaysMapValues).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) Toast.makeText(getContext(), "Update time successfully",
                            Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getContext(), "Cannot update time",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void addSavePreferencesOnClick(){
        buttonSavePreference.setOnClickListener(v -> setHoursForDay(
                isDaySaturdayOrSunday(selectedDayString)));
    }

    private boolean isDaySaturdayOrSunday(String day){
        return (day.equalsIgnoreCase(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SATURDAY)
                || day.equalsIgnoreCase(DatabaseConstants.FIREBASE_DOCUMENT_OP_HOURS_SUNDAY));
    }

    private boolean validation() {
        return true;
    }

}