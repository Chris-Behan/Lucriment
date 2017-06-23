package com.lucriment.lucriment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CurrentSession extends AppCompatActivity implements OnMapReadyCallback {

    private TimeInterval ti;
    private TextView subjectWithField, sessionLengthField, dateField, locationField;
    private String nameString;
    private String className;
    private String locationName;
    private String subjectWith, subject;
    private MapView map;
    private GoogleMap gMap;
    private String userType;
    private UserInfo userInfo;
    private ImageView tutorView, studentView;
    private String sessionID;
    private ArrayList<SessionRequest> allSessions = new ArrayList<>();
    private SessionRequest currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_session);
        //INITIALIZE WIDGETS
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        subjectWithField = (TextView) findViewById(R.id.subjectWithField);
        sessionLengthField = (TextView) findViewById(R.id.sessionLengthField);
        dateField = (TextView) findViewById(R.id.dateField);
        locationField = (TextView) findViewById(R.id.locationField);
        tutorView = (ImageView) findViewById(R.id.tutorView);
        studentView = (ImageView) findViewById(R.id.studentView);
        //GET INTENTS
        if (getIntent().hasExtra("sessionID")) {
            sessionID = getIntent().getStringExtra("sessionID");
        }
        if (getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if (getIntent().hasExtra("userType")) {
            userType = getIntent().getStringExtra("userType");
        }
        if (getIntent().hasExtra("time")) {
            ti = getIntent().getParcelableExtra("time");
        }
        if (getIntent().hasExtra("name")) {
            nameString = getIntent().getStringExtra("name");
        }
        if (getIntent().hasExtra("location")) {
            locationName = getIntent().getStringExtra("location");
        }
        if (getIntent().hasExtra("subject")) {
            subject = getIntent().getStringExtra("subject");
        }
        subjectWith = subject + " with " + nameString;

        subjectWithField.setText(subjectWith);
        dateField.setText(ti.returnFormattedDate());
        locationField.setText(locationName);
        sessionLengthField.setText(String.valueOf(ti.returnTimeInHours()) + "hrs");

        //SET UP MAP FRAGMENT
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //GET CURRENT SESSION
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("sessions").child(sessionID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSessions.clear();
                for (DataSnapshot sessions : dataSnapshot.getChildren()) {
                    allSessions.add(sessions.getValue(SessionRequest.class));

                }
                for (SessionRequest s : allSessions) {
                    currentSession = s;
                }
                    getPics();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent y = new Intent(CurrentSession.this, SessionsActivity.class);
        y.putExtra("userType", userType);
        y.putExtra("userInfo", userInfo);
        startActivity(y);
        finish();
        return true;
    }

    private void getPics() {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(currentSession.getStudentId());
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("tutors").child(currentSession.getTutorId());

            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange (DataSnapshot dataSnapshot){

            String imagePath = dataSnapshot.child("profileImage").getValue(String.class);
            Glide.with(getApplicationContext())
                    .load(imagePath)
                    .into(studentView);
        }

        @Override
        public void onCancelled (DatabaseError databaseError){

        }
    });
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imagePath = dataSnapshot.child("profileImage").getValue(String.class);
                Glide.with(getApplicationContext())
                        .load(imagePath)
                        .into(tutorView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

}

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = gc.getFromLocationName(locationName,1);
            android.location.Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            LatLng sydney = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(sydney)
                    .title(locationName));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setMaxZoomPreference(23);
            googleMap.setMinZoomPreference(15);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
