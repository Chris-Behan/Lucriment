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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, DeclineDialogFragment.NoticeDialogListener, AcceptDialogFragment.NoticeDialogListener, CancelSessionDialogFragment.NoticeDialogListener {
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
    private double price;
    private String accountDest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Session Request");
        TwoItemField field1 = new TwoItemField("Subject", "Select");
        TwoItemField field3 = new TwoItemField("Location", "Select");
        TwoItemField field2 = new TwoItemField("Time", "Select");



        // GET INTENTS
        if(getIntent().hasExtra("requestId")){
            requesteeUid = getIntent().getStringExtra("requestId");
        }
        if(getIntent().hasExtra("price")){
            price = getIntent().getDoubleExtra("price",0);
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
        if(userInfo.getUserType().equals("tutor")) {
            DatabaseReference myAcctRef = FirebaseDatabase.getInstance().getReference("tutors").child(userInfo.getId()).child("stripe_connected").child("id");
            myAcctRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    accountDest = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (userType.equals("tutor")) {
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
        } else {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(requesteeUid);
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
        }
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
            acceptButton.setVisibility(View.INVISIBLE);
            declineButton.setText("Cancel Request");
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
                 Rating rating = (requesteeInfo.getRating());
                if (requesteeInfo.getRating() != null) {
                    double ratingAvg = (rating.getTotalScore() / rating.getNumberOfReviews());
                    DecimalFormat df = new DecimalFormat("#.#");
                    ratingAvg = Double.valueOf(df.format(ratingAvg));

                    ratingText.setText(ratingAvg + "");
                } else {
                    ratingText.setText("  0");
                }

        Glide.with(getApplicationContext())
                .load(requesteeInfo.getProfileImage())
                .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Intent i = new Intent(RequestDetailsActivity.this, TutorSessionsActivity.class);
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
            AcceptDialogFragment acceptDialogFragment = new AcceptDialogFragment();
            acceptDialogFragment.show(getFragmentManager(),"Accept");

        }
        if (v == declineButton){
            if(userType.equals("tutor")) {
                DeclineDialogFragment declineDialogFragment = new DeclineDialogFragment();
                declineDialogFragment.show(getFragmentManager(), "Decline");
            }else{
                CancelSessionDialogFragment cancelSessionDialogFragment = new CancelSessionDialogFragment();
                cancelSessionDialogFragment.show(getFragmentManager(), "Cancel");
            }

        }

    }

    @Override
    public void onAcceptPositiveClick(DialogFragment dialog) {
        DatabaseReference databaseReference2 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()).child(requesteeUid+"_"+userInfo.getId()).child(key).child("confirmed");
        databaseReference2.setValue(true);
        DatabaseReference databaseReference3 =  FirebaseDatabase.getInstance().getReference().child("sessions").child(requesteeUid).child(requesteeUid+"_"+userInfo.getId()).child(key).child("confirmed");
        databaseReference3.setValue(true);
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users").child(requesteeUid).child("charges");
        HashMap<String,Object> amountMap = new HashMap<String, Object>();
        amountMap.put("amount",price*100);
        amountMap.put("destination",accountDest);
        dbr.push().setValue(amountMap);
        Toast.makeText(RequestDetailsActivity.this, "Session Booked",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(RequestDetailsActivity.this, TutorSessionsActivity.class);

        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);

        // clickedSession.setConfirmed(true);

    }

    @Override
    public void onAcceptNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDeclinePositiveClick(DialogFragment dialog) {
        if(userType.equals("tutor")) {
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(requesteeUid).child(requesteeUid + "_" + userInfo.getId()).child(key).child("sessionDeclined");
            databaseReference2.setValue(true);
            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()).child(requesteeUid + "_" + userInfo.getId()).child(key).child("sessionDeclined");
            databaseReference3.setValue(true);
            Toast.makeText(RequestDetailsActivity.this, "Session Declined",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RequestDetailsActivity.this, TutorSessionsActivity.class);

            i.putExtra("userType", userType);
            i.putExtra("userInfo", userInfo);
            startActivity(i);
        }else{
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()).child(userInfo.getId()+ "_"+requesteeUid).child(key).child("sessionCancelled");
            databaseReference2.setValue(true);
            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("sessions").child(requesteeUid).child(userInfo.getId()+ "_"+requesteeUid).child(key).child("sessionCancelled");
            databaseReference3.setValue(true);
            Toast.makeText(RequestDetailsActivity.this, "Session Declined",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RequestDetailsActivity.this, TutorSessionsActivity.class);

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
