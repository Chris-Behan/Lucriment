package com.lucriment.lucriment;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MenuItem;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TutorCreation extends AppCompatActivity implements View.OnClickListener, ProvinceSelectionDialog.NoticeDialogListener{
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
    private  ProvinceSelectionDialog psd;
    private TextView TOS;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_creation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        userInfo = getIntent().getParcelableExtra("userInfo");

        rateField = (EditText) findViewById(R.id.rateField);
        educationField = (EditText) findViewById(R.id.educationField);
        becomeTutor = (Button) findViewById(R.id.becomeTutor);
        dateOfBirth = (EditText) findViewById(R.id.dateOfBirth);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        address = (EditText) findViewById(R.id.addressLine);
        city = (EditText) findViewById(R.id.city);
        province = (EditText) findViewById(R.id.province);
        TOS = (TextView) findViewById(R.id.TOS);

        SpannableString ss = new SpannableString("By becoming a tutor you agree to our Service Agreement and the Stripe Connected Account Agreement");
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String url = "https://lucriment.com/tos.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String url = "https://stripe.com/ca/connect-account/legal";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        ss.setSpan(span1, 37, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, 63, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TOS.setText(ss);
        TOS.setMovementMethod(LinkMovementMethod.getInstance());

       // subjectSelector.setVisibility(View.VISIBLE);
    //    classSelector.setVisibility(View.VISIBLE);
        becomeTutor.setOnClickListener(this);
        dateOfBirth.setKeyListener(null);
        dateOfBirth.setOnClickListener(this);
        province.setOnClickListener(this);
        province.setKeyListener(null);
        province.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    psd = new ProvinceSelectionDialog();
                    psd.show(getFragmentManager(), "Province");
                }
            }
        });
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        province.setText(psd.getSelection());
        educationField.requestFocus();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
                   // databaseReference.child("tutors").child(user.getUid()).child("subjects").setValue(subjectsTaught);
                    adapter.notifyDataSetChanged();
                }
            });

            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        ProgressDialog progressDialog = new ProgressDialog(this);
        Intent i = new Intent(TutorCreation.this, SettingsActivity.class);
        i.putExtra("userType", "student");
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }


    @Override
    public void onClick(View v) {
        if(v == becomeTutor){
            createTutorProfile();
        }
        if(v == province){
            psd = new ProvinceSelectionDialog();
            psd.show(getFragmentManager(),"Province");
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
        education = educationField.getText().toString().replaceAll("\\s+","");;
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
        }


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        id = firebaseAuth.getCurrentUser().getUid();
        displayName = firebaseAuth.getCurrentUser().getDisplayName();
        Geocoder gc = new Geocoder(this);

        List<Address> addresses = null;
        try {
            addresses = gc.getFromLocationName(education, 1);


        } catch (IOException e) {
            e.printStackTrace();
        }



       //
        //classes = classField.getText().toString();

        TutorInfo tutorInfo = new TutorInfo(userInfo,education,phoneNumberString,rate);
        tutorInfo.setAddress(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());

        //tutorInfo.setSubjects(subjectsTaught);

        tutorInfo.setProfileImage(userInfo.getProfileImage());
        databaseReference.child("tutors").child(user.getUid()).updateChildren(tutorInfo.toMap());
        databaseReference.child("users").child(user.getUid()).child("userType").setValue("tutor");
        String userType = "tutor";

        Toast.makeText(this, "You are now a Tutor!", Toast.LENGTH_SHORT).show();
        finish();
        Intent y =new Intent(TutorCreation.this, TutorCreationP2.class);
        y.putExtra("day", tutorDay);
        y.putExtra("month",tutorMonth);
        y.putExtra("year", tutorYear);
        y.putExtra("userInfo",userInfo);
        y.putExtra("userType",userType);
        y.putExtra("address",addressString);
        y.putExtra("city",cityString);
        y.putExtra("province",provinceString);
        y.putExtra("postalCode",tutorInfo.getPostalCode());
        startActivity(y);


    }
}
