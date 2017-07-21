package com.lucriment.lucriment;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
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
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private TwoItemField option1 = new TwoItemField("Availability","");
    private TwoItemField option2 = new TwoItemField("Payment","");
    private TwoItemField option3 = new TwoItemField("Become a Tutor","");
    private TwoItemField option4 = new TwoItemField("Share","");

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

        //INITIALIZE BOTTOM NAVIGATION VIEW
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
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

                    .into(imageView);
        }

        // SET LISTENERS
        profileButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        //FILL OPTIONS
        optionList.add(option1);
        optionList.add(option2);
        if(userType.equals("student")) {
            optionList.add(option3);
        }
        optionList.add(option4);
        populateOptionList();
        registerOptionClicks();


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
                        Intent i = new Intent(SettingsActivity.this, DefaultAvailability.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if (position == 1) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, PaymentActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if (position == 2) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, TutorCreation.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    /*
                    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("charges");
                    HashMap<String,Integer> amountMap = new HashMap<String, Integer>();
                            amountMap.put("amount",5500);
                            dbr.push().setValue(amountMap); */
                    }
                }else{
                    if (position == 0) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, DefaultAvailability.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
                        startActivity(i);
                    }
                    if (position == 1) {
                        finish();
                        Intent i = new Intent(SettingsActivity.this, PaymentActivity.class);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo", userInfo);
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
