package com.lucriment.lucriment;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 8/19/2017.
 */

public class UpcomingSessionFragment extends Fragment {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference().child("sessions");
    private ArrayList<SessionRequest> currentSessions = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> allSessions = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> requestList = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> thisSession = new ArrayList<>();
    private ArrayList<SessionRequest> pastSessions = new ArrayList<>();
    private ArrayList<SessionRequest> bookedSessions = new ArrayList<>();
    private String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> strings = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upcoming_session_tab, container,false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");

        //GET SESSION LIST
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSessions.clear();
                bookedSessions.clear();
                requestList.clear();
                pastSessions.clear();

                Calendar curCal = Calendar.getInstance();
                long currentTime = curCal.getTimeInMillis();
                for(DataSnapshot sSnapShot: dataSnapshot.getChildren()){
                    String thisKey = sSnapShot.getKey();
                    if(thisKey.contains(ID)){
                        strings.add(thisKey);
                        for(DataSnapshot innerSnap:sSnapShot.getChildren()){
                            SessionRequest currentIteratedSession = innerSnap.getValue(SessionRequest.class);
                            allSessions.add(currentIteratedSession);
                            if(currentIteratedSession.isConfirmed()){
                                if(currentTime< currentIteratedSession.getTime().getFrom()){
                                    bookedSessions.add(currentIteratedSession);
                                }

                                if(currentTime > currentIteratedSession.getTime().getFrom()  && currentTime < currentIteratedSession.getTime().getTo()){
                                    thisSession.add(currentIteratedSession);
                                }else if(currentTime > currentIteratedSession.getTime().getTo()){
                                    pastSessions.add(currentIteratedSession);
                                }

                            }else {
                                requestList.add(currentIteratedSession);
                            }
                        }
                        populateRequestList();
                        
                    }
                    
                }
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void populateRequestList(){
        if(userType.equals("tutor")) {
            adapter = new UpcomingSessionFragment.sessionListAdapter();
            ListView list = (ListView) getView().findViewById(R.id.requestList3);
            list.setAdapter(adapter);
        }else{
            adapter = new UpcomingSessionFragment.studentReqAdapter();
            ListView list = (ListView) getView().findViewById(R.id.requestList3);
            list.setAdapter(adapter);
        }

        ArrayList<String> t23 = strings;
    }

    private class sessionListAdapter extends ArrayAdapter<SessionRequest>  {

        public sessionListAdapter(){
            super(getApplicationContext(), R.layout.sessionrequestlayout, requestList);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.sessionrequestlayout, parent, false);
            }
            final SessionRequest session = requestList.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");

            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnSessionTime());
            locationText.setText(session.getLocation());

            /*
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedSession = session;
                    indexOfClickedSession = position;
                    AcceptDialogFragment acceptDialog = new AcceptDialogFragment();
                    acceptDialog.show(getFragmentManager(), "accept");
                }
            });

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedSession = session;
                    indexOfClickedSession = position;
                    DeclineDialogFragment declineDialog = new DeclineDialogFragment();
                    declineDialog.show(getFragmentManager(), "decline");
                }
            }); */



            return itemView;
            // return super.getView(position, convertView, parent);
        }

    }

    private class studentReqAdapter extends ArrayAdapter<SessionRequest>  {

        public studentReqAdapter(){
            super(getApplicationContext(), R.layout.studentreqlayout, requestList);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.studentreqlayout, parent, false);
            }
            final SessionRequest session = requestList.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);


            //set inner fields
            nameText.setText(session.getTutorName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());



            /*
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedSession = session;
                    indexOfClickedSession = position;
                    DeclineDialogFragment declineDialog = new DeclineDialogFragment();
                    declineDialog.show(getFragmentManager(), "decline");
                }
            }); */



            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }

}
