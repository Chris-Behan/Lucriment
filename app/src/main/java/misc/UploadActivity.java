package misc;
import android.Manifest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucriment.lucriment.R;

import students.UserInfo;


public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mRoofRef;
    private Uri mImageUri = null;
    private DatabaseReference mdatabaseRef, tutorRef;
    private StorageReference mStorage;
    private ImageView uploadView;
    private Button uploadButton, selectButton;
    private ProgressDialog progressDialog;
    private UserInfo userInfo;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        //INITIALIZE BUTTONS
        uploadView = (ImageView) findViewById(R.id.uploadView);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        selectButton = (Button) findViewById(R.id.selectImage);
        //INITIALIZE PROGRESS BAR
        progressDialog = new ProgressDialog(UploadActivity.this);

        //READ IMAGE FROM STORAGE
        selectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = mdatabaseRef.child("users").child(userInfo.getId()).child("profileImage");
        mStorage = FirebaseStorage.getInstance().getReference();
        tutorRef = mdatabaseRef.child("tutors").child(userInfo.getId()).child("profileImage");



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
            uploadView.setImageURI(mImageUri);
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
                            .into(uploadView);
                    Toast.makeText(getApplicationContext(), "Updated Complete", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
 

    @Override
    public void onClick(View v) {
        if(v == selectButton){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(), "Call for Permission", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                }
            }
            else
            {
                callGallery();
            }
        }
        if(v == uploadButton){
           // final String mName = title.getText().toString().trim();


         //   Firebase childRef_name = mRoofRef.child("Image_Title");
         //   childRef_name.setValue(mName);

            finish();
            startActivity(new Intent(this, ImageLayout.class));
            Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();
        }
    }
}
