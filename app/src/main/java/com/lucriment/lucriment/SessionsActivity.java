package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.content.Intent;
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

public class SessionsActivity extends FragmentActivity implements DeclineDialogFragment.NoticeDialogListener, AcceptDialogFragment.NoticeDialogListener, View.OnClickListener {

    private ListView requestList;
    private ListView bookedList;
    private FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    private ArrayList<SessionRequest> allSessions = new ArrayList<SessionRequest>();
    private ArrayList<SessionRequest> sessionList = new ArrayList<SessionRequest>();
    private ArrayAdapter<SessionRequest> adapter;
    private ArrayAdapter<SessionRequest> adapter2;
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

    //private ArrayList<SessionsActivity>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
       // firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initialize buttons
        requestList = (ListView) findViewById(R.id.requestList);
        bookedList = (ListView) findViewById(R.id.bookedList);
        backButton = (Button) findViewById(R.id.backButton);
        //SET BUTTON LISTENERS
        backButton.setOnClickListener(this);
        //GET SESSION LIST
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot sSnapShot: dataSnapshot.getChildren()){
                    allSessions.clear();
                    bookedSessions.clear();
                    String thisKey = sSnapShot.getKey();
                  //  GenericTypeIndicator<HashMap<String,SessionRequest>> t = new GenericTypeIndicator<HashMap<String, SessionRequest>>() {};
                 //   seshreq = sSnapShot.getValue(t);
                    if(thisKey.contains(ID)){
                    strings.add(thisKey);
                        for(DataSnapshot innerSnap:sSnapShot.getChildren()){
                            SessionRequest currentIteratedSession = innerSnap.getValue(SessionRequest.class);
                            allSessions.add(currentIteratedSession);
                            if(currentIteratedSession.isConfirmed()){
                                bookedSessions.add(currentIteratedSession);
                            }else {
                                sessionList.add(currentIteratedSession);
                            }
                        }

                       // SessionRequest test1 = cur.getValue(SessionRequest.class);
                       // ArrayList a = new ArrayList();
                      //  set.add(sSnapShot.getValue(SessionRequest.class));
                      //  a = sSnapShot.getValue(ArrayList.class);
                        Toast.makeText(SessionsActivity.this, "Key Success.",
                                Toast.LENGTH_SHORT).show();



                    }

                   // sessionList.add(set)
                   // for(DataSnapshot innerSnap: sSnapShot.getChildren()){
                     //   SessionRequest sessionRequest = innerSnap.getValue(SessionRequest.class);
                     //   sessionList.add(sessionRequest);
                  //  }
                //    SessionRequest sessionRequest = sSnapShot.getValue(SessionRequest.class);
                //    sessionList.add(sessionRequest);
//                 /   Availability ava = avaSnapShot.getValue(Availability.class);
                //    aList.add(ava);
                    populateSelectionList();
                    populateBookedList();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Tutors").child(user.getUid()).child("BookedSessions");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookedSessions.clear();
                for(DataSnapshot bSnapShot: dataSnapshot.getChildren()){
                    SessionRequest sessionRequest = bSnapShot.getValue(SessionRequest.class);
                    bookedSessions.add(sessionRequest);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/

        registerSessionClicks();

    }

    private void populateBookedList(){


        adapter2 = new SessionsActivity.bookedListAdapter();
        ListView list = (ListView) findViewById(R.id.bookedList);


        list.setAdapter(adapter2);

     //   list.addHeaderView(myTextView);


    }

    private void populateSelectionList(){
        adapter = new SessionsActivity.sessionListAdapter();
        ListView list = (ListView) findViewById(R.id.requestList);
        list.setAdapter(adapter);

        ArrayList<String> t23 = strings;
    }

    @Override
    public void onDeclinePositiveClick(DialogFragment dialog) {

        sessionList.remove(indexOfClickedSession);
        databaseReference1.setValue(sessionList);
    }

    @Override
    public void onDeclineNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onAcceptPositiveClick(DialogFragment dialog) {
        DatabaseReference databaseReference2 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(clickedSession.getStudentId()+"_"+clickedSession.getTutorId());
        clickedSession.setConfirmed(true);
       // allSessions.add(clickedSession);
      //  bookedSessions.add(clickedSession);
       // databaseReference2.setValue(bookedSessions);
        sessionList.remove(indexOfClickedSession);
        databaseReference2.setValue(allSessions);

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
              //  i.putExtra("time",selectedSession.getTime());
                i.putExtra("location", selectedSession.getLocation());
                i.putExtra("subject",selectedSession.getSubject());
              //  i.putExtra("selectedTutor", selectedTutor1);

                startActivity(i);

            }
        });

    }

}
