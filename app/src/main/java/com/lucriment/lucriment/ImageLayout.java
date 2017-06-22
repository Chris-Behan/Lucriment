package com.lucriment.lucriment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ImageLayout extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder> mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_layout);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = (RecyclerView)findViewById(R.id.rView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageLayout.this));
        Toast.makeText(ImageLayout.this, "Wait ! Fetching List...", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStart() {
        super.onStart();

//Log.d("LOGGED", "IN onStart ");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder>
                (TutorInfo.class, R.layout.tutor_profile_layout, ImageLayoutViewHolder.class, myRef)
        {

            public void populateViewHolder(final ImageLayoutViewHolder viewHolder, TutorInfo model, final int position) {
                viewHolder.Image_URL(model.getProfileImage());
                viewHolder.Image_Title(model.getFirstName());


//OnClick Item it will Delete data from Database
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageLayout.this);
                        builder.setMessage("Do you want to Delete this data ?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = position;
                                        mFirebaseAdapter.getRef(selectedItems).removeValue();
                                        mFirebaseAdapter.notifyItemRemoved(selectedItems);
                                        recyclerView.invalidate();
                                        onStart();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Confirm");
                        dialog.show();
                    }
                });


            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);
    }

    //View Holder For Recycler View
    public static class ImageLayoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView image_title;
        private final ImageView image_url;



        public ImageLayoutViewHolder(final View itemView) {
            super(itemView);
            image_url = (ImageView) itemView.findViewById(R.id.ProfileImage);
            image_title = (TextView) itemView.findViewById(R.id.browseDisplayName);


        }

        private void Image_Title(String title) {
            image_title.setText(title);
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