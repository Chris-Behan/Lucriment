package com.lucriment.lucriment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ChrisBehan on 6/3/2017.
 */

public class setSubjects {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ArrayList> highSchool = new ArrayList<>();
    private ArrayList<String> university;

    private ArrayList<String> Math = new ArrayList<>();
    private ArrayList<String> Science = new ArrayList<>();
    private ArrayList<String> English = new ArrayList<>();
    private ArrayList<String> SocialStudies = new ArrayList<>();

    public setSubjects(){


        Math.add("Math 10");
        Math.add("Math 20");
        Math.add("Math 30");
        Math.add("Pre-Calculus");

        Science.add("Science 10");
        Science.add("Chemistry 20");
        Science.add("Chemistry 30");
        Science.add("Physics 20");
        Science.add("Physics 30");
        Science.add("Biology 20");
        Science.add("Biology 30");

        English.add("English 10");
        English.add("English 20");
        English.add("English 30");

        SocialStudies.add("Social Studies 10");
        SocialStudies.add("Social Studies 20");
        SocialStudies.add("Social Studies 30");

        highSchool.add(Math);
        highSchool.add(Science);
        highSchool.add(English);
        highSchool.add(SocialStudies);





    }
    public void setData() throws JSONException {

        HashMap<String, HashMap<String, String>> subjectMap = new HashMap<>();
        HashMap<String, String> mathMap = new HashMap<>();
        HashMap<String, String> scienceMap = new HashMap<>();
        HashMap<String, String> englishMap = new HashMap<>();
        HashMap<String, String> socialStudiesMap = new HashMap<>();

        mathMap.put("Math","Math 10");
        mathMap.put("Math","Math 20");
        mathMap.put("Math","Math 30");
        mathMap.put("Math","Pre-Calculus");

        scienceMap.put("Science", "Science 10");
        scienceMap.put("Science", "Chemistry 20");
        scienceMap.put("Science", "Chemistry 30");
        scienceMap.put("Science", "Physics 20");
        scienceMap.put("Science", "Physics 30");
        scienceMap.put("Science", "Biology 20");
        scienceMap.put("Science", "Biology 30");

        englishMap.put("English", "English 10");
        englishMap.put("English", "English 20");
        englishMap.put("English", "English 30");

        socialStudiesMap.put("Social Studies", "Social Studies 10");
        socialStudiesMap.put("Social Studies", "Social Studies 20");
        socialStudiesMap.put("Social Studies", "Social Studies 30");

        subjectMap.put("Math", mathMap);
        subjectMap.put("Science", scienceMap);
        subjectMap.put("English", englishMap);
        subjectMap.put("Social Studies", socialStudiesMap);

        JSONObject json = new JSONObject();
        JSONArray jArr1 = new JSONArray();
        jArr1.put(0, "Science");
        jArr1.put(1, "Math");
        //json.put("Subjects",jArr1);


        databaseReference.child("subjects").setValue(jArr1);
    }




}
