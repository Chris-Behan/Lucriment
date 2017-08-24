package com.lucriment.lucriment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
       // BottomNavHelper.disableShiftMode(navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
        updateNavigationBarState();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  BottomNavHelper.disableShiftMode(navigationView);
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        BottomNavHelper.disableShiftMode(navigationView);
        this.overridePendingTransition(0,R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



            int itemId = item.getItemId();
            if (itemId == R.id.search) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                Intent y = new Intent(this, TutorListActivity.class);
                y.putExtra("userType", getUserType());
                y.putExtra("userInfo",getUserInformation());
                startActivity(y);
            }
        if (itemId == R.id.profile) {
            Intent y = new Intent(this, SettingsActivity.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
        }

        if (itemId == R.id.sessions) {
            Intent y = new Intent(this, TutorSessionsActivity.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        if (itemId == R.id.inbox) {
            Intent y = new Intent(this, ViewMessagesActivity.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
        }
        if(itemId == R.id.favourites){
            if(getUserType().equals("tutor")){
                finish();
                Intent i = new Intent(this, DefaultAvailability.class);
                i.putExtra("userType", getUserType());
                i.putExtra("userInfo", getUserInformation());
                startActivity(i);
            }else {
                Intent y = new Intent(this, Favourites.class);
                y.putExtra("userType", getUserType());
                y.putExtra("userInfo", getUserInformation());
                startActivity(y);
            }

        }

        if (itemId == R.id.tutorSessions) {
            Intent y = new Intent(this, TutorSessionsActivity.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }

        if (itemId == R.id.tutorAvailability) {
            Intent y = new Intent(this, DefaultAvailability.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        if (itemId == R.id.stats) {
            Intent y = new Intent(this, StatsActivity.class);
            y.putExtra("userType", getUserType());
            y.putExtra("userInfo",getUserInformation());
            startActivity(y);
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }


            finish();

        return true;

    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    private void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
           item.setChecked(false);
        }
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

    abstract String getUserType();

    abstract UserInfo getUserInformation();

}