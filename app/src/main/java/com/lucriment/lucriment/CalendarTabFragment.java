package com.lucriment.lucriment;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 6/27/2017.
 */

public class CalendarTabFragment extends Fragment {
    private CompactCalendarView cv;
    private ArrayList<TimeInterval> todaysAvailability = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    private ArrayList<TimeInterval> mondayAva = new ArrayList<>();
    private ArrayList<TimeInterval> tuesdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> wednesdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> thursdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> fridayAva = new ArrayList<>();
    private ArrayList<TimeInterval> saturdayAva = new ArrayList<>();
    private ArrayList<TimeInterval> sundayAva = new ArrayList<>();
    private UserInfo userInfo;
    private String userType;
    private ArrayAdapter<TimeInterval> adapter;
    private Date currentSelectedDate;
    private long currentDate;
    private ArrayList<Event> listOfEvents = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendartab, container, false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");
        cv = (CompactCalendarView) view.findViewById(R.id.calendarView);
        adapter = new TimeTabAdapter(getApplicationContext(),todaysAvailability);
        ListView timeList = (ListView) view.findViewById(R.id.timesList);
        timeList.setAdapter(adapter);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.addCustomTimeButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date clickedDate = new Date();
                long timeToPass = currentSelectedDate.getTime();
                Intent y = new Intent(getApplicationContext(), CustomAvailabilitySelection.class);
                y.putParcelableArrayListExtra("listOfTimes",todaysAvailability);
                y.putExtra("selectedDay",timeToPass);
                y.putExtra("nameOfDay","today");
                y.putExtra("userInfo", userInfo);
                startActivity(y);

            }
        });

        timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

        cv.setUseThreeLetterAbbreviation(true);
        cv.setCurrentSelectedDayBackgroundColor(Color.parseColor("#55ff94"));
        cv.setCurrentDayBackgroundColor(Color.parseColor("#24e7e3"));
        cv.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.FILL_LARGE_INDICATOR);
        cv.setSelected(true);
        cv.setFirstDayOfWeek(Calendar.SUNDAY);

        /*
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);
        cal.add(Calendar.DATE,7); */

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


        cv.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                currentSelectedDate = dateClicked;
                Date date = dateClicked;
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
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


        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("defaultAvailability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("monday")) {
                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                    cal.set(Calendar.HOUR_OF_DAY,0);
                    cal.set(Calendar.MINUTE,0);
                    cal.set(Calendar.SECOND,0);

                    for (DataSnapshot avaSnapShot : dataSnapshot.child("monday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        mondayAva.add(ti);
                    }


                    long todayMillis2 = cal.getTimeInMillis();
                    for(int i = 0; i<26;i++){
                        Event mondayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(mondayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }


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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }


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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }

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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }

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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }

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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }

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
                        Event tuesdayEvent = new Event(Color.GREEN, cal.getTimeInMillis(), "Available this day");
                        cv.addEvent(tuesdayEvent,true);
                        cal.add(Calendar.DATE,7);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getSelectedDayAva(int year, int day, int month) throws ParseException {
        todaysAvailability.clear();

        Calendar cal = Calendar.getInstance();
        String yearS = year + "";
        String dayS;
        String monthS;
        if (day < 10) {
            dayS = "0" + day;
        } else {
            dayS = day + "";
        }
        if (month < 10) {
            monthS = "0" + (month + 1);
        } else {
            monthS = (month + 1) + "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyddMM");
        Date date = sdf.parse(year + "" + dayS + "" + monthS);
        Calendar c = Calendar.getInstance();


        String dayOfWeek = (String) android.text.format.DateFormat.format("EEEE", date);
        long dayTime = date.getTime();
        c.setTimeInMillis(dayTime);


        if (dayOfWeek.equals("Monday")) {
            for (TimeInterval timeInterval : mondayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);


                todaysAvailability.add(finalTimeInterval);



            }

        }
        if (dayOfWeek.equals("Tuesday")) {
            for (TimeInterval timeInterval : tuesdayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);


                todaysAvailability.add(finalTimeInterval);



            }

        }
        if (dayOfWeek.equals("Wednesday")) {
            for (TimeInterval timeInterval : wednesdayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);

                todaysAvailability.add(finalTimeInterval);


            }

        }
        if (dayOfWeek.equals("Thursday")) {
            for (TimeInterval timeInterval : thursdayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);

                todaysAvailability.add(finalTimeInterval);


            }

        }
        if (dayOfWeek.equals("Friday")) {
            for (TimeInterval timeInterval : fridayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);

                todaysAvailability.add(finalTimeInterval);


            }

        }
        if (dayOfWeek.equals("Saturday")) {
            for (TimeInterval timeInterval : saturdayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);

                todaysAvailability.add(finalTimeInterval);


            }

        }
        if (dayOfWeek.equals("Sunday")) {
            for (TimeInterval timeInterval : sundayAva) {
                //GET FROM TIME
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromhour = c2.get(Calendar.HOUR_OF_DAY);
                c2.setTimeInMillis(timeInterval.getFrom());
                int fromminute = c2.get(Calendar.MINUTE);
                String fminuteS = "";
                String fhourS = "";
                if (fromminute < 10) {
                    fminuteS = "0" + fromminute;
                } else {
                    fminuteS = fromminute + "";
                }
                if (fromhour < 10) {
                    fhourS = "0" + fromhour;
                } else {
                    fhourS = fromhour + "";
                }
                TimeInterval todaysInterval = new TimeInterval(timeInterval.getFrom(), timeInterval.getTo());
                SimpleDateFormat fsdf = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalFromDate = fsdf.parse(year + "" + dayS + "" + monthS + "" + fhourS + "" + fminuteS);
                long fromTime = finalFromDate.getTime();
                //GET TO TIME
                Calendar c3 = Calendar.getInstance();
                c3.setTimeInMillis(timeInterval.getTo());
                int tohour = c3.get(Calendar.HOUR_OF_DAY);
                c3.setTimeInMillis(timeInterval.getTo());
                int tominute = c3.get(Calendar.MINUTE);
                String tminuteS = "";
                String thourS = "";
                if (tominute < 10) {
                    tminuteS = "0" + tominute;
                } else {
                    tminuteS = tominute + "";
                }
                if (tohour < 10) {
                    thourS = "0" + tohour;
                } else {
                    thourS = tohour + "";
                }
                SimpleDateFormat fsdf2 = new SimpleDateFormat("yyyyddMMHHmm");
                Date finalToDate = fsdf2.parse(year + "" + dayS + "" + monthS + "" + thourS + "" + tminuteS);
                long toTime = finalToDate.getTime();
                TimeInterval finalTimeInterval = new TimeInterval(fromTime, toTime);

                todaysAvailability.add(finalTimeInterval);


            }

        }
        adapter.notifyDataSetChanged();




    }


}
