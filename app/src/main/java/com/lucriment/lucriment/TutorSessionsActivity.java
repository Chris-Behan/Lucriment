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

public class TutorSessionsActivity extends BaseActivity {
    private UserInfo userInfo;
    private String userType;
    private TutorInfo tutorInfo;
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    private ArrayList<TimeInterval> defaults = new ArrayList<>();
    private Map<String, ArrayList<TimeInterval>> td;
    HashMap<String, ArrayList<String>> defaultAvailability;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);
        this.getSupportActionBar().setElevation(0);

        //INITIALIZE BOTTOM NAVIGATION BAR


        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        getSupportActionBar().setTitle("Sessions");
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
        if(userType!=null) {
            if (userType.equals("tutor")) {
                return R.id.tutorSessions;
            } else {
                return R.id.sessions;
            }
        }else{
            return R.id.tutorSessions;
        }
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
