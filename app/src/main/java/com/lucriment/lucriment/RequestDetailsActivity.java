package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestDetailsActivity extends AppCompatActivity {
    private UserInfo userInfo, requesteeInfo;
    private String userType;
    private TimeInterval ti;
    private String requesteeName, requesteeUid;
    private String locationName;
    private String subject;
    private TextView nameText, titleText, ratingText;
    private ImageView imageView;
    private ListView optionsList;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    private ArrayAdapter<TwoItemField> optionsAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        TwoItemField field1 = new TwoItemField("Subject", "Select");
        TwoItemField field3 = new TwoItemField("Location", "Select");
        TwoItemField field2 = new TwoItemField("Time", "Select");
        itemList.add(field1);
        itemList.add(field2);
        itemList.add(field3);


        // GET INTENTS
        if(getIntent().hasExtra("requestId")){
            requesteeUid = getIntent().getStringExtra("requestId");
        }
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("time")){
            ti = getIntent().getParcelableExtra("time");
        }
        if(getIntent().hasExtra("name")){
            requesteeName = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("location")){
            locationName = getIntent().getStringExtra("location");
        }
        if(getIntent().hasExtra("subject")){
            subject = getIntent().getStringExtra("subject");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(requesteeUid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requesteeInfo = dataSnapshot.getValue(UserInfo.class);
                initializeFields();
                populateOptionsList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //INITIALIZE WIDGETS
        nameText = (TextView) findViewById(R.id.requestName);
        titleText = (TextView) findViewById(R.id.requestTitle);
        ratingText = (TextView) findViewById(R.id.studentScore);
        imageView = (ImageView) findViewById(R.id.requestPic);
        optionsList = (ListView) findViewById(R.id.requestOptions);

        //SET TEXT
        nameText.setText(requesteeName);


    }
    private void initializeFields(){
        titleText.setText(requesteeInfo.getHeadline());
        ratingText.setText("4.5");
        Glide.with(getApplicationContext())
                .load(requesteeInfo.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private void populateOptionsList(){
        optionsAdapter = new RequestDetailsActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.requestOptions);
        list.setAdapter(optionsAdapter);
        list.setOnItemClickListener(null);
        list.setFocusable(false);
        list.setClickable(false);
        list.setFocusableInTouchMode(false);


    }

        //OPTIONS ADAPTER
    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(RequestDetailsActivity.this, R.layout.session_request_field, itemList);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.session_request_field, parent, false);
            }

            TwoItemField currentTwo = itemList.get(position);
            //Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView category = (TextView) itemView.findViewById(R.id.category);
            category.setText(currentTwo.getLabel());


            TextView dataText = (TextView) itemView.findViewById(R.id.input);
            dataText.setText(currentTwo.getData());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
}
