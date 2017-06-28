package com.lucriment.lucriment;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChrisBehan on 6/28/2017.
 */

public class DaySelectAdapter extends ArrayAdapter<TwoItemField> {
    Context context;
    ArrayList<TwoItemField> aList = new ArrayList<>();

   public DaySelectAdapter(Context context, ArrayList<TwoItemField> days){
       super(context,0,days);
       aList = days;
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TwoItemField day = aList.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_request_field, parent, false);
        }
        // Lookup view for data population
        TextView Day = (TextView) convertView.findViewById(R.id.category);
        TextView Time = (TextView) convertView.findViewById(R.id.input);
        // Populate the data into the template view using the data object
        Day.setText(day.getLabel());
        Time.setText(day.getData());
        // Return the completed view to render on screen
        return convertView;
    }


// @NonNull


}
