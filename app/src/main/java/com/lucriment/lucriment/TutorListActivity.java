package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TutorListActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    private List<TutorInfo> tutors = new ArrayList<TutorInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        getTutors();
        populateTutorList();

    }

    private void populateTutorList(){

    }

    private void getTutors(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tutors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){
                    TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                    tutors.add(tutor);
                }
               // tutors =  collectNames((Map<String,Object>) dataSnapshot.getValue());
              //  populateTutorList(tNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
