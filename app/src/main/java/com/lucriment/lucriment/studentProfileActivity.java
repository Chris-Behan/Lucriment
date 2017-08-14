package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class studentProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView name, about;
    private UserInfo userInfo;
    private String userType;
    private Button editButton, uploadButton;
    private boolean editing = false;
    private EditText editAbout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        //INITIALIZE FIELDS
        name = (TextView) findViewById(R.id.studentName);
        about = (TextView) findViewById(R.id.aboutField);
        imageView = (ImageView) findViewById(R.id.imageView4);
        editButton = (Button) findViewById(R.id.Edit);
        uploadButton = (Button) findViewById(R.id.changePicture);
        editAbout = (EditText) findViewById(R.id.editAbout);

        name.setText(userInfo.getFullName());
        Glide.with(getApplicationContext())
                .load(userInfo.getProfileImage())
                .into(imageView);
        about.setText(userInfo.getHeadline());

        editButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == editButton){
            if(editing){
                editing = false;
                uploadButton.setVisibility(View.INVISIBLE);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(userInfo.getId()).child("about").setValue(editAbout.getText().toString());
                about.setText(editAbout.getText());
                about.setVisibility(View.VISIBLE);
                editAbout.setVisibility(View.INVISIBLE);
            }else{
                editing = true;
                about.setVisibility(View.INVISIBLE);
                editAbout.setVisibility(View.VISIBLE);
            }
            if(editing){
                uploadButton.setVisibility(View.VISIBLE);
            }

        }
        if(v == uploadButton) {
            Intent i = new Intent(studentProfileActivity.this, UploadActivity.class);
            i.putExtra("userType", userType);
            i.putExtra("userInfo", userInfo);
            startActivity(i);
        }


    }
}
