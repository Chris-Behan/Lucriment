package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SessionsActivity extends AppCompatActivity {

    private ListView requestList;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Availability> sessionList;


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
               // aList.clear();
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                //    aList.add(ava);

                }
              //  populateScheduleList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
