package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

    private Button registerButton;
    private Spinner schoolSelector;
    private UserInfo UserInformation;
    private TextInputLayout firstNameInputLayout, lastNameInputLayout;

    //private FirebaseUser user;
    private EditText firstName, lastName;


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
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String accountType = "student";

        //initialize buttons
        firstNameInputLayout = (TextInputLayout) findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = (TextInputLayout) findViewById(R.id.lastNameInputLayout);
        registerButton = (Button) findViewById(R.id.createButton);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        String test = firebaseAuth.getCurrentUser().getDisplayName().toString();
        if(firebaseAuth.getCurrentUser().getDisplayName().toString().length() < 1){
            firstName.setVisibility(View.VISIBLE);
            lastName.setVisibility(View.VISIBLE);
        }

        String displayName = firebaseAuth.getCurrentUser().getDisplayName();
        if(firstName.getVisibility() == View.INVISIBLE){
            displayName = firebaseAuth.getCurrentUser().getDisplayName();
            UserInformation = new UserInfo(displayName, displayName.substring(displayName.indexOf(' ')+1,displayName.length()),
                    displayName.substring(0,displayName.indexOf(' ')),user.getUid(),user.getEmail(),accountType);
            databaseReference.child("users").child(user.getUid()).updateChildren(UserInformation.toMap());
            finish();
            startActivity(new Intent(getApplicationContext(), TutorListActivity.class));
        }


        //initialize database
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // set up listeners

        registerButton.setOnClickListener(this);



    }
    //save user information
    private void saveUserInformation(){
        String accountType = "student";

        //String school = "UofC";
        // System.out.println(school);

        String email = firebaseAuth.getCurrentUser().getEmail();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.getDisplayName() == null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName.getText().toString()+" "+lastName.getText().toString())

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
        String displayName = firstName.getText().toString()+" "+lastName.getText().toString();
        if(firstName.getVisibility() == View.INVISIBLE){
            displayName = firebaseAuth.getCurrentUser().getDisplayName();
            UserInformation = new UserInfo(displayName, displayName.substring(displayName.indexOf(' ')+1,displayName.length()),
                    displayName.substring(0,displayName.indexOf(' ')),user.getUid(),user.getEmail(),accountType);
        }else{
            UserInformation = new UserInfo(displayName, lastName.getText().toString(),
                   firstName.getText().toString(),user.getUid(),user.getEmail(),accountType);
        }

       /* if(accountType.equals("Tutor")) {
            databaseReference.child("users").child(user.getUid()).setValue(UserInformation);
            Intent i = new Intent(CreationActivity.this, TutorCreation.class);
            i.putExtra("userInfo", UserInformation);
            startActivity(i);
           // databaseReference.child("Tutors").child(user.getUid()).setValue(UserInformation);
        }else{ */
            databaseReference.child("users").child(user.getUid()).updateChildren(UserInformation.toMap());
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CreationActivity.this, SettingsActivity.class);
            i.putExtra("userInfo", UserInformation);

            startActivity(i);

    }
    private boolean validateFirstName(){
        if (firstName.getText().toString().trim().isEmpty()) {
            firstNameInputLayout.setError(getString(R.string.err_msg_firstname));
            requestFocus(firstName);
            return false;

        } else {
            firstNameInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName(){
        if (lastName.getText().toString().trim().isEmpty()) {
            lastNameInputLayout.setError(getString(R.string.err_msg_lastname));
            requestFocus(lastName);
            return false;

        } else {
            lastNameInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void submitForm() {
        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }


        saveUserInformation();
        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {


        if(v == registerButton){
        submitForm();

        }


    }
}