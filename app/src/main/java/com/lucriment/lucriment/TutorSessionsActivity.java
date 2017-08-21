package com.lucriment.lucriment;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorSessionsActivity extends BaseActivity {
    private UserInfo userInfo;
    private String userType;
    private TutorInfo tutorInfo;
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    private ArrayList<TimeInterval> defaults = new ArrayList<>();
    private Map<String, ArrayList<TimeInterval>> td;
    HashMap<String, ArrayList<String>> defaultAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //INITIALIZE BOTTOM NAVIGATION BAR


        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUpViewPager(viewPager);

    }

    //ADDS FRAGMENTS TO VIEW
    private void setUpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        PastSessionFragment psf = new PastSessionFragment();
        UpcomingSessionFragment USF = new UpcomingSessionFragment();
        Bundle args = new Bundle();
        args.putParcelable("userInfo",userInfo);
        args.putString("userType",userType);
        USF.setArguments(args);
        psf.setArguments(args);

        adapter.addFragment(USF, "Upcoming");
        adapter.addFragment(psf,"Past");



        viewPager.setAdapter(adapter);

    }
    @Override
    int getContentViewId() {
        return R.layout.activity_tutor_sessions;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.search;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }
}
