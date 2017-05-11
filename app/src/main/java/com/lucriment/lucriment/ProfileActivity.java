package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView profileName;
    private Button logoutButton;
    private DatabaseReference databaseReference;
    private Button browseButton;
    private Button viewMessagesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // initialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String name = user.getDisplayName();


        // check whether or not user is logged in
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            //String key = databaseReference.getRef();

        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("Students");
                DataSnapshot tutorSnap = dataSnapshot.child("Tutors");
                if( studentSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                } else if(tutorSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                }else{
                    finish();
                    startActivity(new Intent(ProfileActivity.this, CreationActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //FirebaseUser user = firebaseAuth.getCurrentUser();


        profileName = (TextView) findViewById(R.id.profileLabel);
        if(firebaseAuth.getCurrentUser().getDisplayName() == null){
            profileName.setText("Welcome " );
        }else {
            profileName.setText("Welcome " + user.getDisplayName());
        }
        logoutButton = (Button) findViewById(R.id.logoutButton);
        browseButton = (Button)findViewById(R.id.browseButton1);
        viewMessagesButton = (Button) findViewById(R.id.viewMessagesButton);

        viewMessagesButton.setOnClickListener(this);
        browseButton.setOnClickListener(this);

        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.logoutButton:
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.browseButton1:
                finish();
                startActivity(new Intent(this, TutorListActivity.class));
                break;
            case  R.id.viewMessagesButton:
                finish();
                startActivity(new Intent(this, ViewMessagesActivity.class));
                break;


        }

    }
}