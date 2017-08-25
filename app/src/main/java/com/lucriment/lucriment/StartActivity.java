package com.lucriment.lucriment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.rd.PageIndicatorView;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewFlipper viewFlipper;
    private Animation in, out, inR, outR;
    private  PageIndicatorView pageIndicatorView;
    private Button createAccountButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        createAccountButton = (Button) findViewById(R.id.createAccount);
        loginButton = (Button) findViewById(R.id.logInButton);
        pageIndicatorView.setCount(5);
        pageIndicatorView.setVisibility(View.VISIBLE);
        pageIndicatorView.setAutoVisibility(true);
        pageIndicatorView.setRadius(5);
        pageIndicatorView.setSelection(0);

        createAccountButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        in = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        out = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);

        inR = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
        outR = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);

        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
       // viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        viewFlipper.setOnTouchListener(new OnSwipeTouchListener(StartActivity.this){
            public void onSwipeTop() {
                //Toast.makeText(StartActivity.this,"swiped right",Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
               // viewFlipper.setFlipInterval(100000);
                viewFlipper.setInAnimation(inR);
                viewFlipper.setOutAnimation(outR);
                viewFlipper.showPrevious();
                viewFlipper.setFlipInterval(9000);
                if(pageIndicatorView.getSelection()>0) {
                    pageIndicatorView.setSelection(pageIndicatorView.getSelection() - 1);
                }else{
                    pageIndicatorView.setSelection(4);
                }

            }
            public void onSwipeLeft() {
                viewFlipper.setInAnimation(in);
                viewFlipper.setOutAnimation(out);
                viewFlipper.showNext();
                viewFlipper.setFlipInterval(9000);
                if(pageIndicatorView.getSelection()<pageIndicatorView.getCount()-1) {
                    pageIndicatorView.setSelection(pageIndicatorView.getSelection() + 1);
                }else{
                    pageIndicatorView.setSelection(0);
                }
            }
            public void onSwipeBottom() {

            }

        });




    }


    @Override
    public void onClick(View v) {
        if(v== loginButton){
            finish();
            Intent y = new Intent(this, LoginActivity.class);
            startActivity(y);
        }

    }
}
