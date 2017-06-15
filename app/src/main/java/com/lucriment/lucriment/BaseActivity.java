package com.lucriment.lucriment;

import android.annotation.SuppressLint;
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
        this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            int itemId = item.getItemId();
            if (itemId == R.id.search) {
                startActivity(new Intent(this, TutorListActivity.class));
                //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        if (itemId == R.id.profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }

        if (itemId == R.id.sessions) {
            Intent y = new Intent(this, SessionsActivity.class);
            y.putExtra("userType", getUserType());
            startActivity(y);
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        if (itemId == R.id.inbox) {
            startActivity(new Intent(this, ViewMessagesActivity.class));
            //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }


            finish();

        return true;

    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

    abstract String getUserType();

    abstract UserInfo getUserInformation();

}