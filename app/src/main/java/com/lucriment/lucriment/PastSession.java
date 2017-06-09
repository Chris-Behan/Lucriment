package com.lucriment.lucriment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class PastSession extends AppCompatActivity implements View.OnClickListener {

    private TimeInterval ti;
    private TextView subjectWithField, sessionLengthField, dateField, locationField;
    private String nameString;
    private String className;
    private String locationName;
    private String subjectWith, subject;
    private MapView map;
    private Review review;
    private Button reviewButton;
    private RatingBar ratingBar;
    private EditText reviewField;
    private GoogleMap gMap;
    private boolean leavingReview = false;
    private String SessionID;
    private DatabaseReference databaseReference;
    ArrayList<SessionRequest> allSessions = new ArrayList<>();
    private SessionRequest thisSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_session);

        //INITIALIZE WIDGETS
        subjectWithField = (TextView) findViewById(R.id.subjectWithField);
        sessionLengthField = (TextView) findViewById(R.id.sessionLengthField);
        dateField = (TextView) findViewById(R.id.dateField);
        locationField = (TextView) findViewById(R.id.locationField);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        reviewField = (EditText) findViewById(R.id.reviewField);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        //GET INTENTS
        if(getIntent().hasExtra("sessionID")){
            SessionID = getIntent().getStringExtra("sessionID");
        }
        if(getIntent().hasExtra("review")){
            review = getIntent().getParcelableExtra("review");
        }
        if(getIntent().hasExtra("time")){
            ti = getIntent().getParcelableExtra("time");
        }
        if(getIntent().hasExtra("name")){
            nameString = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("location")){
            locationName = getIntent().getStringExtra("location");
        }
        if(getIntent().hasExtra("subject")){
            subject = getIntent().getStringExtra("subject");
        }
        subjectWith = subject + " with " + nameString;

        subjectWithField.setText(subjectWith);
        dateField.setText(ti.returnFormattedDate());
        locationField.setText(locationName);
        sessionLengthField.setText(String.valueOf(ti.returnTimeInHours())+"hrs");
        if(review == null){
            reviewButton.setVisibility(View.VISIBLE);
        }
        reviewButton.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("sessions").child(SessionID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sessions:dataSnapshot.getChildren()){
                    allSessions.add(sessions.getValue(SessionRequest.class));

                }
                processSessions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void processSessions(){
        for(SessionRequest s:allSessions){

            if(s.getTime().getFrom()==(ti.getFrom())){
               thisSession = s;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if(v==reviewButton){
            if(leavingReview){
                leavingReview = false;
            }else{
                leavingReview = true;
            }
            if(leavingReview){
                ratingBar.setVisibility(View.VISIBLE);
                reviewField.setVisibility(View.VISIBLE);
            }
            else{
                android.icu.util.Calendar cc = android.icu.util.Calendar.getInstance();
                Review review = new Review(thisSession.getStudentId(),Double.valueOf(ratingBar.getRating()),reviewField.getText().toString(),cc.getTimeInMillis());


                databaseReference.setValue(allSessions);
            }

        }
    }
}
