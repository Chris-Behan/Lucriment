package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SelectedTutorActivity extends AppCompatActivity implements View.OnClickListener {
    private TutorInfo selectedTutor;
    private TextView tutorName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private Button contactButton;
    private TextView educationField;
    private TextView bioField;
    private TextView rateField;
    private TextView classesField;
    private ImageView imageView;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String bio;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tutor);
        selectedTutor = getIntent().getParcelableExtra("selectedTutor");
        storageReference = FirebaseStorage.getInstance().getReference();


        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.tutorName);
        backButton = (Button) findViewById(R.id.backButton);
        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorBioField);
        rateField = (TextView) findViewById(R.id.tutorRateField);
        contactButton = (Button) findViewById(R.id.contactButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        classesField = (TextView) findViewById(R.id.classesField);

       // selectedTutor = TutorListActivity.getTutor();
        tutorName.setText(selectedTutor.getName());
        educationField.setText(selectedTutor.getEducation());
        bioField.setText("");
        rateField.setText(String.valueOf(selectedTutor.getRate()));
        classesField.setText(selectedTutor.getClasses());
        backButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);

        StorageReference pathReference = storageReference.child("ProfilePics").child(selectedTutor.getID());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(SelectedTutorActivity.this).load(uri).fit().centerCrop().into(imageView);
            }
        });
        //setup buttons and fields
      //  tutorName.setText();

    }

    @Override
    public void onClick(View v) {

        if(v == backButton){
            finish();
            startActivity(new Intent(SelectedTutorActivity.this, TutorListActivity.class));
        }
        if(v == contactButton){
            Intent i = new Intent(SelectedTutorActivity.this, ViewMessagesActivity.class);
            i.putExtra("tutorID", selectedTutor.getID());

            startActivity(i);
        }
    }
}
