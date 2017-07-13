package com.lucriment.lucriment;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
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
        if(userInfo==null) {

            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    DataSnapshot tutorSnap = dataSnapshot.child("tutors");
                    FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
                    DataSnapshot studentSnap = dataSnapshot.child("users").child(thisUser.getUid());
                    if(thisUser.getDisplayName()!=null){

                        
                    }

                    if (studentSnap.hasChild("firstName")) {

                                userInfo = studentSnap.getValue(UserInfo.class);
                                userType = userInfo.getUserType();

                    } else if (tutorSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        userType = userInfo.getUserType();
                    } else {
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
        }else {

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            BottomNavHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
         //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //   getTutors();
            //   registerTutorClicks();
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
        //  Toast.makeText(ImageLayout.this, "Wait ! Fetching List...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item =  menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search by Class...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*
                searchResult.clear();
                for(TutorInfo ti:tutors){
                    ArrayList<String> subjects = ti.getSubjects();
                    if(subjects!=null){

                        for(String s:subjects){
                            s= s.toLowerCase();
                            if(s.contains((query))){
                                searchResult.add(ti);
                            }
                        }
                    }

                }
                adapter.notifyDataSetChanged(); */
              //  mFirebaseAdapter.equals(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResult.clear();
                for(TutorInfo ti:tutors){
                    ArrayList<String> subjects = ti.getSubjects();
                    if(subjects!=null){

                        for(String s:subjects){
                            s= s.toLowerCase();
                            if(s.contains((newText))){
                                if(!searchResult.contains(ti)) {
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
    protected void onStart() {
        super.onStart();

    }
    private void getTutors(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){
                    TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                    tutors.add(tutor);
                    searchResult.add(tutor);
                }
                populateTutorList();
                // tutors =  collectNames((Map<String,Object>) dataSnapshot.getValue());
                //  populateTutorList(tNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

            //fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ProfileImage);

            Glide.with(getApplicationContext())
                    .load(currentTutor.getProfileImage())
                    .into(imageView);
            // set image imageVIew.setImageResource();
            TextView nameText = (TextView) itemView.findViewById(R.id.browseDisplayName);
            nameText.setText(currentTutor.getFullName());

            RatingBar ratingScore = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            if(currentTutor.getRating()!=null) {
                Rating rating = currentTutor.getRating();
                double score = rating.getTotalScore()/rating.getNumberOfReviews();
                ratingScore.isIndicator();
                ratingScore.setRating((float) score);
            }else{
                ratingScore.setRating(0);
            }

            TextView classText = (TextView) itemView.findViewById(R.id.browseClasses);
            classText.setText(currentTutor.arrToString(currentTutor.getSubjects()));

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
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(TutorListActivity.this, SelectedTutorActivity.class);
                i.putExtra("selectedTutor", selectedTutor1);
                i.putExtra("tutorScore",tutorScore);
                i.putExtra("userType", userType);
                i.putExtra("userInfo",userInfo);
                startActivity(i);

            }
        });

    }




    //View Holder For Recycler View
    public static class ImageLayoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tutorName;
        private final ImageView image_url;
        private final TextView subjectsText;
        private final TextView rateText;
        private final RatingBar ratingBar;



        public ImageLayoutViewHolder(final View itemView) {
            super(itemView);
            image_url = (ImageView) itemView.findViewById(R.id.ProfileImage);
            tutorName = (TextView) itemView.findViewById(R.id.browseDisplayName);
            subjectsText = (TextView) itemView.findViewById(R.id.browseClasses);
            rateText = (TextView) itemView.findViewById(R.id.browseRate);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar2);
        }

        private void SubjectsText(String subjects){
            subjectsText.setText(subjects);
        }
        private void RateText(String rate){
            rateText.setText("$"+rate+"/hr");
        }

        private void RatingBar(Float rating){
            ratingBar.setRating(rating);
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