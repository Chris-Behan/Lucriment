package com.lucriment.lucriment;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class AllReviewsActivity extends AppCompatActivity {
    private UserInfo userInfo;
    private String userType;
    private TutorInfo tutorInfo;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ListView reviewList;
    private double score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.all_reviews_actionbar);
        View view =getSupportActionBar().getCustomView();


        // GET INTENTS
        score = getIntent().getDoubleExtra("Score",0);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("tutorInfo")){
            tutorInfo = getIntent().getParcelableExtra("tutorInfo");
        }
        if(getIntent().hasExtra("reviews")){
            reviews = getIntent().getParcelableArrayListExtra("reviews");
        }
        reviewList = (ListView) findViewById(R.id.reviewList);
        reviewAdapter rA = new reviewAdapter(reviews);
        reviewList.setAdapter(rA);

        TextView back = (TextView) view.findViewById(R.id.action_bar_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AllReviewsActivity.this, SelectedTutorActivity.class);

                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                i.putExtra("selectedTutor",tutorInfo);
                i.putExtra("tutorScore",score);

                startActivity(i);
            }
        });


    }

    private class reviewAdapter extends ArrayAdapter<Review> {

        public reviewAdapter(ArrayList<Review> reviews){
            super(AllReviewsActivity.this, R.layout.reviewitem, reviews);
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
            final Review currentRev = reviews.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.revItemName);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView review = (TextView) itemView.findViewById(R.id.reviewText);
            TextView rating = (TextView) itemView.findViewById(R.id.textView17);

            name.setText(currentRev.getAuthor());

            review.setText(currentRev.getText());

            rating.setText(""+currentRev.getRating());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentRev.getTimeStamp());
            date.setText(dateFormat.format(calendar.getTime()));

            return itemView;
            // return super.getView(position, convertView, parent);
        }




    }
}
