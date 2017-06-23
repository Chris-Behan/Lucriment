package com.lucriment.lucriment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SelectedTutorActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private TutorInfo selectedTutor;
    private TextView tutorName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private Button contactButton;
    private Button requestButton;
    private TextView educationField;
    private TextView aboutField;
    private TextView rateField;
    private TextView classesField;
    private RatingBar ratingBar;
    private ImageView imageView;
    private StorageReference storageReference;
    private DatabaseReference userRoot = FirebaseDatabase.getInstance().getReference().child("users");
   // private
    private String bio;
    private UserInfo userInfo;
    private String myChats;
    private String myChats2;
    private List<UserInfo> users;
    private ArrayList<String> subList = new ArrayList<>();
    private String tutorID;
    private String classesTaught = "";
    private ArrayList<Availability> avaList = new ArrayList<>();
    private ArrayList<Review> revList = new ArrayList<>();
    private ArrayAdapter<Review> revAdapter;
    double score;
    private ScrollView scrollView;
    private String userType;
    private MapView mapView;
    private Button bookMarkButton;
    private ArrayList<String> favourites = new ArrayList<>();
    private boolean favourited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demolayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectedTutor = getIntent().getParcelableExtra("selectedTutor");
        score = getIntent().getDoubleExtra("tutorScore",0);
        storageReference = FirebaseStorage.getInstance().getReference();
        tutorID = selectedTutor.getId();
        Rating rating = selectedTutor.getRating();

       scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0,0);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.tutorName);
        // backButton = (Button) findViewById(R.id.backButton);
        // educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        aboutField = (TextView) findViewById(R.id.tutorAboutField);
        rateField = (TextView) findViewById(R.id.tutorRateField);
        contactButton = (Button) findViewById(R.id.contactButton);
        imageView = (ImageView) findViewById(R.id.imageView3);
        classesField = (TextView) findViewById(R.id.classesField);
        requestButton = (Button) findViewById(R.id.requestButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar3);
        bookMarkButton = (Button) findViewById(R.id.bookMark);

        // selectedTutor = TutorListActivity.getTutor();

        tutorName.setText(selectedTutor.getFullName());
//        educationField.setText(selectedTutor.getTitle());
        aboutField.setText(selectedTutor.getAbout());
        rateField.setText("$"+String.valueOf(selectedTutor.getRate()));
        ratingBar.isIndicator();
        ratingBar.setRating((float) score);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //     backButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        requestButton.setOnClickListener(this);
        bookMarkButton.setOnClickListener(this);
        Glide.with(getApplicationContext())
                .load(selectedTutor.getProfileImage())
                .into(imageView);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.search) {
                    Intent y = new Intent(SelectedTutorActivity.this, TutorListActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }
                if (itemId == R.id.profile) {
                    Intent y = new Intent(SelectedTutorActivity.this, PersonalProfileActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }

                if (itemId == R.id.sessions) {
                    Intent y = new Intent(SelectedTutorActivity.this, SessionsActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                    //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                if (itemId == R.id.inbox) {
                    Intent y = new Intent(SelectedTutorActivity.this, ViewMessagesActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }
                if(itemId == R.id.favourites){
                    Intent y = new Intent(SelectedTutorActivity.this, Favourites.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);

                }


                finish();
                return false;
            }
        });

        DatabaseReference favouriteRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("favourites");
        favouriteRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favourites.clear();
                for(DataSnapshot f: dataSnapshot.getChildren()){
                    String tutorID = f.getValue(String.class);
                    favourites.add(tutorID);
                }
                favouriteButtonState();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference subjectRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutorID).child("subjects");
        subjectRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sSnapShot:dataSnapshot.getChildren()){
                        subList.add(sSnapShot.getValue(String.class));
                }

                for(String s:subList){
                    classesTaught = classesTaught + s+ "  ";
                }
                classesField.setText(classesTaught);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference reviewRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutorID).child("reviews");
        reviewRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot revSnap: dataSnapshot.getChildren()){
                    Review rev = revSnap.getValue(Review.class);
                    revList.add(rev);
                }
                processReviews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference tutorRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutorID).child("availability");
        tutorRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot avaSnapShot: dataSnapshot.getChildren()){
                    Availability ava = avaSnapShot.getValue(Availability.class);
                    avaList.add(ava);

                }
               // populateScheduleList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<String> set = new HashSet<String>();
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                        .iterator();
                users = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    UserInfo user = dataSnapshotChild.getValue(UserInfo.class);
                    if(user.getId().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                   myChats= user.getChatsWith();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //registerScheduleClicks();


    //    Toast.makeText(getApplicationContext(), "Updated Complete", Toast.LENGTH_SHORT).show();
      //  progressDialog.dismiss();
       /* StorageReference pathReference = storageReference.child("ProfilePics").child(selectedTutor.getId());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(SelectedTutorActivity.this).load(uri).fit().centerCrop().into(imageView);
            }
        }); */
        //setup buttons and fields
      //  tutorName.setText();

    }
    //HANDLE STATE OF FAVOURITED BUTTON
    private void favouriteButtonState(){
        if(favourites.contains(selectedTutor.getId())){
            favourited = true;
            bookMarkButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00009FFF));
            bookMarkButton.setSelected(true);

        }else{
            favourited = false;
            bookMarkButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00009FFF));
            bookMarkButton.setSelected(false);
        }

    }
/*
    @Override
    int getContentViewId() {
        return R.layout.demolayout;
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
*/
    private void  processReviews(){
        revAdapter = new SelectedTutorActivity.reviewAdapter();
        ListView reviewList = (ListView) findViewById(R.id.reviewList);
        reviewList.setAdapter(revAdapter);
        scrollView.scrollTo(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(SelectedTutorActivity.this, TutorListActivity.class);
        i.putExtra("tutor", selectedTutor);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            finish();
            startActivity(new Intent(SelectedTutorActivity.this, TutorListActivity.class));
        }
        if(v == contactButton){
            DatabaseReference chatRoot = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            myChats = (myChats + selectedTutor.getId().toString());
            chatRoot.child("chatsWith").setValue(myChats);
            DatabaseReference chatRoot2 = FirebaseDatabase.getInstance().getReference().child("users").child(selectedTutor.getId());
            myChats2 = (myChats2 + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
            chatRoot2.child("chatsWith").setValue(myChats2);
            Intent i = new Intent(SelectedTutorActivity.this, ViewMessagesActivity.class);
            i.putExtra("tutorID", selectedTutor.getId());
            i.putExtra("userType", userType);
            i.putExtra("userInfo",userInfo);
            startActivity(i);
        }
        if(v == requestButton) {

            Intent i = new Intent(SelectedTutorActivity.this, RequestSessionActivity.class);


            i.putExtra("tutor", selectedTutor);
            i.putExtra("userType", userType);
            i.putExtra("userInfo",userInfo);
            i.putExtra("tutorScore",score);
            startActivity(i);
        }
        if(v == bookMarkButton){
            if(!favourited) {
                favourites.add(selectedTutor.getId());
                DatabaseReference favouritesRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("favourites");
                favouritesRoot.setValue(favourites);
                bookMarkButton.setSelected(true);
                bookMarkButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00009FFF));
                favourited = true;

            }else{
                favourites.remove(selectedTutor.getId());
                DatabaseReference favouritesRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("favourites");
                favouritesRoot.setValue(favourites);
                bookMarkButton.setSelected(false);
                bookMarkButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00009FFF));
                favourited = false;

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder gc = new Geocoder(this);
        try {
            List<android.location.Address> list = gc.getFromLocationName("Calgary",1);
            android.location.Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            LatLng sydney = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(sydney)
                    .title("Calgary"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setMaxZoomPreference(23);
            googleMap.setMinZoomPreference(15);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class reviewAdapter extends ArrayAdapter<Review>  {

        public reviewAdapter(){
            super(SelectedTutorActivity.this, R.layout.reviewitem, revList);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.reviewitem, parent, false);
            }
            final Review currentRev = revList.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.revItemName);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView review = (TextView) itemView.findViewById(R.id.reviewText);
            RatingBar rating = (RatingBar) itemView.findViewById(R.id.reviewScore);

            name.setText(currentRev.getAuthor());

            review.setText(currentRev.getText());

            rating.setRating((float) currentRev.getRating());



            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }

/*
    private class myListAdapter extends ArrayAdapter<Availability> {

        public myListAdapter(){
            super(SelectedTutorActivity.this, R.layout.timecardlayout, avaList);
        }


        // @NonNull
        @Override
        public View getView(int position,  View convertView,  ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.timecardlayout, parent, false);
            }

            Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView fromText = (TextView) itemView.findViewById(R.id.fromView);
         //   fromText.setText(currentAva.getFromhour() + ":" + currentAva.getFromminute() + " - " + currentAva.getTohour() + ":"+ currentAva.getTominute());



            TextView dateText = (TextView) itemView.findViewById(R.id.dateView);
          //  dateText.setText(currentAva.getMonth()+", "+currentAva.getDay()+", "+currentAva.getYear());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
/*
    private void populateScheduleList(){
        //  populateTutorList();
        ArrayAdapter<Availability> adapter = new SelectedTutorActivity.myListAdapter();
        // ArrayAdapter<TutorInfo> adapter = new TutorListActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.tutorAList);
        list.setAdapter(adapter);
        //adapter.getView();

    }
    */
    /*
    private void registerScheduleClicks() {
        ListView list = (ListView) findViewById(R.id.tutorAList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Availability selectedAvailability = avaList.get(position);
                Intent i = new Intent(SelectedTutorActivity.this, RequestSessionActivity.class);
                i.putExtra("Availability", selectedAvailability);
                i.putExtra("tutor", selectedTutor);

                startActivity(i);

            }
        });


    }
    */
}
