package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectedTutorActivity extends AppCompatActivity implements View.OnClickListener {
    private TutorInfo selectedTutor;
    private TextView tutorName;
    private TutorListActivity tutorListActivity;
    private Button backButton;
    private TextView educationField;
    private TextView bioField;
    private TextView rateField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tutor);
        selectedTutor = getIntent().getParcelableExtra("selectedTutor");

        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.tutorName);
        backButton = (Button) findViewById(R.id.backButton);
        educationField = (TextView) findViewById(R.id.tutorEducationFIeld);
        bioField = (TextView) findViewById(R.id.tutorBioField);
        rateField = (TextView) findViewById(R.id.tutorRateField);

       // selectedTutor = TutorListActivity.getTutor();
        tutorName.setText(selectedTutor.getName());
        educationField.setText(selectedTutor.getEducation());
        bioField.setText("");
        rateField.setText(String.valueOf(selectedTutor.getRate()));

        backButton.setOnClickListener(this);
        //setup buttons and fields
      //  tutorName.setText();

    }

    @Override
    public void onClick(View v) {

        if(v == backButton){
            finish();
            startActivity(new Intent(SelectedTutorActivity.this, TutorListActivity.class));
        }
    }
}
