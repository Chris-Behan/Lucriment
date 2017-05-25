package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SessionsActivity extends AppCompatActivity {

    private ListView requestList;
    private FirebaseAuth firebaseAuth;
    private ArrayList<SessionRequest> sessionList = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initialize buttons
        requestList = (ListView) findViewById(R.id.requestList);


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Tutors").child(user.getUid()).child("SessionRequests");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sessionList.clear();
                for(DataSnapshot sSnapShot: dataSnapshot.getChildren()){
                    SessionRequest sessionRequest = sSnapShot.getValue(SessionRequest.class);
                    sessionList.add(sessionRequest);
//                 /   Availability ava = avaSnapShot.getValue(Availability.class);
                //    aList.add(ava);

                }
                populateSelectionList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    private void populateSelectionList(){
        adapter = new SessionsActivity.sessionListAdapter();
        ListView list = (ListView) findViewById(R.id.requestList);
        list.setAdapter(adapter);


    }

    private class sessionListAdapter extends ArrayAdapter<SessionRequest> {

        public sessionListAdapter(){
            super(SessionsActivity.this, R.layout.sessionrequestlayout, sessionList);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.sessionrequestlayout, parent, false);
            }
            SessionRequest session = sessionList.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //set inner fields
            nameText.setText(session.getStudentName());
            subjectText.setText(session.getSubject());
            timeText.setText(session.getTime().getTime());
            locationText.setText(session.getLocation());





            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

}
