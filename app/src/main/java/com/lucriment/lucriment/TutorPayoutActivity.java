package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TutorPayoutActivity extends AppCompatActivity {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference databaseReference;
    private TextView accountNumber, transitNumber, institutionNumber;
    private String acctLast4, routingNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_payout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payout Info");

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        accountNumber = (TextView) findViewById(R.id.accountNumber);
        transitNumber = (TextView) findViewById(R.id.transitNumber);
        institutionNumber = (TextView) findViewById(R.id.institutionNumber);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("stripe_connected")
                .child("external_accounts").child("data").child("0");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    acctLast4 = dataSnapshot.child("last4").getValue(String.class);
                    routingNum = dataSnapshot.child("routing_number").getValue(String.class);

                   accountNumber.setText("********"+acctLast4);
                    transitNumber.setText(routingNum.substring(0,routingNum.indexOf('-')));
                    institutionNumber.setText(routingNum.substring(routingNum.indexOf('-')+1,routingNum.length()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(TutorPayoutActivity.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }
}
