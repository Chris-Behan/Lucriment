package Students;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import Misc.BottomNavHelper;
import com.lucriment.lucriment.R;

import Sessions.SubjectListViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Tutors.TutorInfo;

public class TutorListActivity extends BaseActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder> mFirebaseAdapter;
    private UserInfo userInfo;
    private String userType;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, subjectRef;
    private double tutorScore;
    private ArrayList<TutorInfo> tutors = new ArrayList<>();
    ArrayAdapter<TutorInfo> adapter;
    private ArrayList<TutorInfo> searchResult = new ArrayList<>();
    private Menu currentMenu;
    private ProgressDialog progressDialog;
    private ArrayList<String> tutorAddresses;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> all = new ArrayList<>();
    private SubjectListViewAdapter subjectListAdapter;
    private ListView tutorList, subjectList;
    private SearchView searchView;
    private InputMethodManager inputManager;
    private RelativeLayout searchLayout;
    private TextView searchText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        tutorAddresses = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        tutorList = (ListView) findViewById(R.id.tView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        progressDialog.setMessage("Loading...");
       // progressDialog.show();

        searchLayout = (RelativeLayout) findViewById(R.id.startSearching);
        searchText = (TextView) findViewById(R.id.searchText);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        subjectListAdapter = new SubjectListViewAdapter(this,categories,subjects,all,new ArrayList<String>());
        subjectList = (ListView) findViewById(R.id.subjectList);
        subjectList.setAdapter(subjectListAdapter);
        subjectRef = FirebaseDatabase.getInstance().getReference().child("subjects");
        subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot category: dataSnapshot.getChildren()){
                    categories.add(category.getKey());
                    all.add(category.getKey());
                    for(DataSnapshot subject:category.getChildren()){
                        subjects.add(subject.getValue(String.class));
                        all.add(subject.getValue(String.class));
                    }
                }
                subjectListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        inputManager = (InputMethodManager) TutorListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                Query query = FirebaseDatabase.getInstance().getReference().child("tutors");
                searchResult.clear();
                tutors.clear();

                query.orderByChild(all.get(position)).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){
                            Object test = tutorSnapShot.getValue();
                            TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                            tutors.add(tutor);
                            searchResult.add(tutor);
                        }
                      //  getTutorAddresses();
                        populateTutorList();
                        setMenuSearch();
                        subjectList.setVisibility(View.INVISIBLE);
                        tutorList.setVisibility(View.VISIBLE);
                        if(tutors.isEmpty()){
                            searchText.setText("There are currently no tutors for that subject.");
                            searchLayout.setVisibility(View.VISIBLE);


                        }
                        try
                        {

                            inputManager.hideSoftInputFromWindow(TutorListActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        catch (Exception e)
                        {
                            // Ignore exceptions if any
                           // Log.e("KeyBoardUtil", e.toString(), e);
                        }
                        searchView.setQuery(all.get(position),true);
                        adapter.notifyDataSetChanged();
                        searchView.clearFocus();
                        // tutors =  collectNames((Map<String,Object>) dataSnapshot.getValue());
                        //  populateTutorList(tNames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors");


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
        setUp();
//            firebaseAuth = FirebaseAuth.getInstance();
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//            FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
//            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(thisUser.getUid());
//            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                    if (dataSnapshot.hasChild("firstName")) {
//
//                                userInfo = dataSnapshot.getValue(UserInfo.class);
//                                userType = userInfo.getUserType();
//                                if(userType.equals("tutor")){
//                                    Intent y = new Intent(TutorListActivity.this, TutorSessionsActivity.class);
//                                    y.putExtra("userType", getUserType());
//                                    y.putExtra("userInfo",getUserInformation());
//                                    startActivity(y);
//                                }
//                                if(userInfo.getPaymentInfo()!=null){
//                                    userInfo.setHasPamyent(true);
//                                }
//
//
//                    }  else {
//                        finish();
//                        startActivity(new Intent(TutorListActivity.this, CreationActivity.class));
//                    }
//
//
//                    // finish();
//                    //   startActivity(new Intent(ProfileActivity.this, TutorListActivity.class));
//                    // initializeButtons();
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            /*
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            BottomNavHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setOnNavigationItemSelectedListener(this); */

        //  Toast.makeText(ImageLayout.this, "Wait ! Fetching List...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        currentMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, currentMenu);
        MenuItem item = currentMenu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search by Class...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.INVISIBLE);
                tutorList.setVisibility(View.INVISIBLE);
                subjectList.setVisibility(View.VISIBLE);
                searchView.clearFocus();
               // Toast.makeText(TutorListActivity.this,"Triggered!",Toast.LENGTH_LONG).show();
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    tutorList.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                    subjectList.setVisibility(View.VISIBLE);
                }
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!subjects.contains(newText)) {
                    tutorList.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                    subjectList.setVisibility(View.VISIBLE);
                }

                all.clear();
                for(String s:subjects){
                    String sL = s.toLowerCase();
                    String newL = newText.toLowerCase();
                    if(sL.contains(newL)){
                        all.add(s);
                    }
                }
                subjectListAdapter.notifyDataSetChanged();

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
                if (ti.getAddress() == null){
                    List<Address> addresses = gc.getFromLocationName(ti.getPostalCode(), 1);
                if (!addresses.isEmpty()) {

                    tutorAddresses.add(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                }
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

        tutorList.setAdapter(adapter);
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
    protected int getContentViewId() {
        return R.layout.activity_image_layout;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.search;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
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
            /*
            if(!tutorAddresses.isEmpty()) {
                city.setText(tutorAddresses.get(position));
            } */
            if(currentTutor.getAddress()!=null) {
                city.setText(currentTutor.getAddress());
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
                ratingScore.setText("N/A");
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

                Intent i = new Intent(TutorListActivity.this, SelectedTutorActivity.class);
                i.putExtra("selectedTutor", selectedTutor1);
                i.putExtra("tutorScore",tutorScore);
                i.putExtra("userType", userType);
                i.putExtra("userInfo",userInfo);

                if(selectedTutor1.getAddress()!=null) {
                    i.putExtra("location", selectedTutor1.getAddress());
                }
                startActivity(i);
                finish();


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