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
import android.widget.AdapterView;
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
    private ArrayAdapter<SessionRequest> adapter, adapter2;
    private ListView requestListView, bookedListView;
    private ArrayList<String> requestSessionKeys = new ArrayList<>();
    private ArrayList<String> bookedSessionKeys = new ArrayList<>();




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
                        for(DataSnapshot innerSnap:sSnapShot.getChildren()) {
                            SessionRequest currentIteratedSession = innerSnap.getValue(SessionRequest.class);
                            allSessions.add(currentIteratedSession);
                            if(currentIteratedSession.getTutorId().equals(userInfo.getId())&&userType.equals("tutor")
                                    || userType.equals("student")&&currentIteratedSession.getStudentId().equals(userInfo.getId())){
                            if (currentIteratedSession.isConfirmed()) {
                                if (currentTime < currentIteratedSession.getTime().getFrom()) {

                                    bookedSessions.add(currentIteratedSession);
                                    bookedSessionKeys.add(innerSnap.getKey());
                                }

                                if (currentTime > currentIteratedSession.getTime().getFrom() && currentTime < currentIteratedSession.getTime().getTo()) {
                                    thisSession.add(currentIteratedSession);
                                } else if (currentTime > currentIteratedSession.getTime().getTo()) {
                                    pastSessions.add(currentIteratedSession);
                                }

                            } else {
                                requestList.add(currentIteratedSession);
                                requestSessionKeys.add(innerSnap.getKey());
                            }
                        }
                        }


                        
                    }
                    
                }
                populateRequestList();
                populateBookedList();
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void populateRequestList(){
        if(requestList.isEmpty()){

            SessionRequest sq = new SessionRequest();
            requestList.add(sq);
        }

        if(userType.equals("tutor")) {
            adapter = new UpcomingSessionFragment.sessionListAdapter();
            if(getView()!=null) {
                requestListView = (ListView) getView().findViewById(R.id.requestList3);
                requestListView.setAdapter(adapter);
            }
        }else{
            adapter = new UpcomingSessionFragment.sessionListAdapter();
            if(getView()!=null) {
                requestListView = (ListView) getView().findViewById(R.id.requestList3);
                requestListView.setAdapter(adapter);
            }
        }
        if(requestListView!=null) {
            requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SessionRequest selectedSession = requestList.get(position);
                    // TutorInfo selectedTutor1 = tutors.get(position);
                    // selectedTutor1 = TutorListActivity.this.selectedTutor;
                    Intent i = new Intent(getApplicationContext(), RequestDetailsActivity.class);


                    if (selectedSession.getLocation() == null) {

                    } else {
                        String key = requestSessionKeys.get(position);
                        if (userType.equals("tutor")) {
                            i.putExtra("name", selectedSession.getStudentName());
                        } else {
                            i.putExtra("name", selectedSession.getTutorName());
                        }
                        i.putExtra("time", selectedSession.getTime());
                        i.putExtra("location", selectedSession.getLocation());
                        i.putExtra("subject", selectedSession.getSubject());
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        if(userType.equals("tutor")) {
                            i.putExtra("requestId", selectedSession.getStudentId());
                        }else{
                            i.putExtra("requestId", selectedSession.getTutorId());
                        }
                        i.putExtra("requestKey", key);

                        //  i.putExtra("selectedTutor", selectedTutor1);

                        startActivity(i);

                    }
                }
            });
        }

        ArrayList<String> t23 = strings;
    }

    private void populateBookedList(){


        adapter2 = new UpcomingSessionFragment.bookedListAdapter();
        if(getView()!=null) {
            bookedListView = (ListView) getView().findViewById(R.id.confirmedList);
            bookedListView.setAdapter(adapter2);
        }
        bookedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionRequest selectedSession = bookedSessions.get(position);
                // TutorInfo selectedTutor1 = tutors.get(position);
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(getApplicationContext(), BookedDetailsActivity.class);


                if(selectedSession.getLocation()==null){

                }else {
                    String key = bookedSessionKeys.get(position);
                    if (userType.equals("tutor")) {
                        i.putExtra("name", selectedSession.getStudentName());
                    } else {
                        i.putExtra("name", selectedSession.getTutorName());
                    }
                    i.putExtra("time", selectedSession.getTime());
                    i.putExtra("location", selectedSession.getLocation());
                    i.putExtra("subject", selectedSession.getSubject());
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo", userInfo);
                    if(userType.equals("tutor")) {
                        i.putExtra("requestId", selectedSession.getStudentId());
                    }else{
                        i.putExtra("requestId", selectedSession.getTutorId());
                    }
                    i.putExtra("requestKey", key);

                    //  i.putExtra("selectedTutor", selectedTutor1);

                    startActivity(i);

                }

            }
        });



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
            if(session.getLocation()==null){
                nameText.setText("No Session Requests");
                subjectText.setText("");
                timeText.setText("");
                locationText.setText("");
            }else if(userType.equals("tutor")) {

                //set inner fields
                nameText.setText(session.getStudentName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            } else {
                nameText.setText(session.getTutorName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            }

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
            Button declineButton = (Button) itemView.findViewById(R.id.declineButton);


            //set inner fields
            nameText.setText(session.getTutorName());
            subjectText.setText(session.getSubject());
            if(session.getTime()!=null) {
                timeText.setText(session.getTime().returnFormattedDate());
            }
            locationText.setText(session.getLocation());








            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }

    private class bookedListAdapter extends ArrayAdapter<SessionRequest>  {

        public bookedListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, bookedSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = bookedSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");
            if(session.getLocation()==null){
                nameText.setText("No Session Requests");
                subjectText.setText("");
                timeText.setText("");
                locationText.setText("");
            }else if(userType.equals("tutor")) {

                //set inner fields
                nameText.setText(session.getStudentName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            } else {
                nameText.setText(session.getTutorName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            }


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

}
