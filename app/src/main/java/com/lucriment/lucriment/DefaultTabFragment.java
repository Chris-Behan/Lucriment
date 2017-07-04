package com.lucriment.lucriment;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 6/27/2017.
 */

public class DefaultTabFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ListView listView;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<TimeInterval>> expandableListDetail;
    private UserInfo userInfo;
    private String userType;
   // private HashMap<String, ArrayList<TwoItemField>> defaultAvailability;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    private ArrayList<TimeInterval> mondayTime = new ArrayList<>();
    private ArrayList<TimeInterval> tuesdayTime = new ArrayList<>();
    private ArrayList<TimeInterval> wednesdayTime = new ArrayList<>();
    private ArrayList<TimeInterval> thursdayTime = new ArrayList<>();
    private ArrayList<TimeInterval> fridayTime = new ArrayList<>();
    private ArrayList<TimeInterval> saturdayTime = new ArrayList<>();
    private ArrayList<TimeInterval> sundayTime = new ArrayList<>();
    private TwoItemField Monday = new TwoItemField("Monday", "Select");
    private TwoItemField Tuesday = new TwoItemField("Tuesday", "Select");
    private TwoItemField Wednesday = new TwoItemField("Wednesday", "Select");
    private TwoItemField Thursday = new TwoItemField("Thursday", "Select");
    private TwoItemField Friday = new TwoItemField("Friday", "Select");
    private TwoItemField Saturday = new TwoItemField("Saturday", "Select");
    private TwoItemField Sunday = new TwoItemField("Sunday", "Select");
    private TwoItemField Select = new TwoItemField("Select","");
    private  ArrayAdapter<TwoItemField> adapter;
    private LinkedHashMap<String, List<TimeInterval>> dayAvailablities = new LinkedHashMap<String, List<TimeInterval>>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.defaulttab, container,false);
        Bundle args = getArguments();
       // int index = args.getInt("index", 0);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tutors").child(userInfo.getId()).child("defaultAvailability").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(dataSnapshot.hasChild("monday")){
                  DataSnapshot mondaySnap = dataSnapshot.child("monday");
                  for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                          mondayTime.add(tif);
                            Calendar time = Calendar.getInstance();
                            TimeInterval t1 = mondayTime.get(0);
                            String c = t1.returnFromTime()+"-"+t1.returnToTime();
                            Monday.setData(c);
                     // itemList.add(tif);
                  }


              }
                if(dataSnapshot.hasChild("tuesday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("tuesday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        tuesdayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Tuesday.setData(c);
                        // itemList.add(tif);
                    }


                }
                if(dataSnapshot.hasChild("wednesday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("wednesday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        wednesdayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Wednesday.setData(c);

                        // itemList.add(tif);
                    }


                }
                if(dataSnapshot.hasChild("thursday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("thursday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        thursdayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Thursday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("friday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("friday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        fridayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Friday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("saturday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("saturday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        saturdayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Saturday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("sunday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("sunday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        sundayTime.add(tif);
                        String c = tif.returnFromTime() + "-" + tif.returnToTime();

                        Sunday.setData(c);
                    }


                }
                dayAvailablities.put("Monday",mondayTime);
                dayAvailablities.put("Tuesday",tuesdayTime);
                dayAvailablities.put("Wednesday",wednesdayTime);
                dayAvailablities.put("Thursday",thursdayTime);
                dayAvailablities.put("Friday",fridayTime);
                dayAvailablities.put("Saturday",saturdayTime);
                dayAvailablities.put("Sunday",sundayTime);
                itemList.add(Monday);
                itemList.add(Tuesday);
                itemList.add(Wednesday);
                itemList.add(Thursday);
                itemList.add(Friday);
                itemList.add(Saturday);
                itemList.add(Sunday);

                adapter.notifyDataSetChanged();

                expandableListDetail = dayAvailablities;
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter = new CustomExpandableListAdapter(getApplicationContext(),expandableListTitle,expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);
                for(int i = 0; i<expandableListAdapter.getGroupCount(); i++){
                    expandableListView.expandGroup(i);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                i.putExtra("nameOfDay", expandableListTitle.get(groupPosition));
                if(expandableListDetail.get(expandableListTitle.get(groupPosition)).size()>0) {
                    i.putExtra("day", expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                }
                i.putExtra("listOfTimes", mondayTime);
                i.putExtra("userInfo", userInfo);
                startActivity(i);
                return false;
            }
        });





        adapter = new DaySelectAdapter(getApplicationContext(),itemList);
  /*    //  ListView dayList = (ListView) view.findViewById(R.id.dayList);
        dayList.setAdapter(adapter);
        dayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Monday");
                    if(mondayTime.size()>0) {
                        i.putExtra("day", mondayTime.get(0));
                    }
                    i.putExtra("listOfTimes", mondayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 1){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Tuesday");
                    if(tuesdayTime.size()>0) {
                        i.putExtra("day", tuesdayTime.get(0));
                    }
                    i.putExtra("listOfTimes", tuesdayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 2){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Wednesday");
                    if(wednesdayTime.size()>0) {
                        i.putExtra("day", wednesdayTime.get(0));
                    }
                    i.putExtra("listOfTimes", wednesdayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 3){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Thursday");
                    if(thursdayTime.size()>0) {
                        i.putExtra("day", thursdayTime.get(0));
                    }
                    i.putExtra("listOfTimes", thursdayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 4){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Friday");
                    if(fridayTime.size()>0) {
                        i.putExtra("day", fridayTime.get(0));
                    }
                    i.putExtra("listOfTimes", fridayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 5){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Saturday");
                    if(saturdayTime.size()>0) {
                        i.putExtra("day", saturdayTime.get(0));
                    }
                    i.putExtra("listOfTimes", saturdayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }
                if(position == 6){
                    Intent i = new Intent(getApplicationContext(), DayAvailability.class);
                    i.putExtra("nameOfDay", "Sunday");
                    if(sundayTime.size()>0) {
                        i.putExtra("day", sundayTime.get(0));
                    }
                    i.putExtra("listOfTimes", sundayTime);
                    i.putExtra("userInfo", userInfo);
                    startActivity(i);
                }

            }
        }); */
//        registerDayClicks();



       return view;
    }
    private void processDefaultAvailability(){
        //if(defaultAvailability.containsKey("monday")){

       // }
    }
    private void registerDayClicks(){
/*
        ListView list = (ListView) getView().findViewById(R.id.dayList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DayAvailability.class);

                startActivity(i);
            }
        });
        */
    }







}
