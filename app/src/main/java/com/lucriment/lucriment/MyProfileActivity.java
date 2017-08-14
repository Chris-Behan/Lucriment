package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {

    private TextView personalName;
    private TutorListActivity tutorListActivity;
    private Button backButton;

    private TextView educationField;
    private TextView bioField;
    private boolean editing;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private UserInfo userInfo;
    private EditText editBioText;
    private FirebaseUser user;
    private Button logoutButton, aboutButton, editButton;
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> classes = new ArrayList<>();
    private StorageReference storageReference;
    private ProgressDialog picUploadDialog;
    private static final int GALLERYINTENT = 2;
    private ImageView imageView;
    private Spinner subjectSelector;
    private Spinner classSelector;
    private String subjectPath;
    private Uri downloadUri;
    private Button addClassButton;
    private TutorInfo tutorInfo;
    private boolean isTutor;
    private boolean addingClass = false;
    private String[] subjectArray;
    private ArrayList<String> subjectsTaught = new ArrayList<>();
    private  ArrayAdapter<String> adapter;
    private String userType;
    private BottomNavigationView bottomNavigationView;
    private boolean editingBio = false;
    private boolean editingRate = false;
    private TextView hourlyRate;

    private ArrayList<Review> revList = new ArrayList<>();
    private ArrayAdapter<Review> revAdapter;
    private ScrollView scrollView;

    private TextView ratingBar;
    private float score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        final String currentKey = firebaseAuth.getCurrentUser().getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);



        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0,0);

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("users");
                DataSnapshot tutorSnap = dataSnapshot.child("tutors");
                FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();

                if( studentSnap.hasChild(thisUser.getUid())){
                    for(DataSnapshot userSnapShot: studentSnap.getChildren()){
                        if(userSnapShot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                            userInfo = userSnapShot.getValue(UserInfo.class);
                            userType = userInfo.getUserType();
                        }
                    }
                } else if(tutorSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userType = userInfo.getUserType();
                }else{
                    finish();
                    startActivity(new Intent(MyProfileActivity.this, CreationActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot highSchoolSnap = dataSnapshot.child("subjects").child("highschool");

                for(DataSnapshot subjectSnap: highSchoolSnap.getChildren()){
                    subjects.add(subjectSnap.getKey());
                }



                handleSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("users");
                DataSnapshot tutorSnap = dataSnapshot.child("tutors").child(currentKey).child("subjects");
                for(DataSnapshot tutorSnapShot: tutorSnap.getChildren()){
                    subjectsTaught.add(tutorSnapShot.getValue().toString());
                }
                for(DataSnapshot userSnapShot: studentSnap.getChildren()){
                    if(userSnapShot.getKey().equals(currentKey)){
                        userInfo = userSnapShot.getValue(UserInfo.class);
                    }
                }
                populateTaughtList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reviewRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("reviews");
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

        // initialize buttons and fields
        personalName = (TextView) findViewById(R.id.browseDisplayName);

        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorAboutField);
        editBioText = (EditText) findViewById(R.id.editBioField);


        imageView = (ImageView) findViewById(R.id.ProfileImage);
        subjectSelector = (Spinner) findViewById(R.id.subjectSpinner);
        classSelector = (Spinner) findViewById(R.id.classSpinner);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        editButton = (Button) findViewById(R.id.editButton);
        aboutButton = (Button) findViewById(R.id.editAbout);
        hourlyRate = (TextView) findViewById(R.id.browseRate);


        picUploadDialog = new ProgressDialog(this);
        ratingBar = (TextView) findViewById(R.id.rating);
        String[] testarr = new String[]{"hello","goodbye"};


        // String dURI = "https://firebasestorage.googleapis.com/v0/b/lucriment.appspot.com/o/ProfilePics%2FRG095XpINNSl7W1BPFiIqtJvO2h2?alt=media&token=78db062a-a4c8-4221-893f-6510243d590b";

        // Picasso.with(PersonalProfileActivity.this).load(downloadUri).fit().centerCrop().into(imageView);

//        scheduleButton.setOnClickListener(this);
        addClassButton.setOnClickListener(this);

        logoutButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        editButton.setOnClickListener(this);




        if(userInfo!= null) {
            if(userInfo.getUserType().equals("tutor")){
                isTutor = true;
                getTutorInfo();

            }
            Glide.with(getApplicationContext())
                    .load(userInfo.getProfileImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }

        personalName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        if(userInfo!=null) {
//            educationField.setText(userInfo.getTitle());
        }




        //     if(userInfo.getProfileImage()!= null) {
        //  Picasso.with(PersonalProfileActivity.this).load("https://firebasestorage.googleapis.com/v0/b/lucriment.appspot.com/o/ProfilePics%2FRG095XpINNSl7W1BPFiIqtJvO2h2?alt=media&token=d18e97f6-3087-4858-9260-ff9694cc6bf7").fit().centerCrop().into(imageView);
        //    }
        //setup buttons and fields
        //  tutorName.setText();

    }

    /*
    private void updateNavigationBarState(int actionId){
        Menu menu = bottomNavigationView.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == actionId);
        }
    } */
    @Override
    int getContentViewId() {
        return R.layout.activity_personal_profile;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }

    //get Tutor info
    private void getTutorInfo(){
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
        databaseReference3.child("tutors").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tutorInfo = dataSnapshot.getValue(TutorInfo.class);
                bioField.setText(tutorInfo.getAbout());
                hourlyRate.setText("$"+tutorInfo.getRate()+"/hr");
                if(tutorInfo.getRating()!=null) {
                    Rating rating = tutorInfo.getRating();
                    score = (float) (rating.getTotalScore() / rating.getNumberOfReviews());
                    ratingBar.setText(score+"");

                }
                setUpMap();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void  processReviews(){
        revAdapter = new MyProfileActivity.reviewAdapter();
        ListView reviewList = (ListView) findViewById(R.id.reviewList);
        reviewList.setAdapter(revAdapter);
        scrollView.scrollTo(0,0);
    }
    private void populateTaughtList() {
        adapter = new MyProfileActivity.taughtClassAdapter();
        ListView list = (ListView) findViewById(R.id.taughtlist);
        list.setAdapter(adapter);
    }

    private void setUpMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = gc.getFromLocationName(tutorInfo.getPostalCode(),1);
            android.location.Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            LatLng sydney = new LatLng(lat, lng);

            googleMap.addCircle(new CircleOptions().center(sydney).radius(600).fillColor(0x440000ff).strokeColor(Color.BLUE).strokeWidth(2));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setMaxZoomPreference(23);
            googleMap.setMinZoomPreference(13);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class taughtClassAdapter extends ArrayAdapter<String> {

        public taughtClassAdapter(){
            super(MyProfileActivity.this, R.layout.taught_item, subjectsTaught);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.taught_item, parent, false);
            }
            //Find tutor to work with
            Button deleteButton = (Button) itemView.findViewById(R.id.delete);
            String subject = subjectsTaught.get(position);

            //fill the view

            TextView subjectText = (TextView) itemView.findViewById(R.id.taughtLabel);
            subjectText.setText(subject);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectsTaught.remove(position);
                    databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
                    adapter.notifyDataSetChanged();
                }
            });

            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(MyProfileActivity.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERYINTENT && resultCode == RESULT_OK){
            picUploadDialog.setMessage("Uploading...");
            picUploadDialog.show();

            Uri uri = data.getData();
            StorageReference profilePicPath = storageReference.child("ProfilePics").child(firebaseAuth.getCurrentUser().getUid());

            profilePicPath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    picUploadDialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    downloadUri = taskSnapshot.getDownloadUrl();
                    // userInfo.setProfileImage(downloadUri.toString());
                    databaseReference.child("users").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                    if(isTutor){
                        databaseReference.child("tutors").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                    }
                    Glide.with(getApplicationContext())
                            .load(downloadUri.toString())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    private class reviewAdapter extends ArrayAdapter<Review>  {

        public reviewAdapter(){
            super(MyProfileActivity.this, R.layout.reviewitem, revList);
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


    private void handleSpinner(){

        subjectArray = subjects.toArray(new String[subjects.size()]);
        ArrayAdapter<String> subjectNameAdapter = new ArrayAdapter<String>(MyProfileActivity.this,
                android.R.layout.simple_list_item_1, subjectArray);
        subjectNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSelector.setAdapter(subjectNameAdapter);

        subjectSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectPath = subjectSelector.getSelectedItem().toString();


                DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                db3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(subjectPath!= null) {
                            DataSnapshot categorySnap = dataSnapshot.child("subjects").child("highschool").child(subjectPath);
                            classes.clear();

                            for(DataSnapshot classSnap: categorySnap.getChildren()){
                                classes.add(classSnap.getValue().toString());

                            }
                            String[] classesArray = classes.toArray(new String[classes.size()]);
                            ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(MyProfileActivity.this,
                                    android.R.layout.simple_list_item_1, classesArray);
                            classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            classSelector.setAdapter(classNameAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        subjectPath = subjectSelector.getSelectedItem().toString();

    }



    @Override
    public void onClick(View v) {


        if(v == logoutButton){
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

        if(v == backButton){
            Intent i = new Intent(MyProfileActivity.this, ProfileActivity.class);
            i.putExtra("userInfo", userInfo);

            startActivity(i);

        }

        if(v == aboutButton){

            if(editingBio){
                editingBio = false;
                editBioText.setVisibility(View.INVISIBLE);
            }
            else{
                editBioText.setVisibility(View.VISIBLE);
                editingBio=true;
            }
            if(editingBio){
                bioField.setVisibility(View.INVISIBLE);
                editBioText.setVisibility(View.VISIBLE);

            }else {

                bioField.setVisibility(View.VISIBLE);
                tutorInfo.setAbout(editBioText.getText().toString());
                userInfo.setHeadline(editBioText.getText().toString());
                bioField.setText(editBioText.getText());
                editBioText.setVisibility(View.INVISIBLE);



                databaseReference.child("users").child(user.getUid()).child("about").setValue(tutorInfo.getAbout());
                if (userInfo.getUserType().equals("tutor")) {
                    databaseReference.child("tutors").child(user.getUid()).child("about").setValue(tutorInfo.getAbout());
                }
            }
        }

        if(v == addClassButton){
            if(addingClass){
                subjectsTaught.add(classSelector.getSelectedItem().toString());
                databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
                addingClass = false;

            }else{
                addingClass = true;
            }

            if(addingClass){
                addClassButton.setText("Select");
                subjectSelector.setVisibility(View.VISIBLE);
                classSelector.setVisibility(View.VISIBLE);
            }else{
                addClassButton.setText("Add Class");
                subjectSelector.setVisibility(View.INVISIBLE);
                classSelector.setVisibility(View.INVISIBLE);
            }
        }

        if(v == editButton){
            Intent i = new Intent(MyProfileActivity.this, EditTutorProfile.class);
            overridePendingTransition(R.anim.bottom_up,R.anim.top_down);
            i.putExtra("userInfo", userInfo);
            i.putExtra("userType",userType);

            startActivity(i);
        }




    }
}
