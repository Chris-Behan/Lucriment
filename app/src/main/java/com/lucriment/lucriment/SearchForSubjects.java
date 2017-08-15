package com.lucriment.lucriment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchForSubjects extends AppCompatActivity {
    private UserInfo userInfo;
    private TutorInfo tutorInfo;
    private String userType;
    private RecyclerView recyclerView;
    private SubjectListAdapter subjectListAdapter;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> all = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_subjects);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.search_subjects_actionbar);

        View view =getSupportActionBar().getCustomView();

        TextView labelText = (TextView) view.findViewById(R.id.textView15);
        SearchView searchView = (SearchView) view.findViewById(R.id.subjectSearch);
        categories.add("Math");
        subjects.add("Calculus 1");
        subjects.add("Linear Algebra 1");
        all.add("Math");
        all.add("Calculus 1");
        all.add("Linear Algebra 1");
        recyclerView = (RecyclerView) findViewById(R.id.subjectRecyclerView);
        subjectListAdapter = new SubjectListAdapter(this,categories,subjects,all);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(subjectListAdapter);


        // GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("tutorInfo")){
            tutorInfo = getIntent().getParcelableExtra("tutorInfo");
        }
    }


}
