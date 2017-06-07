package com.lucriment.lucriment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TimePickerActivity extends AppCompatActivity  {
    private CalendarView cv;
    private ArrayList<Availability> avaList = new ArrayList<>();
    private ArrayList<Availability> todaysAvailability = new ArrayList<>();
    private TutorInfo tutor;
    private GridView gridView;
    private GridView gridView2;
    private final ArrayList<String> items = new ArrayList<>();
    private final ArrayList<String> items2 = new ArrayList<>();
    private final ArrayList<String> finishTimes = new ArrayList<>();
    private  final gridAdapter myGridAdapter = new gridAdapter(items);
    private  final gridAdapter myGridAdapter2 = new gridAdapter(items2);
    private String selectedFromTime;
    private String selectedToTime;
    private String selectedSubject;
    private Availability today;
    private String selectedLocation;
    private boolean timeState = true;
    private Date fromdate, todate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra("subject")){
            selectedSubject = getIntent().getStringExtra("subject");
        }
        if(getIntent().hasExtra("location"))
            selectedLocation = getIntent().getStringExtra("location");
        setContentView(R.layout.activity_time_picker);
        cv = (CalendarView) findViewById(R.id.calendarViewx);
        tutor = getIntent().getParcelableExtra("tutor");
        gridView = (GridView) findViewById(R.id.timeGrid);


        gridView.setAdapter(myGridAdapter);

        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutor.getId()).child("availability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    avaList.add(ava);


                }
                    myGridAdapter.notifyDataSetChanged();
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

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(), items.get(position), 0).show();
                    if(timeState) {
                        selectedFromTime = items.get(position);
                        getToTimes(items.get(position));

                        gridView.setAdapter(myGridAdapter2);
                        myGridAdapter.notifyDataSetChanged();
                        timeState= false;
                    }else{

                        selectedToTime = items2.get(position);

                        Intent i = new Intent(TimePickerActivity.this, RequestSessionActivity.class);
                        Date mDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
                        String rMonth = "";
                        String rDay = "";
                        if(today.returnMonth()<10){
                            rMonth = "0"+ today.returnMonth();
                        }else{
                            rMonth = ""+today.returnMonth();
                        }
                        if(today.returnDay()<10){
                            rDay = "0" + today.returnDay();
                        }else{
                            rDay = ""+today.returnDay();
                        }
                        String fromTimeString = ""+today.returnYear()+rMonth+rDay+selectedFromTime;
                        String toTimeString = ""+today.returnYear()+rMonth+rDay+selectedToTime;
                        fromdate = null;
                        todate = null;
                        try {
                            fromdate = sdf.parse(fromTimeString);
                            todate = sdf.parse(toTimeString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long fromTimeInMilis = fromdate.getTime();
                        long toTimeInMilis = todate.getTime();

                        TimeInterval selectedTI = new TimeInterval(fromTimeInMilis,toTimeInMilis);
                        Availability requestedAva = new Availability();
                        i.putExtra("requestedTime", selectedTI);
                        i.putExtra("tutor",tutor);
                        i.putExtra("location", selectedLocation);
                        i.putExtra("subject",selectedSubject);
                        startActivity(i);

                    }
                }
            });



    }

    @TargetApi(Build.VERSION_CODES.N)
    private void getToTimes(String selectedTime) {
        String starttime = selectedTime;
        String hour = starttime.substring(0,starttime.indexOf(':'));
        String minute = starttime.substring(starttime.indexOf(':')+1,starttime.length());
        int startMinute = Integer.valueOf(minute);
        int startHour = Integer.valueOf(hour);
        int timeValue = Integer.valueOf(hour)*60 + Integer.valueOf(minute);
        Availability thisAva = new Availability();
        for(Availability ava:todaysAvailability){
            int avaFromVal = ava.returnFromValue();
            int avaToVal = ava.returnToValue();
            if(timeValue>=avaFromVal && timeValue<=avaToVal){
                thisAva = ava;
            }
        }
        today = thisAva;
        int endTotal = thisAva.returnToValue();
       int endMinute = thisAva.returnToMinute();
       int endHour = thisAva.returnToHour();
       int timeDiff = endTotal - timeValue;
        int increment = (timeDiff-60)/15;

        while(increment>=0){
            String processedTime;
            if(endMinute==0){
                processedTime = endHour + ":00";
               endHour-=1;
                endMinute = 45;
            }else{
                processedTime = endHour + ":" + endMinute;
                endMinute-= 15;
            }
            items2.add(processedTime);
            increment--;

        }
        Collections.reverse(items2);



    }

    private void processStartAvailability(Availability ava){
        int startHour = ava.returnFromHour();
        int startMinute = ava.returnFromMinute();
        int endHour = ava.returnToHour();
        int endMinute = ava.returnToMinute();

        int startTotal = startHour*60 + startMinute;
        int endTotal = endHour*60 + endMinute;
        int timeDiff = endTotal-startTotal;
        int increment = (timeDiff -60)/15;

        while(increment>=0){
            String processedTime;
            if(startMinute<45) {
                if(startMinute==0){
                     processedTime = startHour + ":00";
                }else {
                     processedTime = startHour + ":" + startMinute;
                }
                items.add(processedTime);
                startMinute+= 15;
            }else{
                 processedTime = startHour + ":" + startMinute;
                items.add(processedTime);
                startMinute =0;
                startHour+= 1;
            }
          //  items.add(startHour + ":" + startMinute);
            increment--;
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getSelectedDayAva(int year, int day, int month){
        todaysAvailability.clear();
        items.clear();
        Calendar cal = Calendar.getInstance();

        for(Availability ava: avaList ){

        int rday = ava.returnDay();
            int rmonth = ava.returnMonth();
            int ryear = ava.returnYear();
           // int montha = month +1;
           if(ava.returnDay()== day && ava.returnMonth() == month && ava.returnYear() == year){
                todaysAvailability.add(ava);
                processStartAvailability(ava);
                finishTimes.add(ava.returnToTime());
              //  items.add(ava.returnToTime());

            } 
        }
        myGridAdapter.notifyDataSetChanged();
    }

    private class gridAdapter extends BaseAdapter{
        ArrayList<String> items;

        private gridAdapter(final ArrayList<String> items){
        this.items = items;
        }
        private Context mContext;

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView text = (TextView) view.findViewById(android.R.id.text1);

            text.setText(items.get(position));
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setGravity(view.TEXT_ALIGNMENT_CENTER);
            text.setTextSize(20);
            return view;
        }
    }
}
