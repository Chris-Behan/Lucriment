package Students;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.lucriment.lucriment.BaseActivity;
import com.lucriment.lucriment.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Misc.BottomNavHelper;
import Tutors.TutorInfo;

public class Favourites extends BaseActivity {
    //INITIALIZE VARIABLES
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<String> favourites = new ArrayList<String>();
    private String userType;
    private UserInfo userInfo;
    private ArrayList<TutorInfo> tutors = new ArrayList<>();
    private RelativeLayout noFavLayout;
    private double tutorScore;
    private ArrayList<String> favouritesLocations = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button undoButton;
    private ArrayAdapter<TutorInfo> adapter;
    private TutorInfo removedTutor;
    private boolean undo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        getSupportActionBar().setTitle("Favourites");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        undoButton = (Button) findViewById(R.id.undoButton);
        noFavLayout = (RelativeLayout) findViewById(R.id.noFavLayout);

        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getFavourites();

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo = false;
                tutors.add(removedTutor);
                favourites.add(removedTutor.getId());
                adapter.notifyDataSetChanged();
                undoButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_favourites;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.favourites;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
        return userInfo;
    }

    //GET LIST OF FAVOURITES TUTORS
    private void getFavourites(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("favourites");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favourites.clear();
                for(DataSnapshot t :dataSnapshot.getChildren()){
                    String favourite = t.getValue(String.class);
                    favourites.add(favourite);
                }
                if(favourites.isEmpty()){
                    noFavLayout.setVisibility(View.VISIBLE);
                }
                getTutors();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //GET LIST OF FAVOURITE TUTORS
    private void getTutors(){
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("tutors");
        tutorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tutors.clear();
                for(DataSnapshot t: dataSnapshot.getChildren()){
                    TutorInfo currentTutor = t.getValue(TutorInfo.class);

                    if(favourites.contains(currentTutor.getId())){
                        tutors.add(currentTutor);
                    }

                }
                getTutorLocations();
                populateTutorList();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTutorLocations(){
        Geocoder gc = new Geocoder(this);
        try {
            for(TutorInfo ti:tutors) {
                List<Address> addresses = gc.getFromLocationName(ti.getPostalCode(), 1);
                favouritesLocations.add(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class myListAdapter extends ArrayAdapter<TutorInfo> {

        public myListAdapter(){
            super(Favourites.this, R.layout.favourite_profile_layout, tutors);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.favourite_profile_layout, parent, false);
            }
            //Find tutor to work with
            final TutorInfo currentTutor = tutors.get(position);


            //fill the view
            final ImageView imageView = (ImageView)itemView.findViewById(R.id.ProfileImage);
            Glide.with(getApplicationContext())
                    .load(currentTutor.getProfileImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
            // set image imageVIew.setImageResource();
            TextView nameText = (TextView) itemView.findViewById(R.id.browseDisplayName);
            nameText.setText(currentTutor.getFullName());
            ImageView star = (ImageView) itemView.findViewById(R.id.imageView6);

            TextView ratingScore = (TextView) itemView.findViewById(R.id.rating);
            if(currentTutor.getRating()!=null) {
                Rating rating = currentTutor.getRating();
                double score = rating.getTotalScore()/rating.getNumberOfReviews();
                ratingScore.setText(score+"");

            }else{
                ratingScore.setVisibility(View.INVISIBLE);
                star.setVisibility(View.INVISIBLE);
            }
            TextView city = (TextView) itemView.findViewById(R.id.cityText);
            if(!favouritesLocations.isEmpty()) {
                city.setText(favouritesLocations.get(position));
            }

            TextView titleText = (TextView) itemView.findViewById(R.id.title);
            titleText.setText(currentTutor.getHeadline());

            Button bookMark = (Button) itemView.findViewById(R.id.fbookMark);
            bookMark.setSelected(true);
            bookMark.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00009FFF));

            bookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(Favourites.this, currentTutor.getFullName()+" Removed from favourites", Toast.LENGTH_SHORT).show();
                    undo = true;
                    beginRemoval(currentTutor);



                }
            });

            TextView rateText = (TextView) itemView.findViewById(R.id.browseRate);
            rateText.setText( "$"+String.valueOf(currentTutor.getRate())+"/hr" );


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
    private void beginRemoval(TutorInfo tutor){
        tutors.remove(tutor);
        favourites.remove(tutor.getId());
        removedTutor = tutor;
        adapter.notifyDataSetChanged();
        undoButton.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               undoButton.setVisibility(View.INVISIBLE);
                if(undo) {
                    databaseReference.setValue(favourites);
                    adapter.notifyDataSetChanged();
                }
            }
        }, 4000);

    }
    //REGISTER CLICKS
    private void registerFavouriteClicks(){
        ListView favList = (ListView) findViewById(R.id.tView);
        favList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TutorInfo clickedTutor = tutors.get(position);
                Rating tutorRating = clickedTutor.getRating();
                if(tutorRating!=null) {
                    tutorScore = tutorRating.getTotalScore() / tutorRating.getNumberOfReviews();

                }
                ProgressDialog progressDialog = new ProgressDialog(Favourites.this);
                progressDialog.setMessage("Loading");
                progressDialog.show();
                Intent i = new Intent(Favourites.this, SelectedTutorActivity.class);
                i.putExtra("selectedTutor", clickedTutor);
                i.putExtra("tutorScore",tutorScore);
                i.putExtra("userType", userType);
                i.putExtra("userInfo",userInfo);
                if(!favouritesLocations.isEmpty()) {
                    i.putExtra("location", favouritesLocations.get(position));
                }
                startActivity(i);
            }
        });
    }

    //POPULATE TUTOR LIST
    private void populateTutorList(){
        adapter = new Favourites.myListAdapter();
        ListView list = (ListView) findViewById(R.id.tView);
        list.setAdapter(adapter);
        registerFavouriteClicks();


    }
}
