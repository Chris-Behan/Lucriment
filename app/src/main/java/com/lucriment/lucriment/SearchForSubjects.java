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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchForSubjects extends AppCompatActivity {
    private UserInfo userInfo;
    private TutorInfo tutorInfo;
    private String userType;
    private ListView listView;
    private SubjectListViewAdapter subjectListAdapter;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> all = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_for_subjects_layout);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.search_subjects_actionbar);

        View view =getSupportActionBar().getCustomView();

        TextView labelText = (TextView) view.findViewById(R.id.textView15);
        SearchView searchView = (SearchView) view.findViewById(R.id.subjectSearch);
        categories.add("Math");
        categories.add("Science");
        subjects.add("Calculus 1");
        subjects.add("Linear Algebra 1");
        subjects.add("Calculus 2");
        subjects.add("Calculus 3");
        subjects.add("Calculus 4");
        subjects.add("Calculus 5");
        all.add("Science");
        all.add("Math");
        all.add("Calculus 1");
        all.add("Calculus 2");
        all.add("Calculus 3");
        all.add("Calculus 4");
        all.add("Calculus 5");
        all.add("Linear Algebra 1");
        listView = (ListView) findViewById(R.id.subjectLV);
        subjectListAdapter = new SubjectListViewAdapter(this,categories,subjects,all);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setAdapter(subjectListAdapter);
        subjectListAdapter.notifyDataSetChanged();


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

        //SET SEARCH BAR LISTENER
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


}
