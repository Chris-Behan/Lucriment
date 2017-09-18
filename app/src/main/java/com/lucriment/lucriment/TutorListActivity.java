package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TutorListActivity extends BaseActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder> mFirebaseAdapter;
    private UserInfo userInfo;
    private String userType;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private double tutorScore;
    private ArrayList<TutorInfo> tutors = new ArrayList<>();
    ArrayAdapter<TutorInfo> adapter;
    private ArrayList<TutorInfo> searchResult = new ArrayList<>();
    private Menu currentMenu;
    private ProgressDialog progressDialog;
    private ArrayList<String> tutorAddresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        tutorAddresses = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors");
        Query query = databaseReference;
        query.limitToFirst(25);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){
                    TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                    tutors.add(tutor);
                    searchResult.add(tutor);
                }
                getTutorAddresses();
                populateTutorList();
                setMenuSearch();

                // tutors =  collectNames((Map<String,Object>) dataSnapshot.getValue());
                //  populateTutorList(tNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("tutors");
      //  recyclerView = (RecyclerView)findViewById(R.id.rView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(TutorListActivity.this));
        getTutors();



        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");

        }

            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(thisUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.hasChild("firstName")) {

                                userInfo = dataSnapshot.getValue(UserInfo.class);
                                userType = userInfo.getUserType();
                                if(userType.equals("tutor")){
                                    Intent y = new Intent(TutorListActivity.this, TutorSessionsActivity.class);
                                    y.putExtra("userType", getUserType());
                                    y.putExtra("userInfo",getUserInformation());
                                    startActivity(y);
                                }
                                if(userInfo.getPaymentInfo()!=null){
                                    userInfo.setHasPamyent(true);
                                }


                    }  else {
                        finish();
                        startActivity(new Intent(TutorListActivity.this, CreationActivity.class));
                    }


                    // finish();
                    //   startActivity(new Intent(ProfileActivity.this, TutorListActivity.class));
                    // initializeButtons();

                    setUp();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            BottomNavHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //  Toast.makeText(ImageLayout.this, "Wait ! Fetching List...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        currentMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, currentMenu);
        MenuItem item = currentMenu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search by Class...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResult.clear();
                for (TutorInfo ti : tutors) {
                    ArrayList<String> subjects = ti.getSubjects();
                    if (subjects != null) {

                        for (String s : subjects) {
                            s = s.toLowerCase();
                            if (s.contains((newText))) {
                                if (!searchResult.contains(ti)) {
                                    searchResult.add(ti);
                                }
                            }
                        }
                    }

                }
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        currentMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    private void setMenuSearch(){




    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    private void getTutors(){


    }
    private void getTutorAddresses(){
        Geocoder gc = new Geocoder(this);
        try {
            for(TutorInfo ti:tutors) {
                List<Address> addresses = gc.getFromLocationName(ti.getPostalCode(), 1);
                if(!addresses.isEmpty()) {

                    tutorAddresses.add(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                }
            }
            progressDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private void populateTutorList(){
        //  populateTutorList();
        adapter = new myListAdapter();
        ListView list = (ListView) findViewById(R.id.tView);
        list.setAdapter(adapter);
        registerTutorClicks();
        //adapter.getView();

    }

    private void setUp(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_image_layout;
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


    private class myListAdapter extends ArrayAdapter<TutorInfo> {

        public myListAdapter(){
            super(TutorListActivity.this, R.layout.tutor_profile_layout, searchResult);
        }


        // @NonNull
        @Override
        public View getView(int position,  View convertView,  ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.tutor_profile_layout, parent, false);
            }
            //Find tutor to work with
            TutorInfo currentTutor = searchResult.get(position);

            TextView city = (TextView) itemView.findViewById(R.id.cityText);

            if(!tutorAddresses.isEmpty()) {
                city.setText(tutorAddresses.get(position));
            }
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ProfileImage);

                        Glide.with(getApplicationContext())

                                .load(currentTutor.getProfileImage())
                                .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                                .apply(RequestOptions.circleCropTransform())

                                .into(imageView);


            // set image imageVIew.setImageResource();
            TextView nameText = (TextView) itemView.findViewById(R.id.browseDisplayName);
            nameText.setText(currentTutor.getFullName());

            TextView ratingScore = (TextView) itemView.findViewById(R.id.rating);
            if(currentTutor.getRating()!=null) {
                Rating rating = currentTutor.getRating();
                double score = rating.getTotalScore()/rating.getNumberOfReviews();
                ratingScore.setText(score+"");
            }else{
                ratingScore.setText("  "+0);
            }

            TextView titleText = (TextView) itemView.findViewById(R.id.title);
            titleText.setText(currentTutor.getHeadline());

            TextView rateText = (TextView) itemView.findViewById(R.id.browseRate);
            rateText.setText( "$"+String.valueOf(currentTutor.getRate())+"/hr" );


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    private void registerTutorClicks() {
        ListView list = (ListView) findViewById(R.id.tView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TutorInfo selectedTutor1 = searchResult.get(position);
                Rating tutorRating = selectedTutor1.getRating();
                if(tutorRating!=null) {
                    tutorScore = tutorRating.getTotalScore() / tutorRating.getNumberOfReviews();

                }
                ProgressDialog progressDialog = new ProgressDialog(TutorListActivity.this);

                progressDialog.setMessage("Loading...");
                progressDialog.show();
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                progressDialog.dismiss();
                Intent i = new Intent(TutorListActivity.this, SelectedTutorActivity.class);
                i.putExtra("selectedTutor", selectedTutor1);
                i.putExtra("tutorScore",tutorScore);
                i.putExtra("userType", userType);
                i.putExtra("userInfo",userInfo);
                if(!tutorAddresses.isEmpty()) {
                    i.putExtra("location", tutorAddresses.get(position));
                }
                startActivity(i);

            }
        });

    }




    //View Holder For Recycler View
    public static class ImageLayoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tutorName;
        private final ImageView image_url;

        private final TextView rateText;
        private final TextView ratingBar;



        public ImageLayoutViewHolder(final View itemView) {
            super(itemView);
            image_url = (ImageView) itemView.findViewById(R.id.ProfileImage);
            tutorName = (TextView) itemView.findViewById(R.id.browseDisplayName);

            rateText = (TextView) itemView.findViewById(R.id.browseRate);
            ratingBar = (TextView) itemView.findViewById(R.id.rating);
        }


        private void RateText(String rate){
            rateText.setText("$"+rate+"/hr");
        }

        private void RatingBar(Float rating){
            ratingBar.setText(rating+"");
        }


        private void Image_Title(String title) {
            tutorName.setText(title);
        }

        private void Image_URL(String title) {
// image_url.setImageResource(R.drawable.loading);
            Glide.with(itemView.getContext())
                    .load(title)


                    .thumbnail(0.1f)

                    .into(image_url);
        }
    }
}