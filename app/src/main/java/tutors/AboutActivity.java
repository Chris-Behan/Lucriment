package tutors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucriment.lucriment.R;
import students.UserInfo;

public class AboutActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private TutorInfo tutorInfo;
    private String userType;

    private TextView aboutView;
    private EditText editAbout;
    private boolean editing = false;

    private DatabaseReference aboutRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.about_action_bar);
        View view =getSupportActionBar().getCustomView();


        // GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("tutorInfo")){
            tutorInfo = getIntent().getParcelableExtra("tutorInfo");
        }

        //INITIALIZE DATABASE REFERENCE
        aboutRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId()).child("about");

        //initialize widgets
        aboutView = (TextView) findViewById(R.id.aboutView);
        editAbout = (EditText) findViewById(R.id.editAbout);

        //SET FIELDS
        aboutView.setText(tutorInfo.getAbout());
        editAbout.setText(tutorInfo.getAbout());

        TextView back = (TextView) view.findViewById(R.id.action_bar_back);
        final TextView edit = (TextView) view.findViewById(R.id.edit_about_action_bar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutActivity.this, EditTutorProfile.class);

                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                i.putExtra("tutorInfo",tutorInfo);

                startActivity(i);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editing){
                    editing = false;
                    edit.setText("Edit");
                    aboutView.setText(editAbout.getText().toString().trim());
                    aboutRef.setValue(aboutView.getText().toString().trim());
                    aboutView.setVisibility(View.VISIBLE);
                    editAbout.setVisibility(View.INVISIBLE);
                }else{
                    editing = true;
                    edit.setText("Finish");
                    aboutView.setVisibility(View.INVISIBLE);
                    editAbout.setVisibility(View.VISIBLE);
                    editAbout.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editAbout, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }
}
