package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutorCreation extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String displayName;
    private int rating;
    private String classes;
    private String education;
    private String email;
    private String id;
    private double rate;
    private EditText classField;
    private EditText rateField;
    private EditText educationField;
    private Button becomeTutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_creation);

        classField = (EditText) findViewById(R.id.classesField);
        rateField = (EditText) findViewById(R.id.rateField);
        educationField = (EditText) findViewById(R.id.educationField);
        becomeTutor = (Button) findViewById(R.id.becomeTutor);

        becomeTutor.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        if(v == becomeTutor){
            createTutorProfile();
        }
    }

    private void createTutorProfile(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        id = firebaseAuth.getCurrentUser().getUid();
        displayName = firebaseAuth.getCurrentUser().getDisplayName();
        classes = classField.getText().toString();
        rate = Double.parseDouble(rateField.getText().toString());
        education = educationField.getText().toString();
        email = firebaseAuth.getCurrentUser().getEmail();
        TutorInfo tutorInfo = new TutorInfo(email,displayName,education,classes,rate, id);
        databaseReference.child("Tutors").child(user.getUid()).setValue(tutorInfo);
        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(TutorCreation.this, ProfileActivity.class));

    }
}
