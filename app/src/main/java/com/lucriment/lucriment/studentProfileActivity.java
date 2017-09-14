package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class studentProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;

    private Uri mImageUri = null;
    private ImageView profileImage;
    private UserInfo userInfo;
    private String userType;
    private Button editButton;
    private TextView changePicture;
    private boolean editing = false;
    private EditText editAbout, firstName, lastName, fNameLabel,lNameLabel;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private DatabaseReference mdatabaseRef, mRoofRef, firstNameRef, lastNameRef, headlineRef;


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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //INITIALIZE FIELDS
        firstName = (EditText) findViewById(R.id.editFirstName);
        lastName = (EditText) findViewById(R.id.editLastName);
        profileImage = (ImageView) findViewById(R.id.profilePicture);
        editButton = (Button) findViewById(R.id.editButton);
        changePicture = (TextView) findViewById(R.id.changeText);
        editAbout = (EditText) findViewById(R.id.headlineField);
        mStorage = FirebaseStorage.getInstance().getReference();
        fNameLabel = (EditText) findViewById(R.id.enterFirstName);
        lNameLabel = (EditText) findViewById(R.id.enterLastName);
        editAbout.setFocusable(false);
        editAbout.setFocusableInTouchMode(false);
        editAbout.setClickable(false);

        //SET DATABASE REFERENCES
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = mdatabaseRef.child("users").child(userInfo.getId()).child("profileImage");
        firstNameRef = mdatabaseRef.child("users").child(userInfo.getId()).child("firstName");
        lastNameRef = mdatabaseRef.child("users").child(userInfo.getId()).child("lastName");
        headlineRef = mdatabaseRef.child("users").child(userInfo.getId()).child("headline");

        firstName.setText(userInfo.getFirstName());
        lastName.setText(userInfo.getLastName());
        fNameLabel.setText(userInfo.getFirstName());
        lNameLabel.setText(userInfo.getLastName());
        if(userInfo.getProfileImage()!=null) {
            Glide.with(getApplicationContext())
                    .load(userInfo.getProfileImage())
                    .into(profileImage);
        }
        editAbout.setText(userInfo.getHeadline());

        editButton.setOnClickListener(this);
        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Call for Permission", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                }
                else
                {
                    callGallery();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == editButton) {
            if (editing) {
                editing = false;
                firstNameRef.setValue(firstName.getText().toString());
                lastNameRef.setValue(lastName.getText().toString());
                headlineRef.setValue(editAbout.getText().toString());
                fNameLabel.setVisibility(View.VISIBLE);
                lNameLabel.setVisibility(View.VISIBLE);
                fNameLabel.setText(firstName.getText());
                lNameLabel.setText(lastName.getText());
                firstName.setVisibility(View.INVISIBLE);
                lastName.setVisibility(View.INVISIBLE);
                editAbout.setClickable(false);
                editAbout.setFocusable(false);
                editAbout.setFocusableInTouchMode(false);
                userInfo.setHeadline(editAbout.getText().toString());


            } else {
                fNameLabel.setVisibility(View.INVISIBLE);
                lNameLabel.setVisibility(View.INVISIBLE);
                firstName.setVisibility(View.VISIBLE);
                lastName.setVisibility(View.VISIBLE);

                editing = true;
                firstName.setClickable(true);
                firstName.setFocusable(true);
                firstName.setFocusableInTouchMode(true);

                lastName.setClickable(true);
                lastName.setFocusable(true);
                lastName.setFocusableInTouchMode(true);

                editAbout.setClickable(true);
                editAbout.setFocusable(true);
                editAbout.setFocusableInTouchMode(true);

            }
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(studentProfileActivity.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            profileImage.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("ProfileImages").child(userInfo.getId());
              progressDialog = new ProgressDialog(studentProfileActivity.this);
            progressDialog.setMessage("Uploading Image....");
            progressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();  //Ignore This error
                    if(userInfo.getUserType().equals("tutor")){
                        mRoofRef.setValue(downloadUri.toString());


                    }else{
                        mRoofRef.setValue(downloadUri.toString());
                    }

                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            //.centerCrop()
                            // .placeholder(R.drawable.loading)
                            //   .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(profileImage);
                    Toast.makeText(getApplicationContext(), "Updated Complete", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void callGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }
}
