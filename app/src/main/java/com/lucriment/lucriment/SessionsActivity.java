package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SessionsActivity extends BaseActivity implements DeclineDialogFragment.NoticeDialogListener, AcceptDialogFragment.NoticeDialogListener, View.OnClickListener
, CancelDialogFragment.NoticeDialogListener{

    private ListView requestList;
    private ListView bookedList;
    private FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    private ArrayList<SessionRequest> currentSessions = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> allSessions = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> sessionList = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> thisSession = new ArrayList<>();
    private ArrayList<SessionRequest> pastSessions = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter;
    private ArrayAdapter<SessionRequest> adapter2;
    private ArrayAdapter<SessionRequest> adapter3;
    private ArrayAdapter<SessionRequest> adapter4;
    private SessionRequest clickedSession;
    private int indexOfClickedSession;
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference().child("sessions");
    private ArrayList<SessionRequest> bookedSessions = new ArrayList<>();
    private Button backButton;
    private String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    HashSet<SessionRequest> set = new HashSet<>();
    private HashMap<String,SessionRequest> seshreq;
    ArrayList<String> strings = new ArrayList<>();
    private String userType;
    private TextView sessionRequestLabel;
    private String sessionID;
    private UserInfo userInfo;

    //private ArrayList<SessionsActivity>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
       // firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //initialize buttons
        requestList = (ListView) findViewById(R.id.requestList);
        bookedList = (ListView) findViewById(R.id.bookedList);
        sessionRequestLabel = (TextView) findViewById(R.id.sessionRequestLabel);
        backButton = (Button) findViewById(R.id.backButton);
        //SET BUTTON LISTENERS
//        backButton.setOnClickListener(this);
        //GET SESSION LIST
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSessions.clear();
                bookedSessions.clear();
                sessionList.clear();
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
                                sessionList.add(currentIteratedSession);
                            }
                        }



                        populateSelectionList();
                        populateBookedList();
                        populateCurrentSession();
                        populatePastSessions();

                    }




                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerSessionClicks();
        if(userType.equals("Student")){
            sessionRequestLabel.setText("Pending Requests:");
        }

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_sessions;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.sessions;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
       // return userInfo;
        return null;
    }


    private void populatePastSessions(){
        adapter4 = new SessionsActivity.pastListAdapter();
        ListView pastList = (ListView) findViewById(R.id.pastList);
        pastList.setAdapter(adapter4);
    }

    private void populateCurrentSession(){
        adapter3 = new SessionsActivity.currentSessionAdapter();
        ListView curList = (ListView) findViewById(R.id.currentList);
        curList.setAdapter(adapter3);

    }

    private void populateBookedList(){


        adapter2 = new SessionsActivity.bookedListAdapter();
        ListView list = (ListView) findViewById(R.id.bookedList);


        list.setAdapter(adapter2);

     //   list.addHeaderView(myTextView);


    }

    private void populateSelectionList(){
        if(userType.equals("Tutor")) {
            adapter = new SessionsActivity.sessionListAdapter();
            ListView list = (ListView) findViewById(R.id.requestList);
            list.setAdapter(adapter);
        }else{
            adapter = new SessionsActivity.studentReqAdapter();
            ListView list = (ListView) findViewById(R.id.requestList);
            list.setAdapter(adapter);
        }

        ArrayList<String> t23 = strings;
    }

    @Override
    public void onDeclinePositiveClick(DialogFragment dialog) {

        sessionList.remove(indexOfClickedSession);

        currentSessions.clear();
        DatabaseReference databaseReference2 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(clickedSession.getStudentId()+"_"+clickedSession.getTutorId());
        clickedSession.setConfirmed(true);

        // allSessions.add(clickedSession);
        //  bookedSessions.add(clickedSession);
        // databaseReference2.setValue(bookedSessions);
        for(SessionRequest sq : allSessions){
            if(sq.getStudentId().equals(clickedSession.getStudentId())){
                currentSessions.add(sq);
            }
        }
        currentSessions.remove(clickedSession);
        databaseReference2.setValue(currentSessions);
        adapter2.notifyDataSetChanged();



    }

    @Override
    public void onDeclineNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onAcceptPositiveClick(DialogFragment dialog) {
        currentSessions.clear();
        DatabaseReference databaseReference2 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(clickedSession.getStudentId()+"_"+clickedSession.getTutorId());
        clickedSession.setConfirmed(true);

       // allSessions.add(clickedSession);
      //  bookedSessions.add(clickedSession);
       // databaseReference2.setValue(bookedSessions);
        for(SessionRequest sq : allSessions){
            if(sq.getStudentId().equals(clickedSession.getStudentId())){
                currentSessions.add(sq);
            }
        }
        sessionList.remove(indexOfClickedSession);
        databaseReference2.setValue(currentSessions);
        adapter2.notifyDataSetChanged();

    }

    @Override
    public void onAcceptNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onClick(View v) {

        if(v == backButton){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onCancelPositiveClick(DialogFragment dialog) {
        sessionList.remove(indexOfClickedSession);

        currentSessions.clear();
        DatabaseReference databaseReference2 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(clickedSession.getStudentId()+"_"+clickedSession.getTutorId());
        clickedSession.setConfirmed(true);

        // allSessions.add(clickedSession);
        //  bookedSessions.add(clickedSession);
        // databaseReference2.setValue(bookedSessions);
        for(SessionRequest sq : allSessions){
            if(sq.getStudentId().equals(clickedSession.getStudentId())){
                currentSessions.add(sq);
            }
        }
        currentSessions.remove(clickedSession);
        databaseReference2.setValue(currentSessions);
        adapter2.notifyDataSetChanged();
    }

    @Override
    public void onCancelNegativeClick(DialogFragment dialog) {

    }

    private class currentSessionAdapter extends ArrayAdapter<SessionRequest>  {

        public currentSessionAdapter(){
            super(SessionsActivity.this, R.layout.bookedsessionlayout, thisSession);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = thisSession.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);
            TextView paymentText = (TextView) itemView.findViewById(R.id.paymentText);


            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());
            paymentText.setText("$"+session.getPrice());


            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }

    private class bookedListAdapter extends ArrayAdapter<SessionRequest>  {

        public bookedListAdapter(){
            super(SessionsActivity.this, R.layout.bookedsessionlayout, bookedSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = bookedSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);
            TextView paymentText = (TextView) itemView.findViewById(R.id.paymentText);


            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());
            paymentText.setText("$"+session.getPrice());


            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }

    private class pastListAdapter extends ArrayAdapter<SessionRequest>  {

        public pastListAdapter(){
            super(SessionsActivity.this, R.layout.bookedsessionlayout, pastSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = pastSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);
            TextView paymentText = (TextView) itemView.findViewById(R.id.paymentText);


            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());
            paymentText.setText("$"+session.getPrice());
            clickedSession = session;
            sessionID = clickedSession.getStudentId()+"_"+clickedSession.getTutorId();

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }



    private class sessionListAdapter extends ArrayAdapter<SessionRequest>  {

        public sessionListAdapter(){
            super(SessionsActivity.this, R.layout.sessionrequestlayout, sessionList);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.sessionrequestlayout, parent, false);
            }
            final SessionRequest session = sessionList.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);
            Button acceptButton = (Button) itemView.findViewById(R.id.acceptButton);
            Button declineButton = (Button) itemView.findViewById(R.id.declineButton);
            TextView paymentText = (TextView) itemView.findViewById(R.id.payment);

            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());
            paymentText.setText("$"+String.valueOf(session.getPrice()));

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
            });



            return itemView;
            // return super.getView(position, convertView, parent);
        }

    }

    private class studentReqAdapter extends ArrayAdapter<SessionRequest>  {

        public studentReqAdapter(){
            super(SessionsActivity.this, R.layout.studentreqlayout, sessionList);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.studentreqlayout, parent, false);
            }
            final SessionRequest session = sessionList.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);
            Button acceptButton = (Button) itemView.findViewById(R.id.acceptButton);
            Button declineButton = (Button) itemView.findViewById(R.id.declineButton);
            TextView paymentText = (TextView) itemView.findViewById(R.id.payment);

            //set inner fields
            nameText.setText(session.getTutorName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().returnFormattedDate());
            locationText.setText(session.getLocation());
            paymentText.setText("$"+String.valueOf(session.getPrice()));



            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedSession = session;
                    indexOfClickedSession = position;
                    DeclineDialogFragment declineDialog = new DeclineDialogFragment();
                    declineDialog.show(getFragmentManager(), "decline");
                }
            });



            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }

    private void registerSessionClicks() {
        ListView list = (ListView) findViewById(R.id.bookedList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionRequest selectedSession = bookedSessions.get(position);
               // TutorInfo selectedTutor1 = tutors.get(position);
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(SessionsActivity.this, SessionDetails.class);
                i.putExtra("name",selectedSession.getStudentName());
                i.putExtra("time",selectedSession.getTime());
                i.putExtra("location", selectedSession.getLocation());
                i.putExtra("subject",selectedSession.getSubject());
                i.putExtra("userType",userType);
                i.putExtra("userInfo",userInfo);
              //  i.putExtra("selectedTutor", selectedTutor1);

                startActivity(i);

            }
        });

        ListView currList = (ListView) findViewById(R.id.currentList);
        currList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionRequest currSession = thisSession.get(position);
                Intent y = new Intent(SessionsActivity.this, CurrentSession.class);
                y.putExtra("name",currSession.getStudentName());
                y.putExtra("time",currSession.getTime());
                y.putExtra("location", currSession.getLocation());
                y.putExtra("subject",currSession.getSubject());
                y.putExtra("userType",userType);
                y.putExtra("userInfo",userInfo);

                startActivity(y);

            }
        });

        ListView pastList = (ListView) findViewById(R.id.pastList);
        pastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionRequest pastSession = pastSessions.get(position);

                Intent y = new Intent(SessionsActivity.this, PastSession.class);
                if(userType.equals("Tutor")) {
                    y.putExtra("name", pastSession.getStudentName());
                }else{
                    y.putExtra("name", pastSession.getTutorName());
                }
                y.putExtra("userType",userType);
                y.putExtra("userInfo",userInfo);
                y.putExtra("sessionID",sessionID);
                y.putExtra("time",pastSession.getTime());
                y.putExtra("location", pastSession.getLocation());
                y.putExtra("subject",pastSession.getSubject());
                y.putExtra("studentReview",pastSession.getStudentReview());
                y.putExtra("tutorReview", pastSession.getTutorReview());
                startActivity(y);

            }
        });

    }

}
