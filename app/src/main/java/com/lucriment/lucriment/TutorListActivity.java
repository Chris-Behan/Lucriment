package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TutorListActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference databaseReference;
    private Button backButton;
    public TutorInfo selectedTutor;
    private StorageReference storageReference;
    private double tutorScore;

    private List<TutorInfo> tutors = new ArrayList<TutorInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getTutors();
        registerTutorClicks();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.search:
                        finish();
                        startActivity(new Intent(TutorListActivity.this, TutorListActivity.class));
                        TutorListActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.sessions:
                        Intent y = new Intent(TutorListActivity.this, SessionsActivity.class);
                     //   y.putExtra("userType", userType);
                        startActivity(y);
                        TutorListActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.inbox:
                        finish();
                        startActivity(new Intent(TutorListActivity.this, ViewMessagesActivity.class));
                        TutorListActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.profile:
                        Intent i = new Intent(TutorListActivity.this, ProfileActivity.class);
                       // i.putExtra("userInfo", userInfo);
                        startActivity(i);
                        TutorListActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;


                }
                return false;
            }

        });
        //initialize buttons


        //set onclick listener



        storageReference = FirebaseStorage.getInstance().getReference();
        //tutors.add(new TutorInfo("joe", "rob", "biull", "pete", 5));



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        startActivity(new Intent(TutorListActivity.this, ProfileActivity.class));
        return true;
    }




    private void getTutors(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){
                    TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                    tutors.add(tutor);
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
        ArrayAdapter<TutorInfo> adapter = new myListAdapter();
        ListView list = (ListView) findViewById(R.id.tView);
        list.setAdapter(adapter);
        //adapter.getView();

    }

    public TutorInfo getTutor(){
        return selectedTutor;
    }

    @Override
    public void onClick(View v) {


    }



    private class myListAdapter extends ArrayAdapter<TutorInfo> {

        public myListAdapter(){
            super(TutorListActivity.this, R.layout.tutor_profile_layout, tutors);
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
            TutorInfo currentTutor = tutors.get(position);

            //fill the view
            final ImageView imageView = (ImageView)itemView.findViewById(R.id.ProfileImage);
            new DownloadImageTask(imageView)
                    .execute(currentTutor.getProfileImage());
            // set image imageVIew.setImageResource();
            TextView nameText = (TextView) itemView.findViewById(R.id.browseDisplayName);
            nameText.setText(currentTutor.getFullName());

            RatingBar ratingScore = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            if(currentTutor.getRating()!=null) {
                Rating rating = currentTutor.getRating();
                double score = rating.getTotalScore()/rating.getNumberOfReviews();
                ratingScore.isIndicator();
                ratingScore.setRating((float) score);
            }else{}

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
                TutorInfo selectedTutor1 = tutors.get(position);
                Rating tutorRating = selectedTutor1.getRating();
                if(tutorRating!=null) {
                     tutorScore = tutorRating.getTotalScore() / tutorRating.getNumberOfReviews();

                }
               // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(TutorListActivity.this, SelectedTutorActivity.class);
                i.putExtra("selectedTutor", selectedTutor1);
                i.putExtra("tutorScore",tutorScore);
                startActivity(i);

            }
        });

    }

}
