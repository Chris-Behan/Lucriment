package com.lucriment.lucriment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class TimePickerActivity extends AppCompatActivity  {
    private CompactCalendarView cv;
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
    private UserInfo userInfo;
    private String userType;
    private ArrayList<TimeInterval> mondayAva = new ArrayList<>();
    private ArrayList<TimeInterval> tuesdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> wednesdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> thursdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> fridayAva = new ArrayList<>();
    private ArrayList<TimeInterval> saturdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> sundayAva = new ArrayList<>();
    private ArrayList<TimeInterval> selection = new ArrayList<>();
    private ArrayList<TimeInterval> bookedSessions = new ArrayList<>();
    private  HashMap<String,ArrayList<TimeInterval>> customMap;
    private Button nextButton;
    private double score;
    private long clickedTime;
    private int selectedPostision = 999;
    private TextView selectionOrder;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        score = getIntent().getDoubleExtra("tutorScore",0);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        if(getIntent().hasExtra("subject")){
            selectedSubject = getIntent().getStringExtra("subject");
        }
        if(getIntent().hasExtra("location"))
            selectedLocation = getIntent().getStringExtra("location");

        cv = (CompactCalendarView) findViewById(R.id.calendarView);
        tutor = getIntent().getParcelableExtra("tutor");

        selectionOrder = (TextView) findViewById(R.id.selectionLabel);

        gridView = (GridView) findViewById(R.id.timeGrid);
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeState) {
                    gridView.setAdapter(myGridAdapter2);
                    myGridAdapter.notifyDataSetChanged();
                    timeState = false;
                    selectionOrder.setText("Set Session End Time");
                    selectedPostision = 888;
                }else{

                    Intent i = new Intent(TimePickerActivity.this, RequestSessionActivity.class);
                    Date mDate = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
                    String rMonth = "";
                    String rDay = "";
                    if(today.returnMonth()<10){
                        rMonth = "0"+ (today.returnMonth()+1);
                    }else{
                        rMonth = ""+(today.returnMonth()+1);
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
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo",userInfo);
                    i.putExtra("tutorScore",score);
                    startActivity(i);


                }
            }
        });


        gridView.setAdapter(myGridAdapter);

        //GET BOOKED SESSIONS
        DatabaseReference bookedRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutor.getId()).child("bookedSessions");
        bookedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot bookedSession:dataSnapshot.getChildren()){
                    TimeInterval booked = bookedSession.getValue(TimeInterval.class);
                    bookedSessions.add(booked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference customAvaialability = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutor.getId()).child("customAvailability");
        customAvaialability.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customMap = new HashMap<String, ArrayList<TimeInterval>>();

                for(DataSnapshot customTime:dataSnapshot.getChildren()){
                    ArrayList<TimeInterval> availabilities = new ArrayList<TimeInterval>();
                    for(DataSnapshot innerTime:customTime.getChildren()){
                        availabilities.add(innerTime.getValue(TimeInterval.class));

                            Event customEvent = new Event(Color.GREEN, innerTime.getValue(TimeInterval.class).getFrom(), "Available this day");
                            cv.addEvent(customEvent,true);


                        //customMap.put(customTime.getKey(),innerTime.getValue(TimeInterval.class));
                    }
                    customMap.put(customTime.getKey(),availabilities);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutor.getId()).child("defaultAvailability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                if (dataSnapshot.hasChild("monday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("monday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        mondayAva.add(ti);
                    }

                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(mondayAva,new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }

                if (dataSnapshot.hasChild("tuesday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("tuesday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        tuesdayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(tuesdayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.hasChild("wednesday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("wednesday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        wednesdayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(wednesdayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.hasChild("thursday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("thursday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        thursdayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(thursdayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.hasChild("friday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("friday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        fridayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(fridayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.hasChild("saturday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("saturday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        saturdayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(saturdayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                if (dataSnapshot.hasChild("sunday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("sunday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        sundayAva.add(ti);
                    }
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }
                    Collections.sort(sundayAva, new timeComparator());
                    myGridAdapter.notifyDataSetChanged();
                }
                initializeGrid();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        cv.setUseThreeLetterAbbreviation(true);
        cv.setCurrentSelectedDayBackgroundColor(Color.parseColor("#55ff94"));
        cv.setCurrentDayBackgroundColor(Color.parseColor("#24e7e3"));
        cv.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.FILL_LARGE_INDICATOR);
        cv.setSelected(true);
        cv.setFirstDayOfWeek(Calendar.SUNDAY);

        cv.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDayClick(Date dateClicked) {
                Date date = dateClicked;
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                clickedTime = cal.getTimeInMillis();
                int year = cal.get(Calendar.YEAR);
                int dayOfMonth = cal.get(Calendar.DATE);
                int month = cal.get(Calendar.MONTH);
                try {
                    getSelectedDayAva(year,dayOfMonth,month);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

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
                        selectedPostision = position;
                        myGridAdapter.notifyDataSetChanged();
                      //  gridView.setAdapter(myGridAdapter2);
                    //    myGridAdapter.notifyDataSetChanged();
                     //   timeState= false;
                    }else{

                        selectedToTime = items2.get(position);
                        selectedPostision = position;
                        myGridAdapter2.notifyDataSetChanged();


                    }
                }
            });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        Intent i = new Intent(this, RequestSessionActivity.class);
        i.putExtra("tutor",tutor);
        i.putExtra("location", selectedLocation);
        i.putExtra("subject",selectedSubject);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        i.putExtra("tutorScore",score);
        startActivity(i);
        return true;
    }
    private class timeComparator implements Comparator<TimeInterval> {
        public int compare(TimeInterval ti1, TimeInterval ti2) {
            return (int) (ti1.getFrom() - ti2.getFrom());
        }
    }



    @TargetApi(Build.VERSION_CODES.N)
    private void getToTimes(String selectedTime) {
        items2.clear();
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
    private void getSelectedDayAva(int year, int day, int month) throws ParseException {
        todaysAvailability.clear();
        items.clear();
        if(customMap.containsKey(clickedTime+"")){

            ArrayList<TimeInterval> customDayTimes = customMap.get(clickedTime+"");
            for(TimeInterval cdt:customDayTimes){
                Availability innerAva = new Availability(cdt,"");
                getAlteredAvailability(innerAva);
            }
        }

        Calendar cal = Calendar.getInstance();
        String yearS = year+"";
        String dayS;
        String monthS;
        if(day<10){
            dayS = "0"+day;
        }else{
            dayS = day+"";
        }
        if(month<10){
            monthS = "0"+(month+1);
        }else{
            monthS = (month+1)+"";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyddMM");
        Date date = sdf.parse(year+""+dayS+""+monthS);
        Calendar c = Calendar.getInstance();


        String dayOfWeek = (String) android.text.format.DateFormat.format("EEEE", date);
        long dayTime = date.getTime();
        c.setTimeInMillis(dayTime);


        if(dayOfWeek.equals("Monday")){
            for(TimeInterval timeInterval: mondayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                     fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                     fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Tuesday")){
            for(TimeInterval timeInterval: tuesdayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Wednesday")){
            for(TimeInterval timeInterval: wednesdayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Thursday")){
            for(TimeInterval timeInterval: thursdayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Friday")){
            for(TimeInterval timeInterval: fridayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Saturday")){
            for(TimeInterval timeInterval: saturdayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
                getAlteredAvailability(thisAva);


            }

        }
        if(dayOfWeek.equals("Sunday")){
            for(TimeInterval timeInterval: sundayAva){
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if(fromminute<10){
                    fminuteS = "0"+fromminute;
                }else{
                    fminuteS = fromminute+"";
                }
                if(fromhour<10){
                    fhourS = "0"+fromhour;
                }else{
                    fhourS = fromhour+"";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(),timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year+""+dayS+""+monthS+""+fhourS+""+fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if(tominute < 10){
                    tminuteS = "0"+tominute;
                }else{
                    tminuteS = tominute + "";
                }
                if(tohour<10){
                    thourS = "0" + tohour;
                }else{
                    thourS = tohour+"";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year+""+dayS+""+monthS+""+thourS+""+tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime,toTime);

                Availability thisAva = new Availability(finalTimeInterval,"");
               // todaysAvailability.add(thisAva);
                getAlteredAvailability(thisAva);
                //processStartAvailability(thisAva);


            }

        }

        
        
        /*
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
        }*/
        myGridAdapter.notifyDataSetChanged();
    }

    private void getAlteredAvailability(Availability ava){
        TimeInterval ti = ava.gettime();

        for(TimeInterval bookedTi:bookedSessions){
            if(bookedTi.getFrom()>=ti.getFrom()&&bookedTi.getTo()<=ti.getTo()){
                if((bookedTi.getFrom()-ti.getFrom())>=3600000){
                    TimeInterval firstInterval = new TimeInterval(ti.getFrom(),bookedTi.getFrom());
                    Availability firstAva = new Availability(firstInterval,"");
                    processStartAvailability(firstAva);
                    todaysAvailability.add(firstAva);
                }
                if(ti.getTo()-bookedTi.getTo()>=3600000){
                    TimeInterval firstInterval = new TimeInterval(bookedTi.getTo(),ti.getTo());
                    Availability secondAva = new Availability(firstInterval,"");
                    processStartAvailability(secondAva);
                    todaysAvailability.add(secondAva);
                }
                return;
            }else if(bookedTi.getFrom()>=ti.getFrom()&&bookedTi.getFrom()<=ti.getTo()){
                if(ti.getFrom()-bookedTi.getFrom()>=3600000){
                    TimeInterval firstInterval = new TimeInterval(ti.getFrom(),bookedTi.getFrom());
                    Availability secondAva = new Availability(firstInterval,"");
                    processStartAvailability(secondAva);
                    todaysAvailability.add(secondAva);
                }
                return;
            }else if(bookedTi.getTo()>=ti.getFrom()&&bookedTi.getTo()<=ti.getTo()){
                if(ti.getTo()-bookedTi.getTo()>=3600000){
                    TimeInterval firstInterval = new TimeInterval(bookedTi.getTo(),ti.getTo());
                    Availability secondAva = new Availability(firstInterval,"");
                    processStartAvailability(secondAva);
                    todaysAvailability.add(secondAva);
                }
                return;
            }
        }

            processStartAvailability(ava);
            todaysAvailability.add(ava);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeGrid(){
        Date initDate = new Date();
        initDate.setTime(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(initDate);
        int year = cal.get(Calendar.YEAR);
        int dayOfMonth = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);

        try {
            getSelectedDayAva(year,dayOfMonth,month);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
            text.setBackgroundColor(Color.WHITE);
            if(position==selectedPostision){
                text.setBackgroundColor(Color.parseColor("#1DB7F6"));
            }

            text.setText(items.get(position));
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setGravity(Gravity.CENTER);
            text.setTextSize(20);
            return view;
        }
    }
}
