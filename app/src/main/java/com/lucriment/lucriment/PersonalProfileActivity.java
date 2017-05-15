package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView personalName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private Button editButton;
    private TextView educationField;
    private TextView bioField;
    private boolean editing;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private UserInfo userInfo;
    private EditText editBioText;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final String currentKey = firebaseAuth.getCurrentUser().getUid();
        if(userInfo == null) {
            if (getIntent().hasExtra("userInfo")) {
                userInfo = getIntent().getParcelableExtra("userInfo");
            }
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("Students");
                for(DataSnapshot userSnapShot: studentSnap.getChildren()){
                    if(userSnapShot.getKey().equals(currentKey)){
                            userInfo = userSnapShot.getValue(UserInfo.class);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // initialize buttons and fields
        personalName = (TextView) findViewById(R.id.tutorName);
        backButton = (Button) findViewById(R.id.backButton);
        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorBioField);
        editBioText = (EditText) findViewById(R.id.editBioField);
        editButton = (Button) findViewById(R.id.editButton);



        personalName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        educationField.setText(userInfo.getSchool());
        bioField.setText(userInfo.bio);




        editButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        //setup buttons and fields
        //  tutorName.setText();

    }


    @Override
    public void onClick(View v) {

        if(v == backButton){
            Intent i = new Intent(PersonalProfileActivity.this, ProfileActivity.class);
            i.putExtra("userInfo", userInfo);

            startActivity(i);

        }
        if(v == editButton){
            if(editing){
                editing = false;
            }else{
                editing = true;
            }
            if(editing) {
                bioField.setVisibility(View.INVISIBLE);
                editBioText.setVisibility(View.VISIBLE);
            }else{
                bioField.setVisibility(View.VISIBLE);
                editBioText.setVisibility(View.INVISIBLE);
                bioField.setText(editBioText.getText());
                userInfo.setBio(editBioText.getText().toString());
                databaseReference.child("Students").child(user.getUid()).setValue(userInfo);
            }

        }

    }
}
