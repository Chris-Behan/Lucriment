package com.lucriment.lucriment;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.Stripe;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashMap;

public class TutorCreationP2 extends AppCompatActivity {

    private int year, month, day;
    private boolean pushed = false;
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference myRef, baseRef;
    private String address, city, province,postalCode;
    private String accountNumber, transitNumber, institutionNumber,SIN;
    private EditText accountNumField, transitNumField, institutionNumField, SINfield;
    private Button addAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_creation_p2);

        if(getIntent().hasExtra("day")){
            day = getIntent().getIntExtra("day",0);
            month = getIntent().getIntExtra("month",0);
            year = getIntent().getIntExtra("year",0);
        }
        if(getIntent().hasExtra("city")){
            address = getIntent().getStringExtra("address");
            city = getIntent().getStringExtra("city");
            province = getIntent().getStringExtra("province");
            postalCode = getIntent().getStringExtra("postalCode");
        }

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        //  INITIALIZE WIDGETS
        accountNumField = (EditText) findViewById(R.id.accountNumber);
        transitNumField = (EditText) findViewById(R.id.transitNumber);
        institutionNumField = (EditText) findViewById(R.id.institutionNumber);
        addAccountButton = (Button) findViewById(R.id.addAccountButton);
        SINfield = (EditText) findViewById(R.id.SIN);

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(accountNumField.getText().toString())){
                    Toast.makeText(TutorCreationP2.this,"Please enter account number",Toast.LENGTH_SHORT).show();
                    accountNumField.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(transitNumField.getText().toString())){
                    Toast.makeText(TutorCreationP2.this,"Please enter transit number",Toast.LENGTH_SHORT).show();
                    transitNumField.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(institutionNumField.getText().toString())){
                    Toast.makeText(TutorCreationP2.this,"Please enter institution number",Toast.LENGTH_SHORT).show();
                    institutionNumField.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(SINfield.getText().toString())){
                    Toast.makeText(TutorCreationP2.this,"Please enter SIN number",Toast.LENGTH_SHORT).show();
                    SINfield.requestFocus();
                    return;
                }

                accountNumber = accountNumField.getText().toString();
                transitNumber = transitNumField.getText().toString();
                institutionNumber = institutionNumField.getText().toString();
                SIN = SINfield.getText().toString();
                initializeBankAccount();
                finish();
                Intent y =new Intent(TutorCreationP2.this, SettingsActivity.class);
                y.putExtra("userType",userType);
                y.putExtra("userInfo",userInfo);
                startActivity(y);

            }
        });


        baseRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("stripe_connected").child("update");


        if(year!=0) {
            DatabaseReference additionalInfoRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId());

            additionalInfoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("stripe_connected")) {
                        if(!pushed) {
                            HashMap<String,Object> entireMap = new HashMap<String, Object>();
                            HashMap<String, Object> dobMap = new HashMap<String, Object>();
                            HashMap<String,Object> legalMap = new HashMap<String, Object>();
                            HashMap<String,Object> addressMap = new HashMap<String, Object>();
                            addressMap.put("city",city);
                            addressMap.put("line1",address);
                            addressMap.put("state",province);
                            addressMap.put("postal_code",postalCode);
                            legalMap.put("address",addressMap);
                            dobMap.put("day",day);
                            dobMap.put("month",month);
                            dobMap.put("year",year);
                            legalMap.put("dob",dobMap);
                            legalMap.put("first_name",userInfo.getFirstName());
                            legalMap.put("last_name",userInfo.getLastName());
                            legalMap.put("type","individual");
                           // legalMap.put("personal_id_number","123456789");
                            entireMap.put("legal_entity",legalMap);


                            Calendar cal = Calendar.getInstance();
                            long date = cal.getTimeInMillis()/1000;
                            WifiManager wm = (WifiManager) TutorCreationP2.this.getApplicationContext().getSystemService(WIFI_SERVICE);
                            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                            HashMap<String,Object> tosMap = new HashMap<String, Object>();
                            tosMap.put("ip",ip);
                            tosMap.put("date",date);
                            entireMap.put("tos_acceptance",tosMap);

                            baseRef.setValue(entireMap);

                            pushed = true;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void initializeBankAccount(){
        HashMap<String,Object> bankAcct = new HashMap<String, Object>();
        bankAcct.put("object","bank_account");
        bankAcct.put("account_number",accountNumber);
        bankAcct.put("country","CA");
        bankAcct.put("currency","CAD");
        bankAcct.put("routing_number",transitNumber+"-"+institutionNumber);
        baseRef.child("external_account").setValue(bankAcct);
        baseRef.child("legal_entity").child("personal_id_number").setValue(SIN);
        Toast.makeText(TutorCreationP2.this,"Bank account added.",Toast.LENGTH_SHORT).show();

    }
}
