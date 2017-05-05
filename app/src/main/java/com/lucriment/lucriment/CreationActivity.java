package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        System.out.println(school);
        UserInfo UserInformation = new UserInfo(accountType,school);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getDisplayName()).setValue(UserInformation);
        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();

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
