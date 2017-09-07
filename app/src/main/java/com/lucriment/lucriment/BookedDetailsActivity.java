package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookedDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, CancelSessionDialogFragment.NoticeDialogListener{
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
    private Button acceptButton, declineButton;
    private String key;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Session Request");
        TwoItemField field1 = new TwoItemField("Subject", "Select");
        TwoItemField field3 = new TwoItemField("Location", "Select");
        TwoItemField field2 = new TwoItemField("Time", "Select");



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
        if(getIntent().hasExtra("requestKey")){
            key = getIntent().getStringExtra("requestKey");
        }
        field1.setData(subject);
        field2.setData(ti.returnSessionTime());
        field3.setData(locationName);

        itemList.add(field1);
        itemList.add(field2);
        itemList.add(field3);

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
        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton = (Button) findViewById(R.id.declineButton);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

        if(userType.equals("student")){
            acceptButton.setText("Contact Tutor");
        }

        //SET TEXT
        nameText.setText(requesteeName);

        //SET UP MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        optionsAdapter = new BookedDetailsActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.requestOptions);
        list.setAdapter(optionsAdapter);
        list.setOnItemClickListener(null);
        list.setFocusable(false);
        list.setClickable(false);
        list.setFocusableInTouchMode(false);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Intent i = new Intent(BookedDetailsActivity.this, TutorSessionsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
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

    @Override
    public void onClick(View v) {
        if(v == acceptButton){
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            UserInfo selectedUser;

            selectedUser = requesteeInfo;
            // if(myID.equalsIgnoreCase(((TextView)view).getText().toString())){
            //       myID = tutorId;
            //  }
            intent.putExtra("user", selectedUser);
            intent.putExtra("userType", userType);
            intent.putExtra("userInfo",userInfo);
            // intent.putExtra("userName", userName);
            startActivity(intent);


        }
        if (v == declineButton){
            CancelSessionDialogFragment declineDialogFragment = new CancelSessionDialogFragment();
            declineDialogFragment.dialogMessage("By cancelling this session it will be removed from your booked sessions," +
                    " the tutor will be notified, and you will not be charged. Are you sure you wish to cancel the session?");
            declineDialogFragment.show(getFragmentManager(), "Decline");


        }

    }



    @Override
    public void onDeclinePositiveClick(DialogFragment dialog) {
        if(userType.equals("tutor")) {
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(requesteeUid + "_" + userInfo.getId()).child(key).child("sessionCancelled");
            databaseReference2.setValue(true);
            Toast.makeText(BookedDetailsActivity.this, "Session Canceled",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(BookedDetailsActivity.this, TutorSessionsActivity.class);

            i.putExtra("userType", userType);
            i.putExtra("userInfo", userInfo);
            startActivity(i);
        }else{
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()+ "_"+requesteeUid).child(key).child("sessionCancelled");
            databaseReference2.setValue(true);
            Toast.makeText(BookedDetailsActivity.this, "Session Canceled",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(BookedDetailsActivity.this, TutorSessionsActivity.class);

            i.putExtra("userType", userType);
            i.putExtra("userInfo", userInfo);
            startActivity(i);

        }
    }

    @Override
    public void onDeclineNegativeClick(DialogFragment dialog) {

    }

    //OPTIONS ADAPTER
    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(BookedDetailsActivity.this, R.layout.session_request_field, itemList);
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
