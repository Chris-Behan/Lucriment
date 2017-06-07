package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorCreation extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String displayName;
    private int rating;
    private ArrayList<String> classes = new ArrayList<>();
    private String education;
    private String email;
    private String id;
    private int rate;
    private EditText classField;
    private EditText rateField;
    private EditText educationField;
    private Button becomeTutor;
    private UserInfo userInfo;
    private Spinner subjectSelector;
    private Spinner classSelector;

    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> subjectsTaught = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private  ArrayAdapter<String> adapter;
    private String[] subjectArray;
    private String subjectPath;
    private boolean addingClass = false;
    private Button addClassButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_creation);
        userInfo = getIntent().getParcelableExtra("userInfo");
        subjectSelector = (Spinner) findViewById(R.id.subjectSpinner);
        classSelector = (Spinner) findViewById(R.id.classSpinner);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        rateField = (EditText) findViewById(R.id.rateField);
        educationField = (EditText) findViewById(R.id.educationField);
        becomeTutor = (Button) findViewById(R.id.becomeTutor);

       // subjectSelector.setVisibility(View.VISIBLE);
    //    classSelector.setVisibility(View.VISIBLE);
        becomeTutor.setOnClickListener(this);
        addClassButton.setOnClickListener(this);

        adapter = new TutorCreation.taughtClassAdapter();
        ListView list = (ListView) findViewById(R.id.taughtlist);
        list.setAdapter(adapter);

        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("subjects").child("highschool");
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot subSnap:dataSnapshot.getChildren()){
                    subjects.add(subSnap.getKey());
                }

                    handleSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private class taughtClassAdapter extends ArrayAdapter<String> {

        public taughtClassAdapter(){
            super(TutorCreation.this, R.layout.taught_item, subjectsTaught);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.taught_item, parent, false);
            }
            //Find tutor to work with
            Button deleteButton = (Button) itemView.findViewById(R.id.delete);
            String subject = subjectsTaught.get(position);

            //fill the view

            TextView subjectText = (TextView) itemView.findViewById(R.id.taughtLabel);
            subjectText.setText(subject);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectsTaught.remove(position);
                    databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
                    adapter.notifyDataSetChanged();
                }
            });

            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    private void handleSpinner(){

        subjectArray = subjects.toArray(new String[subjects.size()]);
        ArrayAdapter<String> subjectNameAdapter = new ArrayAdapter<String>(TutorCreation.this,
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
                            ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(TutorCreation.this,
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
        if(v == becomeTutor){
            createTutorProfile();
        }

        if(v == addClassButton){
            if(addingClass){
                subjectsTaught.add(classSelector.getSelectedItem().toString());
                databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
                addingClass = false;

            }else{
                addingClass = true;
            }

            if(addingClass){
                addClassButton.setText("select");
                subjectSelector.setVisibility(View.VISIBLE);
                classSelector.setVisibility(View.VISIBLE);
            }else{
                addClassButton.setText("Add class");
                subjectSelector.setVisibility(View.INVISIBLE);
                classSelector.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void createTutorProfile(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        id = firebaseAuth.getCurrentUser().getUid();
        displayName = firebaseAuth.getCurrentUser().getDisplayName();
       //
        //classes = classField.getText().toString();
        rate = Integer.parseInt(rateField.getText().toString());
        education = educationField.getText().toString();
        email = firebaseAuth.getCurrentUser().getEmail();
        TutorInfo tutorInfo = new TutorInfo(userInfo,education,123456789,rate);
        tutorInfo.setSubjects(subjectsTaught);
        databaseReference.child("tutors").child(user.getUid()).setValue(tutorInfo);
        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(TutorCreation.this, ProfileActivity.class));

    }
}
