package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TutorSessionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);
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
        return null;
    }

    @Override
    UserInfo getUserInformation() {
        return null;
    }
}
