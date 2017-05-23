package com.lucriment.lucriment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends FragmentActivity implements  View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, FrequencyDialog.NoticeDialogListener {

   private Button selectTimeButton;
    private TextView timeResult;
    private TextView toView;
    private TextView freqView;
    private String frequency;
    private int day, month, year, hour, minute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, hourFinal2,minuteFinal2;
    private boolean fromSet = false;
    private DatabaseReference databaseReference;
    private FrequencyDialog fe = new FrequencyDialog();
    private FirebaseAuth firebaseAuth;
    private ArrayList<Availability> aList = new ArrayList<>();
    private ArrayList<Availability> aList2 = new ArrayList<>();
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        selectTimeButton = (Button) findViewById(R.id.selectTime);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        selectTimeButton.setOnClickListener(this);
        backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Tutors").child(user.getUid()).child("Availability");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aList.clear();
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    aList.add(ava);

                }
                populateScheduleList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.selectTime:
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleActivity.this, ScheduleActivity.this,year,month,day);

                datePickerDialog.show();

            break;
            case R.id.back:
                finish();
                startActivity(new Intent(ScheduleActivity.this, ProfileActivity.class));



        }

    }

    //LOOK OVER THIS, TAKEN FROM STACKOVERFLOW
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month +1;
        dayFinal = dayOfMonth;

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        tpd2();



        CustomTimePickerDialog timePickerDialog2 = new CustomTimePickerDialog(ScheduleActivity.this,ScheduleActivity.this, hour,minute, true);
        timePickerDialog2.setMessage("From");
        timePickerDialog2.show();







    }

    private void tpd2(){
        CustomTimePickerDialog timePickerDialog1 = new CustomTimePickerDialog(ScheduleActivity.this,ScheduleActivity.this,hour,minute, true);
        timePickerDialog1.setMessage("To");
        timePickerDialog1.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(!fromSet) {

            hourFinal = hourOfDay;
            minuteFinal = minute;

            fromSet = true;
        }else{
            hourFinal2 = hourOfDay;
            minuteFinal2 = minute;


            if(hourFinal2 > hourFinal && minuteFinal2 >= minuteFinal) {
                fe.show(getFragmentManager(), "my dialog");
                fromSet = false;
            }else{
                Toast.makeText(ScheduleActivity.this, "Session must be atlesat one hour long", Toast.LENGTH_SHORT).show();

                tpd2();
            }


        }


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        frequency = fe.getSelection();
        uploadAvailability();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
            fromSet = false;
    }

    private void uploadAvailability(){
        Availability availability = new Availability(dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, hourFinal2, minuteFinal2, frequency);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        aList.add(availability);
        databaseReference.child("Tutors").child(user.getUid()).child("Availability").setValue(aList);
    }

    private class myListAdapter extends ArrayAdapter<Availability> {

        public myListAdapter(){
            super(ScheduleActivity.this, R.layout.timecardlayout, aList);
        }


        // @NonNull
        @Override
        public View getView(int position,  View convertView,  ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.timecardlayout, parent, false);
            }

            Availability currentAva = aList.get(position);
           // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView fromText = (TextView) itemView.findViewById(R.id.fromView);
            fromText.setText(currentAva.getFromhour() + ":" + currentAva.getFromminute() + " - " + currentAva.getTohour() + ":"+ currentAva.getTominute());



            TextView dateText = (TextView) itemView.findViewById(R.id.dateView);
            dateText.setText(currentAva.getMonth()+", "+currentAva.getDay()+", "+currentAva.getYear());




            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }



    private void populateScheduleList(){
        //  populateTutorList();
        ArrayAdapter<Availability> adapter = new ScheduleActivity.myListAdapter();
       // ArrayAdapter<TutorInfo> adapter = new TutorListActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.sList);
        list.setAdapter(adapter);
        //adapter.getView();

    }
}
