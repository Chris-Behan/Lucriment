package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimePickerActivity extends AppCompatActivity  {
    private CalendarView cv;
    private ArrayList<Availability> avaList = new ArrayList<>();
    private ArrayList<Availability> todaysAvailability = new ArrayList<>();
    private TutorInfo tutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        cv = (CalendarView) findViewById(R.id.calendarView2);
        tutor = getIntent().getParcelableExtra("tutor");


        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("Tutors").child(tutor.getID()).child("Availability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    avaList.add(ava);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //Toast.makeText(getApplicationContext(), ""+dayOfMonth, 0).show();
                getSelectedDayAva(year, dayOfMonth, month);

            }
        });



    }


    private void getSelectedDayAva(int year, int day, int month){
        for(Availability ava: avaList ){
           // int montha = month +1;
            if(ava.getDay()== day && ava.getMonth() == month+1 && ava.getYear() == year){
                todaysAvailability.add(ava);
                Toast.makeText(getApplicationContext(), "exists", 0).show();
            }
        }
    }
}
