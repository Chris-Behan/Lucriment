package Misc;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import Messaging.Chat;
import com.lucriment.lucriment.R;

import java.util.List;

/**
 * Created by ChrisBehan on 8/14/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SubjectListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int SUBJECT_CATEGORY = 1;
    private static final int SUBJECT_SPECIFIC = 2;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private List<String> categoryList,subjectList,allItems;
    private List<Chat> mMessageList;
    private String convoImage;
    private Calendar cal = Calendar.getInstance();
    private long currentTime = cal.getTimeInMillis();

    public SubjectListAdapter(Context context, List<String> cList,List<String> sList, List<String> all) {
        mContext = context;
        categoryList = cList;
        subjectList = sList;
        allItems = all;
        // DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("users").child(messageList.get(0))
    }

    @Override
    public int getItemCount() {
        return categoryList.size()+subjectList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        String item = allItems.get(position);
        if(categoryList.contains(item)){
            return SUBJECT_CATEGORY;
        }else{
            return  SUBJECT_SPECIFIC;
        }

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == SUBJECT_CATEGORY) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_category_layout, parent, false);
            return new categoryHolder(view);
        } else if (viewType == SUBJECT_SPECIFIC) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_specific_layout, parent, false);
            return new subjectHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {



        String item = allItems.get(position);
        switch (holder.getItemViewType()) {
            case SUBJECT_CATEGORY:
                ((categoryHolder) holder).bind(item);
                break;
            case SUBJECT_SPECIFIC:
                ((subjectHolder) holder).bind(item);
        }



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
