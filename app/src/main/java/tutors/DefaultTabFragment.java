package tutors;

import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import misc.CustomExpandableListAdapter;
import misc.DaySelectAdapter;
import com.lucriment.lucriment.R;
import sessions.TimeInterval;
import misc.TwoItemField;
import students.UserInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
    private FloatingActionButton floatingActionButton;
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

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.addCustomTimeButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date clickedDate = new Date();

                Intent y = new Intent(getApplicationContext(), DayAvailability.class);


                y.putExtra("nameOfDay","Monday");
                y.putExtra("userInfo", userInfo);
                y.putExtra("userType",userType);
                startActivity(y);

            }
        });

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
                      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                      String c = t1.amPmTime();
                           // String c = t1.returnFromTime()+"-"+t1.returnToTime();
                            Monday.setData(c);
                     // itemList.add(tif);
                  }


              }
                if(dataSnapshot.hasChild("tuesday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("tuesday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        tuesdayTime.add(tif);
                        String c = tif.amPmTime();

                        Tuesday.setData(c);
                        // itemList.add(tif);
                    }


                }
                if(dataSnapshot.hasChild("wednesday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("wednesday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        wednesdayTime.add(tif);
                        String c = tif.amPmTime();

                        Wednesday.setData(c);

                        // itemList.add(tif);
                    }


                }
                if(dataSnapshot.hasChild("thursday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("thursday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        thursdayTime.add(tif);
                        String c = tif.amPmTime();

                        Thursday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("friday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("friday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        fridayTime.add(tif);
                        String c = tif.amPmTime();

                        Friday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("saturday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("saturday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        saturdayTime.add(tif);
                        String c = tif.amPmTime();

                        Saturday.setData(c);
                    }


                }
                if(dataSnapshot.hasChild("sunday")){
                    DataSnapshot mondaySnap = dataSnapshot.child("sunday");
                    for(DataSnapshot mSnap: mondaySnap.getChildren()){
                        TimeInterval tif = mSnap.getValue(TimeInterval.class);
                        sundayTime.add(tif);
                        String c = tif.amPmTime();

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
             /*
                for(int i = 0; i<expandableListAdapter.getGroupCount(); i++){
                    expandableListView.expandGroup(i);
                }
                */

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
                ArrayList<TimeInterval> selectedDayIntervals = (ArrayList<TimeInterval>) expandableListDetail.get(expandableListTitle.get(groupPosition));
                i.putParcelableArrayListExtra("listOfTimes",selectedDayIntervals);
                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                startActivity(i);
                return false;
            }
        });





        adapter = new DaySelectAdapter(getApplicationContext(),itemList);



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
