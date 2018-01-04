package Tutors;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucriment.lucriment.R;

import Students.UserInfo;

public class EditTutorProfile extends AppCompatActivity implements View.OnClickListener {

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;

    private UserInfo userInfo;
    private String userType;
    private TutorInfo tutorInfo;

    private BottomNavigationView bottomNavigationView;
    private ImageView profileImage;
    private Uri mImageUri = null;
    private ProgressDialog progressDialog;
    private TextView changePicText,about,subjects, nameLabel;
    private EditText headline, postalCode,hourlyRate, firstName, lastName,enterFirstName,enterLastName;
    private Button editButton;
    private RelativeLayout aboutRow, subjectsRow;

    private DatabaseReference databaseReference, firstNameRef, lastNameRef,postalCodeRef,rateRef,fullNameRef,headlineRef;
    private DatabaseReference mdatabaseRef, tutorRef,mRoofRef;
    private StorageReference mStorage;
    private boolean editing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.lucriment.lucriment.R.layout.activity_edit_tutor_profile);

        // GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GET TUTOR INFO
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    tutorInfo = dataSnapshot.getValue(TutorInfo.class);
                setTutorFields();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //CREATE WIDGETS
        firstName = (EditText) findViewById(R.id.enterFirstName);
        lastName = (EditText) findViewById(R.id.enterLastName);
         headline = (EditText) findViewById(R.id.headlineField);
         postalCode = (EditText) findViewById(R.id.postalCode);
         hourlyRate = (EditText) findViewById(R.id.hourlyRate);
        enterFirstName = (EditText) findViewById(R.id.editFirstName);
        enterLastName = (EditText) findViewById(R.id.editLastName);
         about = (TextView) findViewById(R.id.aboutField);
        subjects = (TextView) findViewById(R.id.subjectsText);
        nameLabel = (TextView) findViewById(R.id.textView12);
        aboutRow = (RelativeLayout) findViewById(R.id.aboutRow);
        subjectsRow = (RelativeLayout) findViewById(R.id.subjectsRow);

        //INITIALIZE WIDGETS
        profileImage = (ImageView) findViewById(R.id.profilePicture);
        progressDialog = new ProgressDialog(this);
        changePicText = (TextView) findViewById(R.id.changeText);
        editButton = (Button) findViewById(R.id.editButton);
        //SET DATABASE REFERENCES
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = mdatabaseRef.child("users").child(userInfo.getId()).child("profileImage");
        mStorage = FirebaseStorage.getInstance().getReference();
        tutorRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("profileImage");
        mStorage = FirebaseStorage.getInstance().getReference();
        firstNameRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("firstName");
        lastNameRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("lastName");
        postalCodeRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("postalCode");
        rateRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("rate");
        fullNameRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("fullName");
        headlineRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("headline");
        //SET FIELDS
        firstName.setText(userInfo.getFirstName());
        lastName.setText(userInfo.getLastName());

        postalCode.setFocusable(false);
        postalCode.setClickable(false);
        hourlyRate.setFocusable(false);
        hourlyRate.setClickable(false);
        headline.setFocusable(false);
        headline.setClickable(false);

        editButton.setOnClickListener(this);
        aboutRow.setOnClickListener(this);
        subjectsRow.setOnClickListener(this);



        //LOAD PICTURE
        if(userInfo.getProfileImage()!=null) {
            Glide.with(getApplicationContext())
                    .load(userInfo.getProfileImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }

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

    private void setTutorFields(){
        about.setText(tutorInfo.getAbout());
        postalCode.setText(tutorInfo.getPostalCode());
        postalCode.setTextColor(nameLabel.getTextColors());
        hourlyRate.setText(tutorInfo.getRate()+"");
        hourlyRate.setTextColor(nameLabel.getTextColors());
        if(tutorInfo.getSubjects()!=null) {
            subjects.setText(tutorInfo.getSubjects().toString());
        }
        headline.setText(tutorInfo.getHeadline());
        headline.setTextColor(nameLabel.getTextColors());

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
            StorageReference filePath = mStorage.child("ProfileImages").child(userInfo.getId());
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
                    userInfo.setProfileImage(downloadUri.toString());
                    Toast.makeText(getApplicationContext(), "Updated Complete", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(EditTutorProfile.this, MyProfileActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v== editButton){
            if(editing){



                editing = false;
                editButton.setText("Edit");
                firstNameRef.setValue(enterFirstName.getText().toString().trim());
                lastNameRef.setValue(enterLastName.getText().toString().trim());
                fullNameRef.setValue(enterFirstName.getText().toString().trim()+" " +enterLastName.getText().toString().trim());
                postalCodeRef.setValue(postalCode.getText().toString().trim());
                rateRef.setValue(Integer.valueOf(hourlyRate.getText().toString().trim()));
                firstName.setText(enterFirstName.getText().toString().trim());
                lastName.setText(enterLastName.getText().toString().trim());
                headlineRef.setValue(headline.getText().toString().trim());
                enterFirstName.setVisibility(View.INVISIBLE);
                enterLastName.setVisibility(View.INVISIBLE);
                firstName.setVisibility(View.VISIBLE);
                lastName.setVisibility(View.VISIBLE);
                postalCode.setFocusable(false);
                postalCode.setFocusableInTouchMode(false);
                postalCode.setClickable(false);

                hourlyRate.setFocusable(false);
                hourlyRate.setFocusableInTouchMode(false);
                hourlyRate.setClickable(false);

                headline.setFocusable(false);
                headline.setFocusableInTouchMode(false);
                headline.setClickable(false);


            }else{
                editing = true;
                editButton.setText("Finish");

            }

            if(editing){
                firstName.setVisibility(View.INVISIBLE);
                enterFirstName.setVisibility(View.VISIBLE);
                enterFirstName.setText(tutorInfo.getFirstName());



                lastName.setVisibility(View.INVISIBLE);
                enterLastName.setVisibility(View.VISIBLE);
                enterLastName.setText(tutorInfo.getLastName());
                enterLastName.setFocusable(true);

                postalCode.setFocusable(true);
                postalCode.setFocusableInTouchMode(true);
                postalCode.setClickable(true);

                hourlyRate.setFocusable(true);
                hourlyRate.setFocusableInTouchMode(true);
                hourlyRate.setClickable(true);

                headline.setFocusable(true);
                headline.setFocusableInTouchMode(true);
                headline.setClickable(true);
            }



        }
        if(v== aboutRow){
            aboutRow.setBackgroundColor(0x6644B0F2);
            Intent i = new Intent(EditTutorProfile.this, AboutActivity.class);
            i.putExtra("userInfo", userInfo);
            i.putExtra("userType",userType);
            i.putExtra("tutorInfo",tutorInfo);
            startActivity(i);
        }
        if(v == subjectsRow){
            subjectsRow.setBackgroundColor(0x6644B0F2);
            Intent i = new Intent(EditTutorProfile.this, SearchForSubjects.class);
            i.putExtra("userInfo", userInfo);
            i.putExtra("userType",userType);
            i.putExtra("tutorInfo",tutorInfo);
            startActivity(i);

        }
    }
}
