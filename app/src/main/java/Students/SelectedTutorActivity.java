package Students;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import Misc.BottomNavHelper;

import Messaging.MessageActivity;
import com.lucriment.lucriment.R;

import Sessions.RequestSessionActivity;
import Sessions.Review;

import Sessions.TimePickerActivity;
import Messaging.ViewMessagesActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Tutors.AllReviewsActivity;
import Tutors.Availability;
import Tutors.TutorInfo;
import Tutors.TutorSessionsActivity;

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
    private TextView ratingText, locationText, headlineText;
    private ImageView imageView;
    private ListView optionsList;
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
    private String location;
    private ArrayList<String> options = new ArrayList<>();

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
        if(getIntent().hasExtra("location")){
            location = getIntent().getStringExtra("location");
        }
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.browseDisplayName);
        // backButton = (Button) findViewById(R.id.backButton);
        // educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        aboutField = (TextView) findViewById(R.id.tutorAboutField);
        rateField = (TextView) findViewById(R.id.browseRate);
        contactButton = (Button) findViewById(R.id.leftButton);
        imageView = (ImageView) findViewById(R.id.ProfileImage);
        classesField = (TextView) findViewById(R.id.classesField);
        requestButton = (Button) findViewById(R.id.rightButton);
        ratingText = (TextView) findViewById(R.id.rating);
        bookMarkButton = (Button) findViewById(R.id.bookMark);
        locationText = (TextView) findViewById(R.id.cityText);
        headlineText = (TextView) findViewById(R.id.title);
        optionsList = (ListView) findViewById(R.id.optionsList);


        options.add("Read all Reviews");
        options.add("Availability Calendar");
        options.add("Report this profile");
        ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,options);
        optionsList.setAdapter(optionAdapter);


        // selectedTutor = TutorListActivity.getTutor();
        headlineText.setText(selectedTutor.getHeadline());
        tutorName.setText(selectedTutor.getFullName());
//        educationField.setText(selectedTutor.getTitle());
        aboutField.setText(selectedTutor.getAbout());
        rateField.setText("$"+String.valueOf(selectedTutor.getRate()));
        //If tutor does not have a rating, set text
        if(score == 0){
            ratingText.setText("N/A");
        }else {
            ratingText.setText(score + "");
        }
        locationText.setText(location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Get Location if Empty
        if(location == null){
            DatabaseReference getLocationRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(selectedTutor.getId()).child("address");
            getLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    location = dataSnapshot.getValue(String.class);
                    locationText.setText(location);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        //     backButton.setOnClickListener(this);

        requestButton.setOnClickListener(this);
        bookMarkButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        if(selectedTutor.getProfileImage()!=null) {
            Glide.with(getApplicationContext())
                    .load(selectedTutor.getProfileImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }


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
                    Intent y = new Intent(SelectedTutorActivity.this, SettingsActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }

                if (itemId == R.id.sessions) {
                    Intent y = new Intent(SelectedTutorActivity.this, TutorSessionsActivity.class);
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void  processReviews(){
        TextView rName = (TextView) findViewById(R.id.reviewerName);
        TextView rScore = (TextView) findViewById(R.id.reviewScore);
        TextView rDate = (TextView) findViewById(R.id.reviewDate);
        TextView rText = (TextView) findViewById(R.id.reviewText2);
        ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        if(!revList.isEmpty()) {
            Review recentReview = revList.get(revList.size() - 1);

            rName.setText(recentReview.getAuthor());
            rScore.setText(recentReview.getRating() + "");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            rDate.setText(sdf.format(recentReview.getTimeStamp()));
            rText.setText(recentReview.getText());
        }
        else{
            rName.setText("This tutor has not yet received any reviews.");
            rScore.setVisibility(View.INVISIBLE);
            rDate.setVisibility(View.INVISIBLE);
            rText.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent i = new Intent(SelectedTutorActivity.this, AllReviewsActivity.class);
                    i.putExtra("tutorInfo", selectedTutor);
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo",userInfo);
                    i.putExtra("Score",score);
                    i.putParcelableArrayListExtra("reviews",revList);
                    startActivity(i);
                }
                if(position==1){
                    if(!userInfo.isHasPamyent()){
                        Toast.makeText(SelectedTutorActivity.this,"You must have a payment method to view this.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent i = new Intent(SelectedTutorActivity.this, TimePickerActivity.class);
                    i.putExtra("tutor", selectedTutor);
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo",userInfo);
                    i.putExtra("tutorScore",score);
                    startActivity(i);
                }if(position ==2){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            SelectedTutorActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Disputes");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("To report this profile, email us at contact@lucriment.com with the tutors first and last name, along with" +
                                    " why you are reporting them.")
                            .setCancelable(false)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.dismiss();
                                }
                            });


                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
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


            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            UserInfo selectedUser;

            selectedUser = selectedTutor.generateUserInfo();
            // if(myID.equalsIgnoreCase(((TextView)view).getText().toString())){
            //       myID = tutorId;
            //  }
            intent.putExtra("user", selectedUser);
            intent.putExtra("userType", userType);
            intent.putExtra("userInfo",userInfo);
            // intent.putExtra("userName", userName);
            startActivity(intent);

        }
        if(v == requestButton) {

            if(!userInfo.isHasPamyent()){
                Toast.makeText(SelectedTutorActivity.this,"You must have a payment method to request a session.",Toast.LENGTH_LONG).show();
                return;
            }

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
            List<android.location.Address> list = gc.getFromLocationName(selectedTutor.getPostalCode(),1);
            android.location.Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            LatLng sydney = new LatLng(lat, lng);

            googleMap.addCircle(new CircleOptions().center(sydney).radius(600).fillColor(0x440000ff).strokeColor(Color.BLUE).strokeWidth(2));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setMaxZoomPreference(23);
            googleMap.setMinZoomPreference(13);
            scrollView.scrollTo(0,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollView.scrollTo(0,0);
    }

    private class reviewAdapter extends ArrayAdapter<Review>  {

        public reviewAdapter(){
            super(SelectedTutorActivity.this, R.layout.reviewitem, revList);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentRev.getTimeStamp());
            date.setText(dateFormat.format(calendar.getTime()));

            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }


}
