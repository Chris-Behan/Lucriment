package com.lucriment.lucriment;

import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsActivity extends BaseActivity {
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
        setContentView(R.layout.activity_stats);

        getSupportActionBar().setTitle("Stats");
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

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE,Color.WHITE);
        setUpViewPager(viewPager);

    }

    //ADDS FRAGMENTS TO VIEW
    private void setUpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        FeedbackFragment FF = new FeedbackFragment();
        EarningsFragment EF = new EarningsFragment();
        Bundle args = new Bundle();
        args.putParcelable("userInfo",userInfo);
        args.putString("userType",userType);
        EF.setArguments(args);
        FF.setArguments(args);

        adapter.addFragment(EF, "Earnings");
        adapter.addFragment(FF,"Feedback");



        viewPager.setAdapter(adapter);

    }
    @Override
    int getContentViewId() {
        return R.layout.activity_tutor_sessions;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.stats;
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
