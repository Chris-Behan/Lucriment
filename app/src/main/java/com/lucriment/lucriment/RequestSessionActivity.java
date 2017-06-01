package com.lucriment.lucriment;

import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;





import java.util.ArrayList;

public class RequestSessionActivity extends AppCompatActivity implements View.OnClickListener {

    private Availability selectedAvailability;
    private TutorInfo tutor;
    private TextView nameView;
    private TextView rateView;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private String fromTime, toTime, day;
    private TimeInterval requestedTime;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    private Button requestButton;
    private final int requestcode_placepicker = 1;
    private ArrayList<SessionRequest> sessionReqList = new ArrayList<>();
    private String selectedLocation;
    private ArrayAdapter<TwoItemField> adapter;
    private TextView cost;
    private Button backButton;
    private double sessioncost;
    private String selectedTimeInterval;
    TwoItemField field1 = new TwoItemField("Subject", "Select");
    TwoItemField field2 = new TwoItemField("Location", "Select");
    TwoItemField field3 = new TwoItemField("Time", "Select");
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_session);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(getIntent().hasExtra("location"))
            selectedLocation = getIntent().getStringExtra("location");
        if(getIntent().hasExtra("Availability"))
        selectedAvailability = getIntent().getParcelableExtra("Availability");
        tutor = getIntent().getParcelableExtra("tutor");
        nameView = (TextView) findViewById(R.id.textView5);
        rateView = (TextView) findViewById(R.id.textView6);
        imageView = (ImageView) findViewById(R.id.imageView2);
        requestButton = (Button) findViewById(R.id.requestButton);
        cost = (TextView) findViewById(R.id.costView);
        backButton = (Button) findViewById(R.id.backButton);
        if(getIntent().hasExtra("requestedTime")){
            requestedTime = getIntent().getParcelableExtra("requestedTime");

        }

        storageReference = FirebaseStorage.getInstance().getReference();

        nameView.setText(tutor.getName());
        rateView.setText("$"+String.valueOf(tutor.getRate())+"/hr");


        StorageReference pathReference = storageReference.child("ProfilePics").child(tutor.getID());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(RequestSessionActivity.this).load(uri).fit().centerCrop().into(imageView);
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Tutors").child(tutor.getID()).child("SessionRequests");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sessionReqList.clear();
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    SessionRequest ava = avaSnapShot.getValue(SessionRequest.class);
                    sessionReqList.add(ava);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(requestedTime!=null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(requestedTime.getFrom());
            int fromHour = cal.get(Calendar.HOUR_OF_DAY);
            int fromMinute = cal.get(Calendar.MINUTE);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);

            cal.setTimeInMillis(requestedTime.getTo());
            int toHour = cal.get(Calendar.HOUR_OF_DAY);
            int toMinute = cal.get(Calendar.MINUTE);

            selectedTimeInterval = fromHour+":"+fromMinute+" - "+toHour+":"+toMinute;
        }

        populateItemList();
        populateSelectionList();
        registerFieldClicks();

        requestButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }



    private void populateItemList(){

        if(selectedLocation!=null){
            field2.setData(selectedLocation);
        }


        if(requestedTime==null){
            field1.setData(tutor.getClasses());
        }else{
            field1.setData(tutor.getClasses());
           field3.setData(selectedTimeInterval);
            double r = requestedTime.returnTimeInHours();
            sessioncost =  (requestedTime.returnTimeInHours()*tutor.getRate());
            cost.setText(" $"+sessioncost+"");
            cost.setVisibility(View.VISIBLE);
        }
        itemList.add(field1);
        itemList.add(field2);
        itemList.add(field3);



    }


    private void populateSelectionList(){
        adapter = new RequestSessionActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.sessionInfoList);
        list.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {

        if(v == requestButton){

            SessionRequest sessionRequest = new SessionRequest(selectedLocation,tutor.getID(),tutor.getName(),user.getUid(),user.getDisplayName(), field1.getData(),sessioncost,requestedTime );
            sessionReqList.add(sessionRequest);
          //  SessionRequest sessionRequest = new SessionRequest(tutor.getClasses(), selectedLocation, requestedTime, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),sessioncost);
            //sessionReqList.add(sessionRequest);
        databaseReference.child("Tutors").child(tutor.getID()).child("SessionRequests").setValue(sessionReqList);
        }
        if(v== backButton){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(RequestSessionActivity.this, R.layout.session_request_field, itemList);
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

    private void startPlacePicker(){
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try{
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent,requestcode_placepicker);
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == requestcode_placepicker && resultCode == RESULT_OK){
            displaySelectedPlaceFromPlacePicker(data);
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data){
        Place placeSelected = PlacePicker.getPlace(data,this);
        String name = placeSelected.getName().toString();
        String address = placeSelected.getAddress().toString();
        selectedLocation = address;
        itemList.get(1).setData(selectedLocation);
        adapter.notifyDataSetChanged();
    }

    private void registerFieldClicks() {
        ListView list = (ListView) findViewById(R.id.sessionInfoList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 //timeField = itemList.get(position);
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                if(position ==1){
                    startPlacePicker();


                }

                if(position ==2) {
                    Intent i = new Intent(RequestSessionActivity.this, TimePickerActivity.class);
                    i.putExtra("timeField", selectedAvailability);
                    i.putExtra("tutor", tutor);
                    i.putExtra("location", selectedLocation);
                    startActivity(i);
                }

            }
        });

    }
}
