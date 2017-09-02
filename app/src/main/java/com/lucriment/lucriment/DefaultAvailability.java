package com.lucriment.lucriment;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DefaultAvailability extends BaseActivity {

    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    private String userType;
    private UserInfo userInfo;
    private ArrayList<TimeInterval> defaults = new ArrayList<>();
    private Map<String, ArrayList<TimeInterval>> td;
    HashMap<String, ArrayList<String>> defaultAvailability;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_availability);
        getSupportActionBar().setTitle("Availability");
        this.getSupportActionBar().setElevation(0);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
        //INITIALIZE BOTTOM NAVIGATION BAR


        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tutors").child(userInfo.getId()).child("defaultAvailability").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                defaultAvailability = (HashMap<String, ArrayList<String>>) dataSnapshot.getValue();
                setUpViewPager(viewPager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.WHITE,Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    int getContentViewId() {
        return R.layout.activity_default_availability;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.tutorAvailability;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(DefaultAvailability.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;

    }


    //ADDS FRAGMENTS TO VIEW
    private void setUpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        DefaultTabFragment dft = new DefaultTabFragment();
        CalendarTabFragment ctf = new CalendarTabFragment();
        Bundle args = new Bundle();
        args.putParcelable("userInfo",userInfo);
        args.putString("userType",userType);
        ctf.setArguments(args);
        dft.setArguments(args);
        adapter.addFragment(ctf, "Calendar");
        adapter.addFragment(dft,"Default Availability");
        viewPager.setAdapter(adapter);

    }
}
