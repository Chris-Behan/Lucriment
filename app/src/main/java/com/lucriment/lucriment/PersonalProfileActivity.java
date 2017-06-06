package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PersonalProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView personalName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private Button editButton;
    private TextView educationField;
    private TextView bioField;
    private boolean editing;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private UserInfo userInfo;
    private EditText editBioText;
    private FirebaseUser user;
    private Button uploadButton;
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> classes = new ArrayList<>();
    private StorageReference storageReference;
    private ProgressDialog picUploadDialog;
    private static final int GALLERYINTENT = 2;
    private ImageView imageView;
    private Spinner subjectSelector;
    private Spinner classSelector;
    private String subjectPath;
    private Uri downloadUri;
    private boolean isTutor;
    private String[] subjectArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        final String currentKey = firebaseAuth.getCurrentUser().getUid();
        if(userInfo == null) {
            if (getIntent().hasExtra("userInfo")) {
                userInfo = getIntent().getParcelableExtra("userInfo");
            }
        }



        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot highSchoolSnap = dataSnapshot.child("subjects").child("highschool");

                for(DataSnapshot subjectSnap: highSchoolSnap.getChildren()){
                    subjects.add(subjectSnap.getKey());
                }



                handleSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot studentSnap = dataSnapshot.child("users");
                for(DataSnapshot userSnapShot: studentSnap.getChildren()){
                    if(userSnapShot.getKey().equals(currentKey)){
                            userInfo = userSnapShot.getValue(UserInfo.class);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // initialize buttons and fields
        personalName = (TextView) findViewById(R.id.tutorName);
        backButton = (Button) findViewById(R.id.backButton);
        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorBioField);
        editBioText = (EditText) findViewById(R.id.editBioField);
        editButton = (Button) findViewById(R.id.editButton);
        uploadButton = (Button) findViewById(R.id.uploadPhoto);
        imageView = (ImageView) findViewById(R.id.imageView);
        subjectSelector = (Spinner) findViewById(R.id.subjectSpinner);
        classSelector = (Spinner) findViewById(R.id.classSpinner);
        picUploadDialog = new ProgressDialog(this);
        String[] testarr = new String[]{"hello","goodbye"};

        personalName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        educationField.setText(userInfo.getFullName());
        bioField.setText(userInfo.getFullName());
       // String dURI = "https://firebasestorage.googleapis.com/v0/b/lucriment.appspot.com/o/ProfilePics%2FRG095XpINNSl7W1BPFiIqtJvO2h2?alt=media&token=78db062a-a4c8-4221-893f-6510243d590b";

       // Picasso.with(PersonalProfileActivity.this).load(downloadUri).fit().centerCrop().into(imageView);



        editButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        if(userInfo!= null) {
            if(userInfo.getUserType().equals("Tutor")){
                isTutor = true;
            }
            new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                    .execute(userInfo.getProfileImage());
        }

   //     if(userInfo.getProfileImage()!= null) {
         //  Picasso.with(PersonalProfileActivity.this).load("https://firebasestorage.googleapis.com/v0/b/lucriment.appspot.com/o/ProfilePics%2FRG095XpINNSl7W1BPFiIqtJvO2h2?alt=media&token=d18e97f6-3087-4858-9260-ff9694cc6bf7").fit().centerCrop().into(imageView);
    //    }
        //setup buttons and fields
        //  tutorName.setText();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERYINTENT && resultCode == RESULT_OK){
            picUploadDialog.setMessage("Uploading...");
            picUploadDialog.show();

            Uri uri = data.getData();
            StorageReference profilePicPath = storageReference.child("ProfilePics").child(firebaseAuth.getCurrentUser().getUid());

            profilePicPath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    picUploadDialog.dismiss();
                    Toast.makeText(PersonalProfileActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    downloadUri = taskSnapshot.getDownloadUrl();
                   // userInfo.setProfileImage(downloadUri.toString());
                    databaseReference.child("users").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                    if(isTutor){
                        databaseReference.child("tutors").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                    }
                    new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                            .execute(downloadUri.toString());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    private void handleSpinner(){

            subjectArray = subjects.toArray(new String[subjects.size()]);
            ArrayAdapter<String> subjectNameAdapter = new ArrayAdapter<String>(PersonalProfileActivity.this,
                    android.R.layout.simple_list_item_1, subjectArray);
            subjectNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSelector.setAdapter(subjectNameAdapter);
        subjectSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectPath = subjectSelector.getSelectedItem().toString();


                DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                db3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(subjectPath!= null) {
                            DataSnapshot categorySnap = dataSnapshot.child("subjects").child("highschool").child(subjectPath);
                            classes.clear();
                            for(DataSnapshot classSnap: categorySnap.getChildren()){
                                classes.add(classSnap.getValue().toString());

                            }
                            String[] classesArray = classes.toArray(new String[classes.size()]);
                            ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(PersonalProfileActivity.this,
                                    android.R.layout.simple_list_item_1, classesArray);
                            classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            classSelector.setAdapter(classNameAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        subjectPath = subjectSelector.getSelectedItem().toString();

    }



    @Override
    public void onClick(View v) {

        if(v == backButton){
            Intent i = new Intent(PersonalProfileActivity.this, ProfileActivity.class);
            i.putExtra("userInfo", userInfo);

            startActivity(i);

        }
        if(v == editButton){
            if(editing){
                editing = false;
            }else{
                editing = true;
            }
            if(editing) {
                bioField.setVisibility(View.INVISIBLE);
                editBioText.setVisibility(View.VISIBLE);
            }else{
                bioField.setVisibility(View.VISIBLE);
                editBioText.setVisibility(View.INVISIBLE);
                bioField.setText(editBioText.getText());
                userInfo.setTitle(editBioText.getText().toString());
                databaseReference.child("Students").child(user.getUid()).setValue(userInfo);
                if(userInfo.getUserType().equals("Tutor")){
                    databaseReference.child("Tutors").child(user.getUid()).child("bio").setValue(userInfo.getTitle());
                }


            }

        }
        if(v == uploadButton){
            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");
           // intent.putExtra("userInfo", userInfo);
            startActivityForResult(intent, GALLERYINTENT);
        }

    }
}
