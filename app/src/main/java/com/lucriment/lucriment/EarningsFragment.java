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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 8/22/2017.
 */

public class EarningsFragment extends Fragment {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference earningsRef;
    private ArrayList<SessionRequest> mySessions = new ArrayList<>();
    private ListView earningsList;
    private ArrayAdapter<SessionRequest> adapter;
    private ArrayList<SessionRequest> sortedSessions = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earnings_tab, container,false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");
       // earningsList = (ListView) view.findViewById(R.id.earningsList);

       earningsRef  = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId());
        earningsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mySessions.clear();
                for(DataSnapshot sessionSnap:dataSnapshot.getChildren()) {

                    if (sessionSnap.getKey().contains(userInfo.getId())) {
                        for (DataSnapshot individualSnap : sessionSnap.getChildren()) {
                            mySessions.add(individualSnap.getValue(SessionRequest.class));

                        }

                    }
                }
                sortMySessions();
                populateEarningsList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void populateEarningsList(){
        adapter = new EarningsFragment.earningsListAdapter();
        if(getView()!=null) {
            earningsList = (ListView) getView().findViewById(R.id.earningsList);
            earningsList.setAdapter(adapter);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortMySessions(){
        Calendar cal = Calendar.getInstance();
        long currentTimeInMilis = cal.getTimeInMillis();

        for(SessionRequest sr:mySessions){
            if(sr.getTime().getTo()<currentTimeInMilis){
                sortedSessions.add(sr);
            }
        }



    }

    private class earningsListAdapter extends ArrayAdapter<SessionRequest>  {

        public earningsListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, sortedSessions);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.earning_item_layout, parent, false);
            }
            final SessionRequest session = sortedSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView dateText = (TextView) itemView.findViewById(R.id.date);
            TextView timeText = (TextView) itemView.findViewById(R.id.sessionLength);
            TextView amountText = (TextView) itemView.findViewById(R.id.amount);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");
            if(session.getLocation()==null){
                nameText.setText("No Session Requests");
                dateText.setText("");
                timeText.setText("");
                amountText.setText("");
            }else {

                //set inner fields
                nameText.setText(session.getStudentName());
                dateText.setText(sdf.format(session.getTime().getFrom()));
                timeText.setText(session.getTime().returnTimeInHours()*60+" min");
                double earnings = Math.round(((session.getPrice())*.85)*100.0)/100.0;
                amountText.setText("$"+earnings+"");
            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }
}
