package tutors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucriment.lucriment.BaseActivity;
import misc.BottomNavHelper;
import com.lucriment.lucriment.LoginActivity;
import students.ProfileActivity;
import com.lucriment.lucriment.R;
import students.Rating;
import sessions.Review;
import students.SettingsActivity;
import students.UserInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import students.TutorListActivity;

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
    private Button logoutButton, aboutButton;
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> classes = new ArrayList<>();
    private StorageReference storageReference;
    private ProgressDialog picUploadDialog;
    private static final int GALLERYINTENT = 2;
    private ImageView imageView;

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
    private TextView hourlyRate, headline, cityText;
    private ListView optionsList;

    private ArrayList<Review> revList = new ArrayList<>();
    private ArrayAdapter<Review> revAdapter;
    private ScrollView scrollView;
    private ArrayList<String> subList = new ArrayList<>();
    private TextView ratingBar;
    private float score;
    private ArrayList<String> options = new ArrayList<>();
    private String classesTaught ="";

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
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.about_action_bar);
        View view =getSupportActionBar().getCustomView();
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Profile");
        TextView back = (TextView) view.findViewById(R.id.action_bar_back);
        TextView edit = (TextView) view.findViewById(R.id.edit_about_action_bar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, SettingsActivity.class);

                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                i.putExtra("tutorInfo",tutorInfo);
                startActivity(i);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, EditTutorProfile.class);
                overridePendingTransition(R.anim.bottom_up,R.anim.top_down);
                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                startActivity(i);
            }
        });

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

        DatabaseReference subjectRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("subjects");
        subjectRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sSnapShot:dataSnapshot.getChildren()){
                    subList.add(sSnapShot.getValue(String.class));
                }

                for(String s:subList){
                    classesTaught = classesTaught + s+ "  ";
                }
                TextView subjectList = (TextView) findViewById(R.id.taughtlist);
                subjectList.setText(classesTaught);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reviewRoot = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("reviews");
        reviewRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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

        headline = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.ProfileImage);

        addClassButton = (Button) findViewById(R.id.addClassButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        aboutButton = (Button) findViewById(R.id.editAbout);
        hourlyRate = (TextView) findViewById(R.id.browseRate);
        cityText = (TextView) findViewById(R.id.cityText);



        picUploadDialog = new ProgressDialog(this);
        ratingBar = (TextView) findViewById(R.id.rating);
        String[] testarr = new String[]{"hello","goodbye"};



        // String dURI = "https://firebasestorage.googleapis.com/v0/b/lucriment.appspot.com/o/ProfilePics%2FRG095XpINNSl7W1BPFiIqtJvO2h2?alt=media&token=78db062a-a4c8-4221-893f-6510243d590b";

        // Picasso.with(PersonalProfileActivity.this).load(downloadUri).fit().centerCrop().into(imageView);

//        scheduleButton.setOnClickListener(this);
        addClassButton.setOnClickListener(this);

        logoutButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);





        if(userInfo!= null) {
            if(userInfo.getUserType().equals("tutor")){
                isTutor = true;
                getTutorInfo();

            }
            if(userInfo.getProfileImage()!=null) {
                Glide.with(getApplicationContext())
                        .load(userInfo.getProfileImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }
        }

        personalName.setText(userInfo.getFullName());
        if(userInfo!=null) {
//            educationField.setText(userInfo.getTitle());
        }




    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_personal_profile;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
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
                headline.setText(tutorInfo.getHeadline());
                try {
                    Geocoder gc = new Geocoder(getApplicationContext());
                    List<Address> addresses = gc.getFromLocationName(tutorInfo.getPostalCode(), 1);
                    cityText.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                } catch (IOException e) {
                    e.printStackTrace();

                }
                if(tutorInfo.getRating()!=null) {
                    Rating rating = tutorInfo.getRating();
                    score = (float) (rating.getTotalScore() / rating.getNumberOfReviews());
                    ratingBar.setText(score+"");


                }else{
                    ratingBar.setText("0");
                }
                setUpMap();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void  processReviews(){
        TextView rName = (TextView) findViewById(R.id.reviewerName);
        TextView rScore = (TextView) findViewById(R.id.reviewScore);
        TextView rDate = (TextView) findViewById(R.id.reviewDate);
        TextView rText = (TextView) findViewById(R.id.reviewText2);
        ImageView star = (ImageView) findViewById(R.id.imageView3);
        if(!revList.isEmpty()) {
            Review recentReview = revList.get(revList.size() - 1);

            rName.setText(recentReview.getAuthor());
            rScore.setText(recentReview.getRating() + "");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            rDate.setText(sdf.format(recentReview.getTimeStamp()));
            rText.setText(recentReview.getText());
        }else{
            rName.setText("No Recent Reviews");
            star.setVisibility(View.INVISIBLE);
            rScore.setVisibility(View.INVISIBLE);
            rDate.setVisibility(View.INVISIBLE);
            rText.setVisibility(View.INVISIBLE);
        }
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








    }
}
