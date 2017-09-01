package com.lucriment.lucriment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;

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
    private String phoneRegex ="\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private String postalRegex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
    private String rateRegex = "^([1-9][0-9]{0,2}|1000)$";
    private int tutorYear, tutorMonth, tutorDay;

    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> subjectsTaught = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private  ArrayAdapter<String> adapter;
    private String[] subjectArray;
    private String subjectPath, phoneNumberString;
    private boolean addingClass = false;
    private Button addClassButton;
    private int year, month, day;
    private EditText dateOfBirth, phoneNumber, address, city, province;
    private String addressString, cityString, provinceString;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_creation);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        userInfo = getIntent().getParcelableExtra("userInfo");
        subjectSelector = (Spinner) findViewById(R.id.subjectSpinner);
        classSelector = (Spinner) findViewById(R.id.classSpinner);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        rateField = (EditText) findViewById(R.id.rateField);
        educationField = (EditText) findViewById(R.id.educationField);
        becomeTutor = (Button) findViewById(R.id.becomeTutor);
        dateOfBirth = (EditText) findViewById(R.id.dateOfBirth);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        address = (EditText) findViewById(R.id.addressLine);
        city = (EditText) findViewById(R.id.city);
        province = (EditText) findViewById(R.id.province);
       // subjectSelector.setVisibility(View.VISIBLE);
    //    classSelector.setVisibility(View.VISIBLE);
        becomeTutor.setOnClickListener(this);
        addClassButton.setOnClickListener(this);
        dateOfBirth.setOnClickListener(this);

        dateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TutorCreation.this,android.R.style.Theme_Holo_Light_Dialog,myDateListener, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                    // datePickerDialog.getDatePicker().setMinDate(calendar.get(Calendar.MILLISECOND));
                    Window window = datePickerDialog.getWindow();
                   // window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);


                    datePickerDialog.show();
                }
            }
        });

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
       // DatePickerDialog birthPicker = new DatePickerDialog()



    }


    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //showDate(arg1, arg2+1, arg3);
                    tutorDay = arg3;
                    tutorMonth = arg2;
                    tutorYear = arg1;

                    dateOfBirth.setText(arg3+"/"+arg2+"/"+arg1);
                }
            };

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
        if(v== dateOfBirth){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(TutorCreation.this,android.R.style.Theme_Holo_Light_Dialog,myDateListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            // datePickerDialog.getDatePicker().setMinDate(calendar.get(Calendar.MILLISECOND));

            datePickerDialog.show();
        }

        if(v == addClassButton){
            if(addingClass){
                subjectsTaught.add(classSelector.getSelectedItem().toString());
                //databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
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

        if(TextUtils.isEmpty(rateField.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter rate",Toast.LENGTH_SHORT).show();
            rateField.requestFocus();
            return;
        }else if(TextUtils.isEmpty(educationField.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter postal code",Toast.LENGTH_SHORT).show();
            educationField.requestFocus();
            return;
        }else if (TextUtils.isEmpty(phoneNumber.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
            phoneNumber.requestFocus();
            return;
        }else if (TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter your address",Toast.LENGTH_SHORT).show();
            address.requestFocus();
            return;
        }else if (TextUtils.isEmpty(city.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter your city",Toast.LENGTH_SHORT).show();
            city.requestFocus();
            return;
        }else if (TextUtils.isEmpty(province.getText().toString())){
            Toast.makeText(TutorCreation.this,"Please enter your province",Toast.LENGTH_SHORT).show();
            province.requestFocus();
            return;
        }

        rate = Integer.parseInt(rateField.getText().toString());
        education = educationField.getText().toString();
        email = firebaseAuth.getCurrentUser().getEmail();
        phoneNumberString = phoneNumber.getText().toString();
        userInfo.setUserType("tutor");
        addressString = address.getText().toString();
        cityString = city.getText().toString();
        provinceString = province.getText().toString();

        if(dateOfBirth.getText().length()<3){
            Toast.makeText(TutorCreation.this,"Please enter date of birth",Toast.LENGTH_SHORT).show();
            dateOfBirth.requestFocus();
            return;
        }else if(!(rate+"").matches(rateRegex)){
            Toast.makeText(TutorCreation.this,"Invalid rate",Toast.LENGTH_SHORT).show();
            rateField.requestFocus();
            return;
        }else if(!education.matches(postalRegex)){
            Toast.makeText(TutorCreation.this,"Invalid postal code",Toast.LENGTH_SHORT).show();
            educationField.requestFocus();
            return;
        }else if(!phoneNumberString.matches(phoneRegex)){
            Toast.makeText(TutorCreation.this,"Please enter a valid phone number",Toast.LENGTH_SHORT).show();
            phoneNumber.requestFocus();
            return;
        }else if(subjectsTaught.isEmpty()){
            Toast.makeText(TutorCreation.this,"You must select at least one class to teach",Toast.LENGTH_SHORT).show();

            return;
        }



        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        id = firebaseAuth.getCurrentUser().getUid();
        displayName = firebaseAuth.getCurrentUser().getDisplayName();
       //
        //classes = classField.getText().toString();

        TutorInfo tutorInfo = new TutorInfo(userInfo,education,Long.valueOf(phoneNumberString),rate);

        tutorInfo.setSubjects(subjectsTaught);

        tutorInfo.setProfileImage(userInfo.getProfileImage());
        databaseReference.child("tutors").child(user.getUid()).updateChildren(tutorInfo.toMap());
        databaseReference.child("users").child(user.getUid()).child("userType").setValue("tutor");
        String userType = "tutor";

        Toast.makeText(this, "You are now a Tutor!", Toast.LENGTH_SHORT).show();
        finish();
        Intent y =new Intent(TutorCreation.this, TutorCreationP2.class);
        y.putExtra("day", day);
        y.putExtra("month",month);
        y.putExtra("year", year);
        y.putExtra("userInfo",userInfo);
        y.putExtra("userType",userType);
        y.putExtra("address",addressString);
        y.putExtra("city",cityString);
        y.putExtra("province",provinceString);
        y.putExtra("postalCode",tutorInfo.getPostalCode());
        startActivity(y);


    }
}
