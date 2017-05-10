package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SelectedTutorActivity extends AppCompatActivity {
    private TutorInfo selectedTutor;
    private TextView tutorName;
    private TutorListActivity tutorListActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tutor);
        selectedTutor = getIntent().getParcelableExtra("selectedTutor");

        // initialize buttons and fields
        tutorName = (TextView) findViewById(R.id.tutorName);

       // selectedTutor = TutorListActivity.getTutor();
        tutorName.setText(selectedTutor.getName());

        //setup buttons and fields
      //  tutorName.setText();

    }
}
