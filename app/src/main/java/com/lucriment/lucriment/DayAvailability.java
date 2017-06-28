package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class DayAvailability extends AppCompatActivity {

    private ListView listView;
    private TimeInterval time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_availability);
        //GET INTENTS
        time = getIntent().getParcelableExtra("day");

    }
}
