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
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.search:
                finish();
                Intent a =(new Intent(this, TutorListActivity.class));
              //  a.putExtra("userType", userType);
             //   a.putExtra("userInfo", userInfo);
                this.overridePendingTransition(R.anim.fade_in,0);
                break;
            case R.id.sessions:
                Intent y = new Intent(this, SessionsActivity.class);
             //   y.putExtra("userType", userType);
                startActivity(y);
                this.overridePendingTransition(0,0);
                break;
            case R.id.inbox:
                finish();
                startActivity(new Intent(this, ViewMessagesActivity.class));
                this.overridePendingTransition(0,0);
                break;
            case R.id.profile:
                Intent i = new Intent(this, PersonalProfileActivity.class);
            //    i.putExtra("userInfo", userInfo);
                startActivity(i);
                this.overridePendingTransition(0,0);
                break;


        }
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

}