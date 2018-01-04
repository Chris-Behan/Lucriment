package Sessions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.R;
import Students.Rating;

import Students.UserInfo;

/**
 * Created by ChrisBehan on 8/21/2017.
 */

public class ReviewDialog extends DialogFragment{
    private UserInfo userInfo, receiver;
    private String userType, sessionId, sessionKey;
    private DatabaseReference reviewRef, receiverRating, sessionRef, sessionRef2;
    private Rating rating;
    private float score;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();



        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        receiver = args.getParcelable("receiver");
        userType = args.getString("userType");
        sessionId = args.getString("sessionId");
        sessionKey = args.getString("sessionKey");

        if(userType.equals("tutor")){
            receiverRating = FirebaseDatabase.getInstance().getReference().child("users").child(receiver.getId()).child("rating");

            reviewRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiver.getId()).
                    child("reviews").child(sessionKey);
            sessionRef = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()).child(sessionId)
                    .child(sessionKey).child("studentReview");
            sessionRef2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(receiver.getId()).child(sessionId)
                    .child(sessionKey).child("studentReview");

        }else{
            receiverRating = FirebaseDatabase.getInstance().getReference().child("tutors").child(receiver.getId()).child("rating");
            reviewRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(receiver.getId()).
                    child("reviews").child(sessionKey);
            sessionRef = FirebaseDatabase.getInstance().getReference().child("sessions").child(userInfo.getId()).child(sessionId)
                    .child(sessionKey).child("tutorReview");
            sessionRef2 = FirebaseDatabase.getInstance().getReference().child("sessions").child(receiver.getId()).child(sessionId)
                    .child(sessionKey).child("tutorReview");
        }


        //SET UP DATABASE REFERENCES
        receiverRating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rating = dataSnapshot.getValue(Rating.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        View view = inflater.inflate(R.layout.review_dialog_layout,null);
        //  final CardInputWidget mCardInputWidget = (CardInputWidget) view.findViewById(R.id.card_input_widget);
        final EditText reviewField = (EditText) view.findViewById(R.id.editText);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar2);
        ratingBar.setStepSize(1);
        builder.setView(view)

                // Add action buttons
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        java.util.Calendar cc = java.util.Calendar.getInstance();
                        Dialog dialog1  = (Dialog) dialog;
                        Context context = dialog1.getContext();
                        if(rating!=null) {
                            rating.setNumberOfReviews(rating.getNumberOfReviews() + 1);
                            rating.setTotalScore(rating.getTotalScore() + ratingBar.getRating());
                        }else{
                            rating = new Rating(ratingBar.getRating(),1);
                        }
                        Review review = new Review(userInfo.getFullName(),ratingBar.getRating(),reviewField.getText().toString(),cc.getTimeInMillis(), userInfo.getId());
                        receiverRating.setValue(rating);
                        reviewRef.setValue(review);
                        sessionRef.setValue(review);
                        sessionRef2.setValue(review);



                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ReviewDialog.this.getDialog().cancel();
                    }
                });



        return builder.create();
    }
}
