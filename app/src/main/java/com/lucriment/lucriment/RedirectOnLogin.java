package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class RedirectOnLogin extends AppCompatActivity {
    private UserInfo userInfo;
    private String userType;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_on_login);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();

        //Initialize progress dialog
        progressDialog = new ProgressDialog(RedirectOnLogin.this);
        progressDialog.setMessage("Connecting..");
        progressDialog.show();


        //Initialize database listener to check account type and status

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(thisUser.getUid());
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        databaseReference.child("deviceToken").setValue(deviceToken);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if (dataSnapshot.hasChild("firstName")) {

                    userInfo = dataSnapshot.getValue(UserInfo.class);
                    userType = userInfo.getUserType();
                    if(userInfo.getPaymentInfo()!=null){
                        userInfo.setHasPamyent(true);
                    }
                    if(userType.equals("student")){
                        Intent y = new Intent(RedirectOnLogin.this, TutorListActivity.class);
                        y.putExtra("userType", userType);
                        y.putExtra("userInfo",userInfo);
                        startActivity(y);
                    }
                    else if(userType.equals("tutor")){
                        Intent y = new Intent(RedirectOnLogin.this, TutorSessionsActivity.class);
                        y.putExtra("userType", userType);
                        y.putExtra("userInfo",userInfo);
                        startActivity(y);
                    }



                }  else {
                    finish();
                    startActivity(new Intent(RedirectOnLogin.this, CreationActivity.class));
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
