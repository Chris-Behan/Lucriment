package com.lucriment.lucriment;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditTutorProfile extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;

    private UserInfo userInfo;
    private String userType;

    private BottomNavigationView bottomNavigationView;
    private ImageView profileImage;
    private Uri mImageUri = null;
    private ProgressDialog progressDialog;
    private TextView changePicText;

    private DatabaseReference databaseReference;
    private DatabaseReference mdatabaseRef, tutorRef,mRoofRef;
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutor_profile);

        // GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        //SET UP BOTTOM NAVIGATION VIEW

        //INITIALIZE WIDGETS
        profileImage = (ImageView) findViewById(R.id.profilePicture);
        progressDialog = new ProgressDialog(this);
        changePicText = (TextView) findViewById(R.id.changeText);
        //SET DATABASE REFERENCES
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = mdatabaseRef.child("users").child(userInfo.getId()).child("profileImage");
        mStorage = FirebaseStorage.getInstance().getReference();
        tutorRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("profileImage");
        mStorage = FirebaseStorage.getInstance().getReference();



        //LOAD PICTURE
        Glide.with(getApplicationContext())
                .load(userInfo.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);

        changePicText.setOnClickListener(new View.OnClickListener() {
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

    private void callGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            profileImage.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("ProfilePics").child(userInfo.getId());
            //  progressDialog = new ProgressDialog(UploadActivity.this);
            progressDialog.setMessage("Uploading Image....");
            progressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();  //Ignore This error
                    if(userInfo.getUserType().equals("tutor")){
                        mRoofRef.setValue(downloadUri.toString());
                        tutorRef.setValue(downloadUri.toString());

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


}
