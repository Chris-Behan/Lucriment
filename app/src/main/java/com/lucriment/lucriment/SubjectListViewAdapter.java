package com.lucriment.lucriment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by ChrisBehan on 8/15/2017.
 */

public class SubjectListViewAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int SUBJECT_CATEGORY = 1;
    private static final int SUBJECT_SPECIFIC = 2;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private List<String> categoryList,subjectList,allItems;

    public SubjectListViewAdapter(Context context, List<String> cList,List<String> sList, List<String> all) {
        mContext = context;
        categoryList = cList;
        subjectList = sList;
        allItems = all;
        // DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("users").child(messageList.get(0))
    }

    @Override
    public int getCount() {
        return categoryList.size()+subjectList.size();
    }

    @Override
    public Object getItem(int position) {
        String item = allItems.get(position);
        if(categoryList.contains(item)){
            return SUBJECT_CATEGORY;
        }else{
            return  SUBJECT_SPECIFIC;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}

