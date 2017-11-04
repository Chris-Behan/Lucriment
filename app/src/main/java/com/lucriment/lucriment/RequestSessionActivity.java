package com.lucriment.lucriment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.Map;

public class RequestSessionActivity extends BaseActivity implements View.OnClickListener, SubjectSelectionDialog.NoticeDialogListener {

    private Availability selectedAvailability;
    private  SubjectSelectionDialog se = new SubjectSelectionDialog();
    private String subjectSelection;
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
    private double sessioncost;
    private String selectedTimeInterval;
    private UserInfo userInfo;
    private String userType, tutorLocation;
    private TextView ratingBar;
    private TutorInfo selectedTutor;
    private ProgressDialog progressDialog;
    private TextView headline, city;
    double score;
//    private SubjectSelectionDialog se = new SubjectSelectionDialog();
    TwoItemField field1 = new TwoItemField("Subject", "Select");
    TwoItemField field2 = new TwoItemField("Location", "Select");
    TwoItemField field3 = new TwoItemField("Time", "Select");
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        score = getIntent().getDoubleExtra("tutorScore",0);
        if(getIntent().hasExtra("tutor")){
            selectedTutor = getIntent().getParcelableExtra("tutor");
        }
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        if(getIntent().hasExtra("subject"))
            subjectSelection = getIntent().getStringExtra("subject");
        if(getIntent().hasExtra("location"))
            selectedLocation = getIntent().getStringExtra("location");
        if(getIntent().hasExtra("Availability"))
        selectedAvailability = getIntent().getParcelableExtra("Availability");
        tutor = getIntent().getParcelableExtra("tutor");
        nameView = (TextView) findViewById(R.id.browseDisplayName);
        rateView = (TextView) findViewById(R.id.browseRate);
        imageView = (ImageView) findViewById(R.id.ProfileImage);
        requestButton = (Button) findViewById(R.id.requestButton);
        cost = (TextView) findViewById(R.id.costView);
        headline = (TextView) findViewById(R.id.title);
        city = (TextView) findViewById(R.id.cityText);
        tutorLocation = selectedTutor.getAddress();

        city.setText(tutorLocation);

        headline.setText(selectedTutor.getHeadline());
        ratingBar = (TextView) findViewById(R.id.rating);
        ratingBar.setText(""+ score);
        if(getIntent().hasExtra("requestedTime")){
            requestedTime = getIntent().getParcelableExtra("requestedTime");

        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        storageReference = FirebaseStorage.getInstance().getReference();

        nameView.setText(tutor.getFullName());
        rateView.setText("$"+String.valueOf(tutor.getRate())+"/hr");
        Glide.with(getApplicationContext())
                .load(selectedTutor.getProfileImage())
                .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);



        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("sessions").child(user.getUid()+"_"+tutor.getId());
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
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, h:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat(" - h:mm a");
            cal.setTimeInMillis(requestedTime.getTo());
            int toHour = cal.get(Calendar.HOUR_OF_DAY);
            int toMinute = cal.get(Calendar.MINUTE);

            selectedTimeInterval = sdf1.format(requestedTime.getFrom())+sdf2.format(requestedTime.getTo());
        }

        populateItemList();
        populateSelectionList();
        registerFieldClicks();

        requestButton.setOnClickListener(this);


    }

    @Override
    int getContentViewId() {
        return R.layout.activity_request_session;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.search;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }


    private void populateItemList(){
        if(subjectSelection!=null){
            field1.setData(subjectSelection);
        }

        if(selectedLocation!=null){
            field2.setData(selectedLocation);
        }


        if(requestedTime==null){
          //  field1.setData(tutor.getSubjects().get(0));
        }else{
          //  field1.setData(tutor.getSubjects().get(0));
           field3.setData(selectedTimeInterval);
            double r = requestedTime.returnTimeInHours();
            sessioncost =  Math.round(((requestedTime.returnTimeInHours()*tutor.getRate()*1.029)+0.3)*100.0)/100.0 ;
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
            if(field1.getData().isEmpty()||field1.getData().equals("Select")){
                Toast.makeText(RequestSessionActivity.this, "Please select a subject",Toast.LENGTH_LONG).show();
                return;
            }
            else if(field2.getData().isEmpty()||field2.getData().equals("Select")){
                Toast.makeText(RequestSessionActivity.this, "Please select a location",Toast.LENGTH_LONG).show();
                return;
            }else if(field3.getData().isEmpty()||field3.getData().equals("Select")){
                Toast.makeText(RequestSessionActivity.this, "Please select a time",Toast.LENGTH_LONG).show();
                return;
            }

            SessionRequest sessionRequest = new SessionRequest(selectedLocation,tutor.getId(),tutor.getFullName(),user.getUid(),userInfo.getFullName(), field1.getData(),sessioncost,requestedTime );
            sessionReqList.add(sessionRequest);
          //  SessionRequest sessionRequest = new SessionRequest(tutor.getClasses(), selectedLocation, requestedTime, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),sessioncost);
            //sessionReqList.add(sessionRequest);
            DatabaseReference pushID = databaseReference.child("keys").push();
            pushID.setValue(true);
            String sessionKey = pushID.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("requestNotifications");
            HashMap<String, String> notificationMap = new HashMap<String, String>();
            notificationMap.put("from", userInfo.getId());
            notificationRef.child(tutor.getId()).push().setValue(notificationMap);

            databaseReference.child("sessions").child(userInfo.getId()).child(user.getUid()+"_"+tutor.getId()).child(sessionKey).setValue(sessionRequest);
            databaseReference.child("sessions").child(tutor.getId()).child(user.getUid()+"_"+tutor.getId()).child(sessionKey).setValue(sessionRequest);

            Toast.makeText(RequestSessionActivity.this, "Request Sent.",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RequestSessionActivity.this, SelectedTutorActivity.class);

            i.putExtra("userType", userType);
            i.putExtra("userInfo",userInfo);
            i.putExtra("selectedTutor", tutor);
            i.putExtra("tutorScore",score);
            startActivity(i);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(RequestSessionActivity.this, SelectedTutorActivity.class);

        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        i.putExtra("selectedTutor", tutor);
        i.putExtra("tutorScore",score);
        startActivity(i);
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        subjectSelection = se.getSelection();
        field1.setData(subjectSelection);
        itemList.get(0).setData(subjectSelection);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
                if(position ==0){

                    Bundle args = new Bundle();
                    if(tutor.returnSubjectString()!=null) {
                        String[] testArr = tutor.stringToArr(tutor.returnSubjectString());
                        args.putStringArray("subjects", testArr);
                        se.setArguments(args);
                        se.show(getFragmentManager(), "my dialog");
                    }else {
                        Toast.makeText(RequestSessionActivity.this, "This tutor does not yet teach any subjects", Toast.LENGTH_LONG).show();
                    }
                }
                if(position ==1){

                    startPlacePicker();



                }

                if(position ==2) {
                    Intent i = new Intent(RequestSessionActivity.this, TimePickerActivity.class);
                    i.putExtra("timeField", selectedAvailability);
                    i.putExtra("tutor", tutor);
                    i.putExtra("location", selectedLocation);
                    i.putExtra("subject",subjectSelection);
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo",userInfo);
                    i.putExtra("tutorScore",score);
                    startActivity(i);
                }

            }
        });

    }
}
