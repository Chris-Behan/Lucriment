package com.lucriment.lucriment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private Button profileButton, logoutButton;
    private String userType;
    private UserInfo userInfo;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView;
    private ArrayList<TwoItemField> optionList = new ArrayList<>();
    private ArrayAdapter<TwoItemField> adapter;
    private TwoItemField option1 = new TwoItemField("Notifications","");
    private TwoItemField option2 = new TwoItemField("Payout","");
    private TwoItemField option3 = new TwoItemField("Switch to Student","");
    private TwoItemField option4 = new TwoItemField("Share","");
    private TwoItemField sOption1 = new TwoItemField("Notifications","");
    private TwoItemField sOption2 = new TwoItemField("Payment","");
    private TwoItemField sOption3 = new TwoItemField("Switch to Tutor","");
    private TwoItemField sOption4 = new TwoItemField("Share","");
    private TwoItemField option5 = new TwoItemField("Disputes","");
    private TwoItemField option6 = new TwoItemField("About This App","");

    private boolean isTutor = false;
    private DatabaseReference myRef, baseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }


        firebaseAuth = FirebaseAuth.getInstance();
        baseRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("stripe_connected").child("update");
        myRef = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("userType");
        DatabaseReference testRefund = FirebaseDatabase.getInstance().getReference().child("users").child("qrD21Pv02RhjQlGPDdxGyBHMoVr2")
                .child("refunds");
        HashMap<String,String> refundMap = new HashMap<>();
        refundMap.put("chargeId","ch_1B1NrAGQFWOITl4suXjiUW8V");
        testRefund.push().setValue(refundMap);
        DatabaseReference checkT = FirebaseDatabase.getInstance().getReference().child("tutors");
        checkT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userInfo.getId())){
                    isTutor = true;
                }

                if(userType.equals("tutor")) {

                    optionList.add(option2);
                    optionList.add(option3);
                    optionList.add(option4);
                    optionList.add(option6);

                    populateOptionList();
                    registerOptionClicks();
                }else{

                    optionList.add(sOption2);
                    if(isTutor){

                    }else {
                        sOption3.setLabel("Become a Tutor");
                    }
                    optionList.add(sOption3);
                    optionList.add(sOption4);
                    optionList.add(option5);
                    optionList.add(option6);
                    populateOptionList();
                    registerOptionClicks();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //INITIALIZE BOTTOM NAVIGATION VIEW
        if(userType.equals("tutor")) {
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else{
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation2);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);

        //INITIALIZE BUTTONS
        profileButton = (Button) findViewById(R.id.profileButton);
        logoutButton = (Button) findViewById(R.id.logout);
        if(userInfo.getProfileImage()!=null) {
            imageView = (ImageView) findViewById(R.id.settingsPic);
            Glide.with(getApplicationContext())
                    .load(userInfo.getProfileImage())
                    .apply(RequestOptions.circleCropTransform())

                    .into(imageView);
        }

        // SET LISTENERS
        profileButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        //FILL OPTIONS



    }


    private void populateOptionList(){
        adapter = new SettingsActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.options);
        list.setAdapter(adapter);


    }

    private void registerOptionClicks(){
        ListView list = (ListView) findViewById(R.id.options);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(userType.equals("student")) {

                    if (position == 0) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, PaymentActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if (position == 1) {
                        finish();
                        if(!isTutor) {
                            Intent i = new Intent(SettingsActivity.this, TutorCreation.class);
                            i.putExtra("userType", userType);
                            i.putExtra("userInfo", userInfo);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(SettingsActivity.this, TutorSessionsActivity.class);
                            userInfo.setUserType("tutor");
                            userType = "tutor";
                            myRef.setValue(userType);
                            i.putExtra("userType", userType);
                            i.putExtra("userInfo", userInfo);
                            startActivity(i);
                        }
                    /*
                    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("charges");
                    HashMap<String,Integer> amountMap = new HashMap<String, Integer>();
                            amountMap.put("amount",5500);
                            dbr.push().setValue(amountMap); */
                    }
                    if(position ==2){
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareSub = "Lucriment, an Online Marketplace for Tutors";
                        String shareBody = "Download now: www.lucriment.com";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                    if(position ==3){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                SettingsActivity.this);

                        // set title
                        alertDialogBuilder.setTitle("Disputes");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("If you have a dispute regarding a session, email us at disputes@lucriment.com" +
                                        " with a detailed message as to why you deserve a refund and we will get back to you " +
                                        "immediately")
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.dismiss();
                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                    if(position==4){
                        Intent i = new Intent(SettingsActivity.this, AppInfoActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo",userInfo);
                        startActivity(i);
                    }


                }else{

                    if (position == 0) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, TutorPayoutActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if(position == 1){
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(userInfo.getId()).child("userType");
                        myRef.setValue("student");
                        Intent i = new Intent(SettingsActivity.this, TutorListActivity.class);
                        userInfo.setUserType("student");
                        userType = "student";
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if(position ==2){
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareSub = "Lucriment, an Online Marketplace for Tutors";
                        String shareBody = "Download now: www.lucriment.com";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                    if(position ==3){
                        Intent i = new Intent(SettingsActivity.this, AppInfoActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo",userInfo);
                        startActivity(i);
                    }

                }


            }
        });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }

    @Override
    public void onClick(View v) {
        if(v == profileButton){
            if(userType.equals("student")) {
                Intent i = new Intent(SettingsActivity.this, studentProfileActivity.class);
                i.putExtra("userType", userType);
                i.putExtra("userInfo", userInfo);
                startActivity(i);
            }else{
                Intent i = new Intent(SettingsActivity.this, MyProfileActivity.class);
                i.putExtra("userType", userType);
                i.putExtra("userInfo", userInfo);
                startActivity(i);

            }
        }
        if(v == logoutButton){
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(SettingsActivity.this, R.layout.session_request_field, optionList);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.session_request_field, parent, false);
            }
            TwoItemField currentTwo = optionList.get(position);
            //Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView category = (TextView) itemView.findViewById(R.id.category);
            category.setText(currentTwo.getLabel());


            TextView dataText = (TextView) itemView.findViewById(R.id.input);
            dataText.setText(currentTwo.getData());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
}
