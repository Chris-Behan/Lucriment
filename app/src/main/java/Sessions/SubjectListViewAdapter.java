package Sessions;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lucriment.lucriment.R;

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
    private List<String> categoryList,subjectList,allItems, currentSubjects;

    public SubjectListViewAdapter(Context context, List<String> cList,List<String> sList, List<String> all, List<String> subjects) {
        mContext = context;
        categoryList = cList;
        subjectList = sList;
        allItems = all;
        currentSubjects = subjects;
        // DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("users").child(messageList.get(0))
    }

    @Override
    public int getCount() {
        return allItems.size();
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
    public int getItemViewType(int position) {

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
        View v = convertView;

        int type = getItemViewType(position);


        if (type == SUBJECT_CATEGORY) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_category_layout, parent, false);
            TextView category = (TextView) convertView.findViewById(R.id.category);
            category.setText(allItems.get(position));
           // return new SubjectListAdapter.categoryHolder(view);
        } else if (type == SUBJECT_SPECIFIC) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_specific_layout, parent, false);
            if(currentSubjects.contains(allItems.get(position))){
                ImageView check = (ImageView) convertView.findViewById(R.id.checkMark);
                check.setVisibility(View.VISIBLE);
            }
            TextView subject = (TextView) convertView.findViewById(R.id.subject);
            subject.setText(allItems.get(position));
           // return new SubjectListAdapter.subjectHolder(view);
        }





        return convertView;
    }

    private class categoryHolder extends RecyclerView.ViewHolder{
        TextView category;
        categoryHolder(View itemView){
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.category);
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(String c) {
            category.setText(c);
        }
    }

    private class subjectHolder extends RecyclerView.ViewHolder{
        TextView subject;
        subjectHolder(View itemView){
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.subject);
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(String s) {
            subject.setText(s);
        }
    }
}

