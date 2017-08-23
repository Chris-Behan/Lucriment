package com.lucriment.lucriment;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 8/22/2017.
 */

public class FeedbackFragment extends Fragment {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference earningsRef = FirebaseDatabase.getInstance().getReference().child("sessions");
    private DatabaseReference feedbackRef;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<SessionRequest> mySessions = new ArrayList<>();
    private ListView reviewList;
    private ArrayAdapter<SessionRequest> adapter;
    private ArrayList<SessionRequest> sortedSessions = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_tab, container,false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");
        // earningsList = (ListView) view.findViewById(R.id.earningsList);
        feedbackRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId())
                .child("reviews");

        feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot reviewSnap:dataSnapshot.getChildren()){
                    reviews.add(reviewSnap.getValue(Review.class));
                }
                populateFeedbackList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void populateFeedbackList(){
        if(reviews.isEmpty()){
            Review noReviews = new Review();
            reviews.add(noReviews);
        }
        reviewList = (ListView) getView().findViewById(R.id.reviewList);
        FeedbackFragment.reviewAdapter rA = new FeedbackFragment.reviewAdapter(reviews);
        reviewList.setAdapter(rA);

    }

    private class reviewAdapter extends ArrayAdapter<Review> {

        public reviewAdapter(ArrayList<Review> reviews){
            super(getApplicationContext(), R.layout.reviewitem, reviews);
          //  reviewList = (ListView) findViewById(R.id.reviewList);
          //  AllReviewsActivity.reviewAdapter rA = new AllReviewsActivity.reviewAdapter(reviews);
          //  reviewList.setAdapter(rA);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.reviewitem, parent, false);
            }
            final Review currentRev = reviews.get(position);


            TextView name = (TextView) itemView.findViewById(R.id.revItemName);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView review = (TextView) itemView.findViewById(R.id.reviewText);
            TextView rating = (TextView) itemView.findViewById(R.id.textView17);
            ImageView star = (ImageView) itemView.findViewById(R.id.imageView7);
            if(currentRev.getAuthor()==null){
                name.setText("No Reviews yet");
                review.setText(null);
                date.setText(null);
                rating.setText(null);
                star.setVisibility(View.INVISIBLE);
            }else {

                name.setText(currentRev.getAuthor());

                review.setText(currentRev.getText());

                rating.setText(""+currentRev.getRating());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentRev.getTimeStamp());
                date.setText(dateFormat.format(calendar.getTime()));
            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }

    }

}
