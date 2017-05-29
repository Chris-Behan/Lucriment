package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SessionDetails extends AppCompatActivity {

    private Availability ava;
    private TextView classLabel, nameLabel, name, timeInterval, location;
    private String nameString;
    private String className;
    private String locationName;
    private String subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        classLabel = (TextView) findViewById(R.id.classtitle);
        timeInterval = (TextView) findViewById(R.id.time);
        if(getIntent().hasExtra("time")){
            ava = getIntent().getParcelableExtra("time");
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
        classLabel.setText(subject);
        name.setText(nameString);
       timeInterval.setText(ava.getTime());
        location.setText(locationName);


    }
}
