package com.lucriment.lucriment;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 6/27/2017.
 */

public class CalendarTabFragment extends Fragment {
    private CalendarView cv;
    private ArrayList<TimeInterval> todaysAvailability = new ArrayList<>();

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendartab, container, false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");
        cv = (CalendarView) view.findViewById(R.id.calendarView);
        adapter = new TimeTabAdapter(getApplicationContext(),todaysAvailability);
        ListView timeList = (ListView) view.findViewById(R.id.timesList);
        timeList.setAdapter(adapter);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                try {
                    getSelectedDayAva(year,dayOfMonth,month);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("defaultAvailability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("monday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("monday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        mondayAva.add(ti);


                    }

                }

                if (dataSnapshot.hasChild("tuesday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("tuesday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        tuesdayAva.add(ti);


                    }

                }
                if (dataSnapshot.hasChild("wednesday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("wednesday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        wednesdayAva.add(ti);


                    }

                }
                if (dataSnapshot.hasChild("thursday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("thursday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        thursdayAva.add(ti);


                    }

                }
                if (dataSnapshot.hasChild("friday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("friday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        fridayAva.add(ti);


                    }

                }
                if (dataSnapshot.hasChild("saturday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("saturday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        saturdayAva.add(ti);


                    }

                }
                if (dataSnapshot.hasChild("sunday")) {
                    for (DataSnapshot avaSnapShot : dataSnapshot.child("sunday").getChildren()) {
                        //  Availability ava = avaSnapShot.getValue(Availability.class);
                        //   avaList.add(ava);
                        TimeInterval ti = avaSnapShot.getValue(TimeInterval.class);
                        sundayAva.add(ti);


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
