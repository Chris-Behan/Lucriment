package Students;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import Misc.BottomNavHelper;
import com.lucriment.lucriment.LoginActivity;
import com.lucriment.lucriment.R;
import Sessions.SessionsActivity;
import Messaging.ViewMessagesActivity;

import Tutors.PersonalProfileActivity;
import Tutors.ScheduleActivity;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView profileName;
    private Button logoutButton;
    private DatabaseReference databaseReference;
    private Button browseButton;
    private Button viewMessagesButton;
    private Button viewProfileButton;
    private UserInfo userInfo;
    private Button ScheduleButton;
    private Button SessionsButton;
    private String userType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Animation animFadein;
        Animation animFadeout;
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animFadeout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        // initialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
      //  selectBottomNavigationBarItem(getNavigationMenuItemId());
       // bottomNavigationView.setSelected(true);



      // databaseReference.child("subjects").setValue("");
        String name = user.getDisplayName();
        if(getIntent().hasExtra("userInfo"))
        userInfo = getIntent().getParcelableExtra("userInfo");

        // check whether or not user is logged in
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            //String key = databaseReference.getRef();

        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("users");
                DataSnapshot tutorSnap = dataSnapshot.child("tutors");
                FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();

                if( studentSnap.hasChild(thisUser.getUid())){
                    for(DataSnapshot userSnapShot: studentSnap.getChildren()){
                        if(userSnapShot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                            userInfo = userSnapShot.getValue(UserInfo.class);
                            userType = userInfo.getUserType();
                        }
                    }
                } else if(tutorSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userType = userInfo.getUserType();
                }else{
                    finish();
                    startActivity(new Intent(ProfileActivity.this, CreationActivity.class));
                }
               // finish();
             //   startActivity(new Intent(ProfileActivity.this, TutorListActivity.class));
               // initializeButtons();
                Intent y = new Intent(ProfileActivity.this, TutorListActivity.class);
                y.putExtra("userType", userType);
                y.putExtra("userInfo",userInfo);
                startActivity(y);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    private void initializeButtons(){
        profileName = (TextView) findViewById(R.id.profileLabel);
        if(firebaseAuth.getCurrentUser().getDisplayName() == null){
            profileName.setText("Welcome " );
        }else {
            profileName.setText("Welcome " + userInfo.getFirstName());
        }
        logoutButton = (Button) findViewById(R.id.logoutButton);
        browseButton = (Button)findViewById(R.id.browseButton1);
        viewMessagesButton = (Button) findViewById(R.id.viewMessagesButton);
        viewProfileButton = (Button) findViewById(R.id.viewProfile);
        ScheduleButton = (Button) findViewById(R.id.scheduleButton);
        SessionsButton = (Button) findViewById(R.id.sessionsButton);

        SessionsButton.setOnClickListener(this);
        ScheduleButton.setOnClickListener(this);
        viewMessagesButton.setOnClickListener(this);
        browseButton.setOnClickListener(this);
        viewProfileButton.setOnClickListener(this);

        logoutButton.setOnClickListener(this);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_profile;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
        return userInfo;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.logoutButton:
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.browseButton1:
                finish();
                startActivity(new Intent(this, TutorListActivity.class));
                break;
            case  R.id.viewMessagesButton:
                finish();
                startActivity(new Intent(this, ViewMessagesActivity.class));
                break;
            case R.id.viewProfile:
                Intent i = new Intent(ProfileActivity.this, PersonalProfileActivity.class);
                i.putExtra("userInfo", userInfo);
                startActivity(i);
                break;
            case R.id.scheduleButton:
                 finish();
                startActivity(new Intent(this, ScheduleActivity.class));
                break;
            case R.id.sessionsButton:
                Intent y = new Intent(ProfileActivity.this, SessionsActivity.class);
                y.putExtra("userType", userType);
                startActivity(y);

                break;

        }

    }


}