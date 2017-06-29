package com.lucriment.lucriment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayAvailability extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private ListView listView;
    private TimeInterval time;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    private ArrayAdapter<TwoItemField> adapter;
    private String day;
    private TwoItemField tif1 = new TwoItemField("Day",day);
    private TwoItemField tif2 = new TwoItemField("From", "Select");
    private TwoItemField tif3 = new TwoItemField("To","Select");
    private TwoItemField tif4 = new TwoItemField("Select","");
    private boolean settingDay = false;
    private boolean settingFrom = false;
    private boolean settingTo = false;
    private ArrayList<TimeInterval> selectedTime = new ArrayList<>();
    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_availability);
        //GET INTENTS
        selectedTime = getIntent().getParcelableArrayListExtra("listOfTimes");
        userInfo = getIntent().getParcelableExtra("userInfo");
        day = getIntent().getStringExtra("nameOfDay");
        tif1 = new TwoItemField("Day",day);
        itemList.add(tif1);
        time = getIntent().getParcelableExtra("day");
        tif2 = new TwoItemField("From", "Select");
        tif2.setData(time.returnFromTime());
        itemList.add(tif2);
        tif3 = new TwoItemField("To","Select");
        tif3.setData(time.returnToTime());
        itemList.add(tif3);
        itemList.add(tif4);
        adapter = new DayAvailability.myListAdapter();
        ListView list = (ListView) findViewById(R.id.dayOptions);

        list.setAdapter(adapter);
        registerEditTimeClick();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(settingFrom) {
            tif2.setData(hourOfDay + ":" + minute);
            adapter.notifyDataSetChanged();
            settingFrom = false;
            TimeInterval ti =selectedTime.get(0);
          //  selectedTime.get(0) = new TimeInterval(2,3);
          //  tif1.setData("");
        }
        if(settingTo){
            tif3.setData(hourOfDay+":"+minute);
            adapter.notifyDataSetChanged();
            settingTo = false;
        }

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

    private void registerEditTimeClick(){
        ListView list = (ListView) findViewById(R.id.dayOptions);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){

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
                        selectedTime.set(0,timeInterval);
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
