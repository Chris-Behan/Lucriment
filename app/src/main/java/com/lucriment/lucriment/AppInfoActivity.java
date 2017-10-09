package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AppInfoActivity extends AppCompatActivity {
    private UserInfo userInfo;
    private String userType;
    private TextView TOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("App Info");
        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        TOS = (TextView) findViewById(R.id.aboutTOS);

        SpannableString ss = new SpannableString("By using this application, you agree to our Terms of Service and Privacy Policy.");
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String url = "https://lucriment.com/tos.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String url = "https://lucriment.com/privacy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        ss.setSpan(span1, 44, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, 65, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TOS.setText(ss);
        TOS.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        Intent i = new Intent(AppInfoActivity.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }
}
