package misc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sessions.SessionRequest;

public class SessionsTableActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ListView requestList;
    private ListView bookedList;
    private FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();;
    private ArrayList<SessionRequest> sessionList = new ArrayList<>();
    private ArrayAdapter<SessionRequest> adapter;
    private ArrayAdapter<SessionRequest> adapter2;
    private SessionRequest clickedSession;
    private int indexOfClickedSession;
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference().child("tutors").child(user.getUid()).child("SessionRequests");
    private ArrayList<SessionRequest> bookedSessions = new ArrayList<>();
    private Button backButton;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions_table);
      //  expListView = (ExpandableListView) findViewById(R.id.expandableList);


        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("tutors").child(user.getUid()).child("BookedSessions");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookedSessions.clear();
                for(DataSnapshot bSnapShot: dataSnapshot.getChildren()){
                    SessionRequest sessionRequest = bSnapShot.getValue(SessionRequest.class);
                    bookedSessions.add(sessionRequest);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        // preparing list data
        prepareListData();

       // listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
       // expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

}
