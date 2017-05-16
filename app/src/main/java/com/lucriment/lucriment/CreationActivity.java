package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button logoutButton;
    private Button registerButton;
    private RadioGroup rg;
    private RadioButton rb;
    private Spinner schoolSelector;

    //private FirebaseUser user;
    private EditText name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        //initialize firebaseAuth and check if user is signed in
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // initialize firebase user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //initialize buttons
        logoutButton = (Button) findViewById(R.id.logoutButton2);
        schoolSelector = (Spinner) findViewById(R.id.schoolSelect);
        registerButton = (Button) findViewById(R.id.createButton);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        name = (EditText) findViewById(R.id.Name);
        if(firebaseAuth.getCurrentUser().getDisplayName() == null){
            name.setVisibility(View.VISIBLE);
        }


        //initialize database
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // set up listeners
        logoutButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        //set up drop down menu
        ArrayAdapter<String> schoolNameAdapter = new ArrayAdapter<String>(CreationActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.SchoolNames));
        schoolNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSelector.setAdapter(schoolNameAdapter);

    }
    //save user information
    private void saveUserInformation(){
        String accountType = rb.getText().toString().trim();
        String school = schoolSelector.getSelectedItem().toString().trim();
        //String school = "UofC";
        // System.out.println(school);

        String email = firebaseAuth.getCurrentUser().getEmail();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.getDisplayName() == null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString())

                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
        }
      //  String displayName = firebaseAuth.getCurrentUser().getDisplayName().toString();
        String displayName = name.getText().toString();
        if(name.getVisibility() == View.INVISIBLE){
            displayName = firebaseAuth.getCurrentUser().getDisplayName();
        }
        UserInfo UserInformation = new UserInfo(accountType,school, email, displayName);
        if(accountType.equals("Tutor")) {
            databaseReference.child("Students").child(user.getUid()).setValue(UserInformation);
            finish();
            startActivity(new Intent(CreationActivity.this, TutorCreation.class));
           // databaseReference.child("Tutors").child(user.getUid()).setValue(UserInformation);
        }else{
            databaseReference.child("Students").child(user.getUid()).setValue(UserInformation);
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CreationActivity.this, ProfileActivity.class);
            i.putExtra("userInfo", UserInformation);

            startActivity(i);


        }



    }

    public void rbClick(View v){
        int radioButtonID = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radioButtonID);
    }

    @Override
    public void onClick(View v) {

        if(v == logoutButton){
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v == registerButton){
            saveUserInformation();
        }


    }
}