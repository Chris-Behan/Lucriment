package com.lucriment.lucriment;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimePickerActivity extends AppCompatActivity  {
    private CalendarView cv;
    private ArrayList<Availability> avaList = new ArrayList<>();
    private ArrayList<Availability> todaysAvailability = new ArrayList<>();
    private TutorInfo tutor;
    private GridView gridView;
    private final ArrayList<String> items = new ArrayList<>();
    private  final gridAdapter myGridAdapter = new gridAdapter(items);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        cv = (CalendarView) findViewById(R.id.calendarView2);
        tutor = getIntent().getParcelableExtra("tutor");
        gridView = (GridView) findViewById(R.id.timeGrid);


        gridView.setAdapter(myGridAdapter);
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");
        items.add("12:30 - 1:30");

        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("Tutors").child(tutor.getID()).child("Availability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    avaList.add(ava);
                    items.add(ava.getFromTime());

                }
                    myGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //Toast.makeText(getApplicationContext(), ""+dayOfMonth, 0).show();
                getSelectedDayAva(year, dayOfMonth, month);

            }
        });



    }


    private void getSelectedDayAva(int year, int day, int month){
        items.clear();
        for(Availability ava: avaList ){
           // int montha = month +1;
            if(ava.getDay()== day && ava.getMonth() == month+1 && ava.getYear() == year){
                todaysAvailability.add(ava);
                items.add(ava.getToTime());
                Toast.makeText(getApplicationContext(), "exists", 0).show();
            }
        }
        myGridAdapter.notifyDataSetChanged();
    }

    private class gridAdapter extends BaseAdapter{
        ArrayList<String> items;

        private gridAdapter(final ArrayList<String> items){
        this.items = items;
        }
        private Context mContext;

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView text = (TextView) view.findViewById(android.R.id.text1);

            text.setText(items.get(position));
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setGravity(view.TEXT_ALIGNMENT_CENTER);
            text.setTextSize(20);
            return view;
        }
    }
}
