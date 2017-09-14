package com.lucriment.lucriment;

import android.content.Intent;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 8/21/2017.
 */

public class PastSessionFragment extends Fragment {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference().child("sessions");

    private ArrayList<SessionRequest> pastSessions = new ArrayList<>();
    private ArrayList<SessionRequest> cancelledSessions = new ArrayList<>();
    private ArrayList<SessionRequest> declinedSessions = new ArrayList<>();

    private String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> strings = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter, adapter2, declineAdapter, cancelledAdapter;
    private ListView requestListView, bookedListView, declinedList, cancelledList;
    private ArrayList<String> pastSessionKeys = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_session_tab, container,false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");


        //GET SESSION LIST
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pastSessions.clear();

                Calendar curCal = Calendar.getInstance();
                long currentTime = curCal.getTimeInMillis();
                for(DataSnapshot sSnapShot: dataSnapshot.getChildren()){
                    String thisKey = sSnapShot.getKey();
                    if(thisKey.contains(ID)){
                        strings.add(thisKey);
                        for(DataSnapshot innerSnap:sSnapShot.getChildren()) {
                            SessionRequest currentIteratedSession = innerSnap.getValue(SessionRequest.class);
                            if(currentIteratedSession.getTutorId().equals(userInfo.getId())&&userType.equals("tutor")
                                    || userType.equals("student")&&currentIteratedSession.getStudentId().equals(userInfo.getId())){
                                if(currentIteratedSession.isSessionCancelled()){
                                    cancelledSessions.add(currentIteratedSession);
                                }
                                if(currentIteratedSession.isSessionDeclined()){
                                    declinedSessions.add(currentIteratedSession);
                                }

                            if (currentIteratedSession.isConfirmed()) {
                                if (currentTime < currentIteratedSession.getTime().getFrom()) {

                                }

                                if (currentTime > currentIteratedSession.getTime().getFrom() && currentTime < currentIteratedSession.getTime().getTo()) {

                                } else if (currentTime > currentIteratedSession.getTime().getTo()) {
                                    pastSessions.add(currentIteratedSession);
                                    pastSessionKeys.add(innerSnap.getKey());
                                }

                            } else {

                            }
                        }
                        }


                        populatPastSessions();
                        populateDeclinedSessions();
                        populateCancelledSessions();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }





    private void populatPastSessions(){


        adapter2 = new PastSessionFragment.bookedListAdapter();
        if(getView()!=null) {
            bookedListView = (ListView) getView().findViewById(R.id.pastSessionList);
            bookedListView.setAdapter(adapter2);
            TextView pastLabel = (TextView) getView().findViewById(R.id.pastSessionsLabel);
            final ViewGroup.LayoutParams test7 = bookedListView.getLayoutParams();
            test7.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            pastLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(test7.height == 0){
                        test7.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
                        bookedListView.setLayoutParams(test7);
                    }else{
                        test7.height =  0;
                        bookedListView.setLayoutParams(test7);
                    }
                }
            });


            bookedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SessionRequest selectedSession = pastSessions.get(position);
                    // TutorInfo selectedTutor1 = tutors.get(position);
                    // selectedTutor1 = TutorListActivity.this.selectedTutor;
                    Intent i = new Intent(getApplicationContext(), PastDetailsActivity.class);
                    if (selectedSession.getLocation() == null) {
                    } else {
                        String key = pastSessionKeys.get(position);
                        if (userType.equals("tutor")) {
                            i.putExtra("name", selectedSession.getStudentName());
                            i.putExtra("requestId", selectedSession.getStudentId());
                        } else {
                            i.putExtra("name", selectedSession.getTutorName());
                            i.putExtra("requestId", selectedSession.getTutorId());
                        }
                        i.putExtra("time", selectedSession.getTime());
                        i.putExtra("location", selectedSession.getLocation());
                        i.putExtra("subject", selectedSession.getSubject());
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        i.putExtra("requestKey", key);
                        //  i.putExtra("selectedTutor", selectedTutor1);
                        startActivity(i);
                    }
                }
            });
        }
    }

    private void populateDeclinedSessions(){
        declineAdapter = new PastSessionFragment.declinedListAdapter();
        if(getView()!=null) {
            declinedList = (ListView) getView().findViewById(R.id.declineSessionsList);
            declinedList.setAdapter(declineAdapter);
            TextView pastLabel = (TextView) getView().findViewById(R.id.declineSessionsLabel);
            final ViewGroup.LayoutParams test8 = declinedList.getLayoutParams();
            test8.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            pastLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(test8.height == 0){
                        test8.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
                        declinedList.setLayoutParams(test8);
                    }else{
                        test8.height =  0;
                        declinedList.setLayoutParams(test8);
                    }
                }
            });



        }
    }
    private void populateCancelledSessions(){
        cancelledAdapter = new PastSessionFragment.cancelledListAdapter();
        if(getView()!=null){
            cancelledList = (ListView) getView().findViewById(R.id.cancelledSessionsList);
            cancelledList.setAdapter(cancelledAdapter);
            TextView pastLabel = (TextView) getView().findViewById(R.id.cancelledSessionsLabel);
            final ViewGroup.LayoutParams test8 = cancelledList.getLayoutParams();
            test8.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            pastLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(test8.height == 0){
                        test8.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
                        cancelledList.setLayoutParams(test8);
                    }else{
                        test8.height =  0;
                        cancelledList.setLayoutParams(test8);
                    }
                }
            });

        }
    }

    private class bookedListAdapter extends ArrayAdapter<SessionRequest>  {

        public bookedListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, pastSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = pastSessions.get(position);

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
            }else{
                nameText.setText(session.getTutorName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());

            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }

    private class declinedListAdapter extends ArrayAdapter<SessionRequest>  {

        public declinedListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, declinedSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = declinedSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");
            if(session.getLocation()==null){
                nameText.setText("No Declined Requests");
                subjectText.setText("");
                timeText.setText("");
                locationText.setText("");
            }else if(userType.equals("tutor")) {

                //set inner fields
                nameText.setText(session.getStudentName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            }else{
                nameText.setText(session.getTutorName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());

            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }

    private class cancelledListAdapter extends ArrayAdapter<SessionRequest>  {

        public cancelledListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, cancelledSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = cancelledSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");
            if(session.getLocation()==null){
                nameText.setText("No Cancelled Sessions");
                subjectText.setText("");
                timeText.setText("");
                locationText.setText("");
            }else if(userType.equals("tutor")) {

                //set inner fields
                nameText.setText(session.getStudentName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            }else{
                nameText.setText(session.getTutorName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());

            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }


}
