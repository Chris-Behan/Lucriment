package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText enterAccountNumber, enterTransitNumber, enterInstNumber;
    private boolean editing = false;
    private DatabaseReference baseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_payout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payout Info");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.about_action_bar);
        View view =getSupportActionBar().getCustomView();
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Profile");
        TextView back = (TextView) view.findViewById(R.id.action_bar_back);
        TextView edit = (TextView) view.findViewById(R.id.edit_about_action_bar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TutorPayoutActivity.this, SettingsActivity.class);

                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                //i.putExtra("tutorInfo",tutorInfo);

                startActivity(i);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editing) {


                    if(TextUtils.isEmpty(enterAccountNumber.getText().toString())){
                        Toast.makeText(TutorPayoutActivity.this,"Please enter account number",Toast.LENGTH_SHORT).show();
                        enterAccountNumber.requestFocus();
                        return;
                    }else if(TextUtils.isEmpty(enterTransitNumber.getText().toString())){
                        Toast.makeText(TutorPayoutActivity.this,"Please enter transit number",Toast.LENGTH_SHORT).show();
                        enterTransitNumber.requestFocus();
                        return;
                    }else if (TextUtils.isEmpty(enterInstNumber.getText().toString())){
                        Toast.makeText(TutorPayoutActivity.this,"Please enter institution number",Toast.LENGTH_SHORT).show();
                        enterInstNumber.requestFocus();
                        return;
                    }
                    initializeBankAccount();
                    accountNumber.setVisibility(View.VISIBLE);
                    transitNumber.setVisibility(View.VISIBLE);
                    institutionNumber.setVisibility(View.VISIBLE);
                    enterAccountNumber.setVisibility(View.INVISIBLE);
                    enterTransitNumber.setVisibility(View.INVISIBLE);
                    enterInstNumber.setVisibility(View.INVISIBLE);
                    editing = false;

                }else {
                    accountNumber.setVisibility(View.INVISIBLE);
                    transitNumber.setVisibility(View.INVISIBLE);
                    institutionNumber.setVisibility(View.INVISIBLE);
                    enterAccountNumber.setVisibility(View.VISIBLE);
                    enterTransitNumber.setVisibility(View.VISIBLE);
                    enterInstNumber.setVisibility(View.VISIBLE);
                    editing = true;
                }
            }
        });
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        enterAccountNumber = (EditText) findViewById(R.id.enterAccountNumber);
        enterTransitNumber = (EditText) findViewById(R.id.enterTransitNumber);
        enterInstNumber = (EditText) findViewById(R.id.enterInstNumber);
        baseRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("stripe_connected").child("update");
        accountNumber = (TextView) findViewById(R.id.accountNumber);
        transitNumber = (TextView) findViewById(R.id.transitNumber);
        institutionNumber = (TextView) findViewById(R.id.institutionNumber);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("stripe_connected")
                .child("external_accounts").child("data").child("0");
        databaseReference.addValueEventListener(new ValueEventListener() {
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

    private void initializeBankAccount(){
        HashMap<String,Object> bankAcct = new HashMap<String, Object>();
        bankAcct.put("object","bank_account");
        bankAcct.put("account_number",enterAccountNumber.getText().toString().trim());
        bankAcct.put("country","CA");
        bankAcct.put("currency","CAD");
        bankAcct.put("routing_number",enterTransitNumber.getText().toString().trim()+"-"+enterInstNumber.getText().toString().trim());
        baseRef.child("external_account").setValue(bankAcct);

        Toast.makeText(TutorPayoutActivity.this,"Bank account added.",Toast.LENGTH_SHORT).show();

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
