package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SelectedTutorActivity extends AppCompatActivity implements View.OnClickListener {
    private TutorInfo selectedTutor;
    private TextView tutorName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private Button contactButton;
    private TextView educationField;
    private TextView bioField;
    private TextView rateField;
    private TextView classesField;
    private ImageView imageView;
    private StorageReference storageReference;
    private DatabaseReference userRoot = FirebaseDatabase.getInstance().getReference().child("Students");
    private String bio;
    private UserInfo userInfo;
    private String myChats;
    private String myChats2;
    private List<UserInfo> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tutor);
        selectedTutor = getIntent().getParcelableExtra("selectedTutor");
        storageReference = FirebaseStorage.getInstance().getReference();

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<String> set = new HashSet<String>();
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                        .iterator();
                users = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    UserInfo user = dataSnapshotChild.getValue(UserInfo.class);
                    if(user.getUid().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                   myChats= user.getMyChats();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.tutorName);
        backButton = (Button) findViewById(R.id.backButton);
        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorBioField);
        rateField = (TextView) findViewById(R.id.tutorRateField);
        contactButton = (Button) findViewById(R.id.contactButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        classesField = (TextView) findViewById(R.id.classesField);

       // selectedTutor = TutorListActivity.getTutor();
        tutorName.setText(selectedTutor.getName());
        educationField.setText(selectedTutor.getEducation());
        bioField.setText(selectedTutor.getBio());
        rateField.setText(String.valueOf(selectedTutor.getRate()));
        classesField.setText(selectedTutor.getClasses());
        backButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);

        StorageReference pathReference = storageReference.child("ProfilePics").child(selectedTutor.getID());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(SelectedTutorActivity.this).load(uri).fit().centerCrop().into(imageView);
            }
        });
        //setup buttons and fields
      //  tutorName.setText();

    }

    @Override
    public void onClick(View v) {

        if(v == backButton){
            finish();
            startActivity(new Intent(SelectedTutorActivity.this, TutorListActivity.class));
        }
        if(v == contactButton){
            DatabaseReference chatRoot = FirebaseDatabase.getInstance().getReference().child("Students").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            myChats = (myChats + selectedTutor.getID().toString());
            chatRoot.child("myChats").setValue(myChats);
            DatabaseReference chatRoot2 = FirebaseDatabase.getInstance().getReference().child("Students").child(selectedTutor.getID());
            myChats2 = (myChats2 + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
            chatRoot2.child("myChats").setValue(myChats2);
            Intent i = new Intent(SelectedTutorActivity.this, ViewMessagesActivity.class);
            i.putExtra("tutorID", selectedTutor.getID());

            startActivity(i);
        }
    }
}
