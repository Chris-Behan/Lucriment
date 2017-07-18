package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayAvailability extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, daySelectDialog.NoticeDialogListener {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_availability);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //GET INTENTS
        //selectedTime = getIntent().getParcelableArrayListExtra("listOfTimes");
        userType = getIntent().getStringExtra("userType");
        userInfo = getIntent().getParcelableExtra("userInfo");
        day = getIntent().getStringExtra("nameOfDay");
        tif1 = new TwoItemField("Day",day);
        itemList.add(tif1);
        if(getIntent().hasExtra("day")) {
            time = getIntent().getParcelableExtra("day");
            tif2.setData(time.returnFromTime());
            tif3.setData(time.returnToTime());
        }
        itemList.add(tif2);
        itemList.add(tif3);
        itemList.add(tif4);

       setDayListListener(day.toLowerCase());


        adapter = new DayAvailability.myListAdapter();
        ListView list = (ListView) findViewById(R.id.dayOptions);

        list.setAdapter(adapter);
        registerEditTimeClick();

    }

    private void setDayListListener(String chosenDay){
        availabilityRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("defaultAvailability").child(chosenDay);
        availabilityRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                selectedTime.clear();
                for(DataSnapshot timeSnap: dataSnapshot.getChildren()){
                    selectedTime.add(timeSnap.getValue(TimeInterval.class));

                }
                populateTimeList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(settingFrom) {
            tif2.setData(hourOfDay + ":" + minute);
            adapter.notifyDataSetChanged();
            settingFrom = false;
//            TimeInterval ti =selectedTime.get(0);
          //  selectedTime.get(0) = new TimeInterval(2,3);
          //  tif1.setData("");
        }
        if(settingTo){
            tif3.setData(hourOfDay+":"+minute);
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
            super(DayAvailability.this, R.layout.session_request_field, itemList);
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

    private class dayTimesAdapter extends ArrayAdapter<TimeInterval> {

        public dayTimesAdapter(){
            super(DayAvailability.this, R.layout.taught_item, selectedTime);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.taught_item, parent, false);
            }
            //Find tutor to work with
            Button deleteButton = (Button) itemView.findViewById(R.id.delete);
            TimeInterval timeslot = selectedTime.get(position);

            //fill the view

            TextView timeSlotText = (TextView) itemView.findViewById(R.id.taughtLabel);
            timeSlotText.setText(timeslot.returnFromTime()+" - " +timeslot.returnToTime());
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTime.remove(position);
                    databaseReference.child("tutors").child(userInfo.getId()).child("defaultAvailability").child(day.toLowerCase()).setValue(selectedTime);
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
        Intent i = new Intent(DayAvailability.this, DefaultAvailability.class);
        i.putExtra("userInfo",userInfo);
        i.putExtra("userType",userType);
        startActivity(i);
        return true;
    }

    private void populateTimeList() {
        adapter2 = new DayAvailability.dayTimesAdapter();
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
                    Bundle args = new Bundle();
                    String[] testArr = getResources().getStringArray(R.array.daysOfWeek);
                    args.putStringArray("days",testArr);
                    daySelectDialog.setArguments(args);
                    daySelectDialog.show(getFragmentManager(), "my dialog");

                }
                if(position ==1) {
                    DayAvailability.CustomTimePickerDialog timePickerDialog2 = new DayAvailability.CustomTimePickerDialog(DayAvailability.this, DayAvailability.this, 9, 9, true);
                    timePickerDialog2.setMessage("From");
                    timePickerDialog2.show();
                    settingFrom = true;


                }
                if(position ==2){
                    DayAvailability.CustomTimePickerDialog timePickerDialog2 = new DayAvailability.CustomTimePickerDialog(DayAvailability.this, DayAvailability.this, 9, 9, true);
                    timePickerDialog2.setMessage("To");
                    timePickerDialog2.show();
                    settingTo = true;
                }

                if(position ==3){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    try {
                        Date fromDate = sdf.parse(tif2.getData().toString());
                        Date toDate = sdf.parse(tif3.getData().toString());
                        long fromTime = fromDate.getTime();
                        long toTime = toDate.getTime();
                        TimeInterval timeInterval = new TimeInterval(fromTime,toTime);
                        for(TimeInterval existingTime:selectedTime){
                            if(timeInterval.getFrom()>=existingTime.getFrom()&&timeInterval.getFrom()<=timeInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }
                            else if(timeInterval.getTo()>=existingTime.getFrom()&&timeInterval.getTo()<=timeInterval.getTo()){
                                Toast.makeText(getApplicationContext(),"The time interval you have entered conflicts with an existing time interval",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        selectedTime.add(timeInterval);
                        adapter2.notifyDataSetChanged();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("defaultAvailability")
                                .child(day.toLowerCase());
                        databaseReference.setValue(selectedTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }




                }
            }
        });
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
                    if (mTimeSetListener != null) {
                        mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                                mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
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
