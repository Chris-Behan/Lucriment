package com.lucriment.lucriment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChrisBehan on 6/30/2017.
 */

public class TimeTabAdapter extends ArrayAdapter<TimeInterval> {
    Context context;
    ArrayList<TimeInterval> tList = new ArrayList<>();

    public TimeTabAdapter(Context context, ArrayList<TimeInterval> days){
        super(context,0,days);
        tList = days;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TimeInterval day = tList.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetabitem, parent, false);
        }
        // Lookup view for data population

        TextView Time = (TextView) convertView.findViewById(R.id.time);
        // Populate the data into the template view using the data object

        Time.setText(day.returnFromTime()+" - "+day.returnToTime());
        // Return the completed view to render on screen


        return convertView;


    }
}
