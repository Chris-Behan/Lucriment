package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrowseActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ArrayList<String> tNames;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        firebaseAuth = FirebaseAuth.getInstance();
        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(this);



        // databaseReference = FirebaseDatabase.getInstance().getReference();
        getTutors();
        // populateTutorList();
    }

    private void populateTutorList(ArrayList<String> names){
        ArrayList<String> nameList = names;
        String[] namesArray = nameList.toArray(new String[nameList.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.tutor_list, namesArray);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

    }

    private void getTutors(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tutors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tNames =  collectNames((Map<String,Object>) dataSnapshot.getValue());
                populateTutorList(tNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> collectNames(Map<String, Object> tutors){
        ArrayList<String> tutorNames = new ArrayList<String>();
        for(Map.Entry<String, Object> entry: tutors.entrySet()){
            Map Tutor = (Map) entry.getValue();
            tutorNames.add((String)Tutor.get("name"));
        }
        return tutorNames;
    }

    @Override
    public void onClick(View v) {

        if(v == backButton){
            finish();
            startActivity(new Intent(BrowseActivity.this, ProfileActivity.class));
        }
    }
}