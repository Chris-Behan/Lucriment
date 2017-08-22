package com.lucriment.lucriment;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ChrisBehan on 8/21/2017.
 */

public class PastSessionFragment extends Fragment {
    private UserInfo userInfo;
    private String userType;
    private DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference().child("sessions");

    private ArrayList<SessionRequest> pastSessions = new ArrayList<>();

    private String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> strings = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter, adapter2;
    private ListView requestListView, bookedListView;
    private ArrayList<String> pastSessionKeys = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_session_tab, container,false);
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");
        userType = args.getString("userType");


        //GET SESSION LIST
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pastSessions.clear();

                Calendar curCal = Calendar.getInstance();
                long currentTime = curCal.getTimeInMillis();
                for(DataSnapshot sSnapShot: dataSnapshot.getChildren()){
                    String thisKey = sSnapShot.getKey();
                    if(thisKey.contains(ID)){
                        strings.add(thisKey);
                        for(DataSnapshot innerSnap:sSnapShot.getChildren()){
                            SessionRequest currentIteratedSession = innerSnap.getValue(SessionRequest.class);

                            if(currentIteratedSession.isConfirmed()){
                                if(currentTime< currentIteratedSession.getTime().getFrom()){

                                }

                                if(currentTime > currentIteratedSession.getTime().getFrom()  && currentTime < currentIteratedSession.getTime().getTo()){

                                }else if(currentTime > currentIteratedSession.getTime().getTo()){
                                    pastSessions.add(currentIteratedSession);
                                    pastSessionKeys.add(innerSnap.getKey());
                                }

                            }else {

                            }
                        }


                        populatPastSessions();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }





    private void populatPastSessions(){


        adapter2 = new PastSessionFragment.bookedListAdapter();
        if(getView()!=null) {
            bookedListView = (ListView) getView().findViewById(R.id.pastSessionList);
            bookedListView.setAdapter(adapter2);
        }
        bookedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionRequest selectedSession = pastSessions.get(position);
                // TutorInfo selectedTutor1 = tutors.get(position);
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(getApplicationContext(), PastDetailsActivity.class);


                if(selectedSession.getLocation()==null){

                }else {
                    String key = pastSessionKeys.get(position);
                    if (userType.equals("tutor")) {
                        i.putExtra("name", selectedSession.getStudentName());
                    } else {
                        i.putExtra("name", selectedSession.getTutorName());
                    }
                    i.putExtra("time", selectedSession.getTime());
                    i.putExtra("location", selectedSession.getLocation());
                    i.putExtra("subject", selectedSession.getSubject());
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo", userInfo);
                    i.putExtra("requestId", selectedSession.getStudentId());
                    i.putExtra("requestKey", key);

                    //  i.putExtra("selectedTutor", selectedTutor1);

                    startActivity(i);

                }

            }
        });



    }


    private class bookedListAdapter extends ArrayAdapter<SessionRequest>  {

        public bookedListAdapter(){
            super(getApplicationContext(), R.layout.bookedsessionlayout, pastSessions);
        }


        // @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.bookedsessionlayout, parent, false);
            }
            final SessionRequest session = pastSessions.get(position);

            //initialize inner fields
            TextView nameText = (TextView) itemView.findViewById(R.id.name);
            TextView subjectText = (TextView) itemView.findViewById(R.id.subject);
            final TextView timeText = (TextView) itemView.findViewById(R.id.timeInterval);
            TextView locationText = (TextView) itemView.findViewById(R.id.locationtext);

            //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM \n ");
            if(session.getLocation()==null){
                nameText.setText("No Session Requests");
                subjectText.setText("");
                timeText.setText("");
                locationText.setText("");
            }else {

                //set inner fields
                nameText.setText(session.getStudentName());
                subjectText.setText(session.getSubject());
                timeText.setText(session.getTime().returnSessionTime());
                locationText.setText(session.getLocation());
            }

            return itemView;
            // return super.getView(position, convertView, parent);
        }


    }

}
