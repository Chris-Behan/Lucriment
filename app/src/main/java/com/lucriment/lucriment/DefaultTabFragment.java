package com.lucriment.lucriment;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private CustomExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private UserInfo userInfo;
   // private HashMap<String, ArrayList<TwoItemField>> defaultAvailability;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.defaulttab, container,false);
        Bundle args = getArguments();
       // int index = args.getInt("index", 0);
        userInfo = args.getParcelable("userInfo");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tutors").child(userInfo.getId()).child("defaultAvailability").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(dataSnapshot.hasChild("monday")){
                  DataSnapshot mondaySnap = dataSnapshot.child("monday");
                 ArrayList<TwoItemField> tr = (ArrayList<TwoItemField>) mondaySnap.getValue();
                  String a = tr.toString();
              }

//                ArrayList<TwoItemField> test =  defaultAvailability.get("monday");
             //   for(Map.Entry<String,ArrayList<TwoItemField>>entry: test.entrySet()){
             //       String key= entry.getKey();
                    //    ArrayList<String> value = entry.getValue();
             //   }
              //  HashMap<String,String> tr = (HashMap<String,String>) test;
               // ArrayList<String> y =tr.get("monday");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        TwoItemField Monday = new TwoItemField("Monday", "Select");
        TwoItemField Tuesday = new TwoItemField("Tuesday", "Select");
        TwoItemField Wednesday = new TwoItemField("Wednesday", "Select");
        TwoItemField Thursday = new TwoItemField("Thursday", "Select");
        TwoItemField Friday = new TwoItemField("Friday", "Select");
        TwoItemField Saturday = new TwoItemField("Saturday", "Select");
        TwoItemField Sunday = new TwoItemField("Sunday", "Select");
        itemList.add(Monday);

        ArrayAdapter<TwoItemField> adapter = new DaySelectAdapter(getApplicationContext(),itemList);
        ListView dayList = (ListView) view.findViewById(R.id.dayList);
        dayList.setAdapter(adapter);

       return view;
    }
    private void processDefaultAvailability(){
        //if(defaultAvailability.containsKey("monday")){

       // }
    }







}
