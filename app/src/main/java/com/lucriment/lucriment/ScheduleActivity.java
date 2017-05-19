package com.lucriment.lucriment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        selectTimeButton = (Button) findViewById(R.id.selectTime);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        selectTimeButton.setOnClickListener(this);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Tutors").child(user.getUid()).child("Availability");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    aList.add(ava);

                }

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


        TimePickerDialog timePickerDialog1 = new TimePickerDialog(ScheduleActivity.this,AlertDialog.THEME_HOLO_LIGHT, ScheduleActivity.this, hour,minute, true);
        timePickerDialog1.setMessage("To");
        timePickerDialog1.show();

        TimePickerDialog timePickerDialog2 = new TimePickerDialog(ScheduleActivity.this,AlertDialog.THEME_HOLO_LIGHT, ScheduleActivity.this, hour,minute, true);
        timePickerDialog2.setMessage("From");
        timePickerDialog2.show();







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


            fe.show(getFragmentManager(), "my dialog");
            fromSet = false;


        }


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        frequency = fe.getSelection();
        uploadAvailability();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void uploadAvailability(){
        Availability availability = new Availability(dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal, hourFinal2, minuteFinal2, frequency);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        aList.add(availability);
        databaseReference.child("Tutors").child(user.getUid()).child("Availability").setValue(aList);
    }
}
