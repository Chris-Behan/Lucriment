package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAvailabilitySelection extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, daySelectDialog.NoticeDialogListener {

    private ListView listView;
    private TimeInterval time;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    private ArrayAdapter<TwoItemField> adapter;
    private ArrayAdapter<TimeInterval> adapter2;
    private String day;
    private TwoItemField tif1 = new TwoItemField("Day",day);
    private TwoItemField tif2 = new TwoItemField("From", "Select");
    private TwoItemField tif3 = new TwoItemField("To","Select");
    private TwoItemField tif4 = new TwoItemField("Add Time","");
    private boolean settingDay = false;
    private boolean settingFrom = false;
    private boolean settingTo = false;
    private ArrayList<TimeInterval> selectedTime = new ArrayList<>();
    private UserInfo userInfo;
    private String userType;
    private String daySelection;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private  daySelectDialog daySelectDialog = new daySelectDialog();
    private DatabaseReference availabilityRoot;
    private long selectedDay;
    private  HashMap<String,ArrayList<TimeInterval>> customMap;
    private ArrayList<TimeInterval> customAvas = new ArrayList<>();
    private ArrayList<TimeInterval> defaultAvas = new ArrayList<>();
    private CustomAvailabilitySelection.CustomTimePickerDialog timePickerDialog1, timePickerDialog2;
    private int firstSelectionHour, firstSelectionMinute;
    private boolean firstTimeSet = false;
    private HashMap<String, TimeInterval> innerMap;
    private HashMap<TimeInterval, String> keyMap;
    private ArrayList<String> keys = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_availability);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //GET INTENTS
        //selectedTime = getIntent().getParcelableArrayListExtra("listOfTimes");
        userType = getIntent().getStringExtra("userType");
        userInfo = getIntent().getParcelableExtra("userInfo");
        selectedDay = getIntent().getLongExtra("selectedDay",1);
        day = getIntent().getStringExtra("nameOfDay");
        Date formattedDate = new Date();
        formattedDate.setTime(selectedDay);
        SimpleDateFormat df2 = new SimpleDateFormat("MMMM dd, yyyy");
        String dateText = df2.format(formattedDate);
        System.out.println(dateText);
        tif1 = new TwoItemField("Day",dateText);
        itemList.add(tif1);
        if(getIntent().hasExtra("day")) {
            time = getIntent().getParcelableExtra("day");
            tif2.setData(time.returnFromTime());
            tif3.setData(time.returnToTime());
        }
        itemList.add(tif2);
        itemList.add(tif3);
        itemList.add(tif4);

        DatabaseReference customAvaialability = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("customAvailability");
        customAvaialability.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                selectedTime.clear();
                customMap = new HashMap<String, ArrayList<TimeInterval>>();
                innerMap = new HashMap<String, TimeInterval>();
                keyMap = new HashMap<TimeInterval, String>();
                for(DataSnapshot customTime:dataSnapshot.getChildren()){
                    ArrayList<TimeInterval> availabilities = new ArrayList<TimeInterval>();
                    for(DataSnapshot innerTime:customTime.getChildren()){
                        availabilities.add(innerTime.getValue(TimeInterval.class));
                        //customMap.put(customTime.getKey(),innerTime.getValue(TimeInterval.class));
                        innerMap.put(innerTime.getKey(),innerTime.getValue(TimeInterval.class));
                        keyMap.put(innerTime.getValue(TimeInterval.class),innerTime.getKey());
                        keys.add(innerTime.getKey());
                    }

                    customMap.put(customTime.getKey(),availabilities);

                }

                if(!customMap.containsKey(selectedDay+"")){
                    setDayListListener(day.toLowerCase());
                }else{
                    if(customMap.containsKey(selectedDay+"")) {
                        for (TimeInterval ti : customMap.get(selectedDay + "")) {
                            selectedTime.add(ti);
                            customAvas.add(ti);
                        }
                    }
                    sortTimes();
                    populateTimeList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        adapter = new CustomAvailabilitySelection.myListAdapter();
        ListView list = (ListView) findViewById(R.id.dayOptions);

        list.setAdapter(adapter);
        registerEditTimeClick();



    }

    private void setDayListListener(String chosenDay){
        availabilityRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("defaultAvailability").child(chosenDay);
        availabilityRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    selectedTime.clear();
                Calendar re1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Calendar re2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                for(DataSnapshot timeSnap: dataSnapshot.getChildren()){
               /*     TimeInterval ti = timeSnap.getValue(TimeInterval.class);
                    re1.setTimeInMillis(selectedDay);
                    re2.setTimeInMillis(selectedDay);
                    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
                    sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String fromString = sdf3.format(ti.getFrom());
                    String toString = sdf3.format(ti.getTo());
                    re1.add(Calendar.MINUTE,hoursAndMinutesToMinutes(fromString));
                    re2.add(Calendar.MINUTE,hoursAndMinutesToMinutes(toString));
                    long fromLong = re1.getTimeInMillis();
                    long toLong =re2.getTimeInMillis();
                    TimeInterval defaultToCustom = new TimeInterval(fromLong, toLong);
                    selectedTime.add(defaultToCustom); */
                    selectedTime.add(timeSnap.getValue(TimeInterval.class));
                    defaultAvas.add(timeSnap.getValue(TimeInterval.class));

                }
                if(customMap.containsKey(selectedDay+"")) {
                    for (TimeInterval ti : customMap.get(selectedDay + "")) {
                        selectedTime.add(ti);
                        customAvas.add(ti);
                    }
                }
                sortTimes();
                populateTimeList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //DatabaseReference dbr4 = FirebaseDatabase


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if(settingFrom) {
            if(minute<10){
                String min = "0"+minute;
                tif2.setData(hourOfDay + ":" + min);
            }else {
                tif2.setData(hourOfDay + ":" + minute);
            }
            adapter.notifyDataSetChanged();
            tif3.setData("");
            settingFrom = false;
//            TimeInterval ti =selectedTime.get(0);
            //  selectedTime.get(0) = new TimeInterval(2,3);
            //  tif1.setData("");
        }
        if(settingTo){
            if(minute<10){
                String min = "0"+minute;
                tif3.setData(hourOfDay + ":" + min);
            }else {
                tif3.setData(hourOfDay + ":" + minute);
            }
            adapter.notifyDataSetChanged();
            settingTo = false;
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        daySelection = daySelectDialog.getSelection();
        day = daySelection.toLowerCase();
        setDayListListener(day);
        tif1 = new TwoItemField("Day",daySelection);
        itemList.set(0,tif1);
        adapter.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(CustomAvailabilitySelection.this, R.layout.session_request_field, itemList);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.session_request_field, parent, false);
            }
            TwoItemField currentTwo = itemList.get(position);
            //Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView category = (TextView) itemView.findViewById(R.id.category);
            category.setText(currentTwo.getLabel());


            TextView dataText = (TextView) itemView.findViewById(R.id.input);
            dataText.setText(currentTwo.getData());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    private void sortTimes(){
        Collections.sort(selectedTime, new TimeComparator());

    }


    private class dayTimesAdapter extends ArrayAdapter<TimeInterval> {

        public dayTimesAdapter(){
            super(CustomAvailabilitySelection.this, R.layout.taught_item, selectedTime);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.taught_item, parent, false);
            }
            //Find tutor to work with
            Button deleteButton = (Button) itemView.findViewById(R.id.delete);
            final TimeInterval timeslot = selectedTime.get(position);

            //fill the view

            TextView timeSlotText = (TextView) itemView.findViewById(R.id.taughtLabel);
            timeSlotText.setText(timeslot.amPmTime());
            if(customAvas.isEmpty()){
                deleteButton.setVisibility(View.INVISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                timeSlotText.setText(sdf.format(timeslot.getFrom())+" - "+sdf.format(timeslot.getTo()));
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            String keyToDelete = "";
                    for(String key:keys){
                        TimeInterval currentTi = innerMap.get(key);
                     //   long from1 = currentTi.getFrom();
                     //   long from2 = timeslot.getFrom();
                        if(currentTi.getFrom()==timeslot.getFrom()){
                            keyToDelete = key;

                        }
                    }

                    selectedTime.remove(position);
                        keys.remove(keyToDelete);
                        customAvas.remove(timeslot);
                        databaseReference.child("tutors").child(userInfo.getId()).child("customAvailability").child(selectedDay + "").child(keyToDelete).removeValue();

                    adapter2.notifyDataSetChanged();
                }
            });

            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(CustomAvailabilitySelection.this, DefaultAvailability.class);
        i.putExtra("userInfo",userInfo);
        i.putExtra("userType",userType);
        startActivity(i);
        return true;
    }

    private void populateTimeList() {
        adapter2 = new CustomAvailabilitySelection.dayTimesAdapter();
        ListView list = (ListView) findViewById(R.id.selectedDaysTimes);
        list.setAdapter(adapter2);
    }



    private void registerEditTimeClick(){
        ListView list = (ListView) findViewById(R.id.dayOptions);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    /*
                    Bundle args = new Bundle();
                    String[] testArr = getResources().getStringArray(R.array.daysOfWeek);
                    args.putStringArray("days",testArr);
                    daySelectDialog.setArguments(args);
                    daySelectDialog.show(getFragmentManager(), "my dialog"); */

                }
                if(position ==1) {
                    timePickerDialog1 = new CustomAvailabilitySelection.CustomTimePickerDialog(CustomAvailabilitySelection.this, CustomAvailabilitySelection.this, 9, 9, true);
                    timePickerDialog1.setMessage("From");
                    timePickerDialog1.show();
                    firstTimeSet = false;
                    settingFrom = true;


                }
                if(position ==2){
                    timePickerDialog2 = new CustomAvailabilitySelection.CustomTimePickerDialog(CustomAvailabilitySelection.this, CustomAvailabilitySelection.this, firstSelectionHour+1,
                            firstSelectionMinute, true);
                    timePickerDialog2.setMessage("To");
                    timePickerDialog2.show();
                    settingTo = true;
                }

                if(position ==3){


                        Date todaysDate = new Date();
                        todaysDate.setTime(selectedDay);
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        cal2.setTime(todaysDate);
                        cal.setTime(todaysDate);
                    if(tif2.getData().equals("Select")||tif2.getData().length()<2){
                        Toast.makeText(getApplicationContext(),"Please enter From time",Toast.LENGTH_LONG).show();
                        return;
                    }else  if(tif3.getData().equals("Select")||tif3.getData().length()<2){
                        Toast.makeText(getApplicationContext(),"Please enter To time",Toast.LENGTH_LONG).show();
                        return;
                    }

                        cal.add(Calendar.MINUTE, hoursAndMinutesToMinutes(tif2.getData().toString()));
                        cal2.add(Calendar.MINUTE, hoursAndMinutesToMinutes(tif3.getData().toString()));

                        TimeInterval timeInterval = new TimeInterval(cal.getTimeInMillis(),cal2.getTimeInMillis());
                        Calendar cal3 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        Calendar cal4 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        if(cal.getTimeInMillis()<=cal3.getTimeInMillis()){
                            Toast.makeText(getApplicationContext(),"Availability cannot be set in the past.",Toast.LENGTH_LONG).show();
                            return;
                        }
                       // cal4.setTime(todaysDate);
                        //cal3.setTime(todaysDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String hourAndMin1 = sdf.format(cal.getTimeInMillis());
                    String hourAndMin2 = sdf.format(cal2.getTimeInMillis());
                    try {

                        Date fromDate = sdf.parse(hourAndMin1);
                        Date toDate = sdf.parse(hourAndMin2);
                        cal.add(Calendar.MILLISECOND, (int) fromDate.getTime());
                        long testr = cal.getTimeInMillis();
                        TimeInterval testInterval = new TimeInterval(fromDate.getTime(),toDate.getTime());

                        for(TimeInterval ti: selectedTime) {



                            cal3.setTimeInMillis( ti.getFrom());
                            cal4.setTimeInMillis( ti.getTo());
                            TimeInterval defaultInterval = new TimeInterval(cal3.getTimeInMillis(),cal4.getTimeInMillis());
                            //COMPARE WITH DEFUALT AVAS
                            if(testInterval.getFrom()>defaultInterval.getFrom()&&testInterval.getFrom()<defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }else if(testInterval.getTo()>defaultInterval.getFrom()&&testInterval.getTo()<defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }else if(testInterval.getFrom()<=defaultInterval.getFrom()&&testInterval.getTo()>=defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;

                            }
                            //COMPARE WITH CUSTOM AVAS
                            if(timeInterval.getFrom()>defaultInterval.getFrom()&&timeInterval.getFrom()<defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }else if(timeInterval.getTo()>defaultInterval.getFrom()&&timeInterval.getTo()<defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }else if(timeInterval.getFrom()<=defaultInterval.getFrom()&&timeInterval.getTo()>=defaultInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;

                            }


                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("customAvailability")
                            .child(""+selectedDay);

                    if(!defaultAvas.isEmpty()){

                        Calendar re1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        Calendar re2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                        re1.setTimeInMillis(selectedDay);
                        re2.setTimeInMillis(selectedDay);

                        for(TimeInterval ti: defaultAvas){
                            re1.setTimeInMillis(selectedDay);
                            re2.setTimeInMillis(selectedDay);
                            SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
                            sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String fromString = sdf3.format(ti.getFrom());
                            String toString = sdf3.format(ti.getTo());
                            re1.add(Calendar.MINUTE,hoursAndMinutesToMinutes(fromString));
                            re2.add(Calendar.MINUTE,hoursAndMinutesToMinutes(toString));
                            long fromLong = re1.getTimeInMillis();
                            long toLong =re2.getTimeInMillis();
                            TimeInterval defaultToCustom = new TimeInterval(fromLong, toLong);
                            databaseReference.push().setValue(defaultToCustom);
                        }

                    }


                        selectedTime.add(timeInterval);
                        adapter2.notifyDataSetChanged();


                        databaseReference.push().setValue(timeInterval);
                    Toast.makeText(CustomAvailabilitySelection.this,"Availability added for:" +timeInterval.returnFormattedDate()+" "
                            ,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CustomAvailabilitySelection.this, DefaultAvailability.class);
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo",userInfo);
                    startActivity(i);




                }
            }
        });
    }

    private double hoursAndMinutesToHours(String timeString){
        int value1 = Integer.valueOf(timeString.substring(0,timeString.indexOf(':')));
        int value2 = Integer.valueOf(timeString.substring(timeString.indexOf(':'),timeString.length()));
        value2 = value2/60;
        return value1+value2;
    }
    private int hoursAndMinutesToMinutes(String timeString){
        int value1 = Integer.valueOf(timeString.substring(0,timeString.indexOf(':')));
        int value2 = Integer.valueOf(timeString.substring(timeString.indexOf(':')+1,timeString.length()));
        value1 = value1*60;
        return value1+value2;
    }

    private class CustomTimePickerDialog extends TimePickerDialog {

        private final static int TIME_PICKER_INTERVAL = 15;
        private TimePicker mTimePicker;
        private final OnTimeSetListener mTimeSetListener;


        public CustomTimePickerDialog(Context context, OnTimeSetListener listener,
                                      int hourOfDay, int minute, boolean is24HourView) {
            super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, hourOfDay,
                    minute / TIME_PICKER_INTERVAL, is24HourView);
            mTimeSetListener = listener;
        }

        @Override
        public void updateTime(int hourOfDay, int minuteOfHour) {
            mTimePicker.setCurrentHour(hourOfDay);
            mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                if(firstTimeSet) {
                    double firstSelectionString = hoursAndMinutesToMinutes(firstSelectionHour + ":" + firstSelectionMinute);
                    double secondSelectionString = hoursAndMinutesToMinutes(mTimePicker.getCurrentHour() + ":" + mTimePicker.getCurrentMinute());
                    if (secondSelectionString < firstSelectionString + 60) {
                        Toast.makeText(CustomAvailabilitySelection.this, "Invalid Time Selection", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                    if (mTimeSetListener != null) {
                        mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                                mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                        if(!firstTimeSet) {
                            firstSelectionHour = mTimePicker.getCurrentHour();
                            firstSelectionMinute = mTimePicker.getCurrentMinute();
                            firstTimeSet = true;
                        }

                    }
                    break;
                case BUTTON_NEGATIVE:
                    cancel();
                    break;
            }
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");
                Field timePickerField = classForid.getField("timePicker");
                mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));

                Field field = classForid.getField("minute");

                NumberPicker minuteSpinner = (NumberPicker) mTimePicker
                        .findViewById(field.getInt(null));
                minuteSpinner.setMinValue(0);
                minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
                List<String> displayedValues = new ArrayList<>();
                for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                    displayedValues.add(String.format("%02d", i));
                }
                minuteSpinner.setDisplayedValues(displayedValues
                        .toArray(new String[displayedValues.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
