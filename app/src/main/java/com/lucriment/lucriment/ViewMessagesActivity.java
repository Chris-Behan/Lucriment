package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.widget.AdapterView;
import android.widget.TextView;

public class ViewMessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView chats;
    private Button backButton;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfChats = new ArrayList<>();
    private DatabaseReference chatRoot = FirebaseDatabase.getInstance().getReference().child("chats");
    private String myID, tutorId;
    private List<UserInfo> users = new ArrayList<>();
    private List<Chat> chatList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashSet<String> set = new HashSet<String>();
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        users = new ArrayList<>();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            UserInfo user = dataSnapshotChild.getValue(UserInfo.class);
                            if(user.getChatsWith()!= null) {
                                if (user.getChatsWith().contains(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                                    users.add(user);
                                    set.add(user.getFullName());

                                }
                            }
                        } listOfChats.addAll(set);
                        arrayAdapter.notifyDataSetChanged();
                        // All users are retrieved except the one who is currently logged
                        // in device.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });


        tutorId = getIntent().getParcelableExtra("tutorID");
        chats = (ListView) findViewById(R.id.currentChats);
        backButton = (Button) findViewById(R.id.backToProfile);
        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(getIntent().hasExtra("tutorID"))
            tutorId = getIntent().getExtras().get("tutorID").toString();

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listOfChats);
        backButton.setOnClickListener(this);

        chats.setAdapter(arrayAdapter);

        if(tutorId != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(myID + "_" + tutorId, "");
            chatRoot.updateChildren(map);

        }


        /*
        chatRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<String> set = new HashSet<String>();
                Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    String currentKey = ((DataSnapshot)i.next()).getKey();
                    //    String tutorName = ((DataSnapshot)i.next()).getKey();
                    if(currentKey.contains(myID)){
                        //  int a = currentKey.indexOf('_') +1;
                        //  int b = currentKey.length();
                        set.add(currentKey);
                    }
                    //  set.add(((DataSnapshot)i.next()).getKey());

                }
                // HashSet<String> nameSet = convertToTutorUserName(set);
                listOfChats.clear();
                listOfChats.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */
        registerChatSelect();

    }



    private HashSet<String> convertToTutorUserName(final HashSet<String> set){
        final HashSet<String> tutorNames = new HashSet<String>();
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Tutors");
        for(final String s : set){

            dbr.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String Name = dataSnapshot.child(s).child("name").getValue().toString();
                    tutorNames.add(Name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //String Name = dbr.child(s).child("name").getKey().toString();
            //tutorNames.add(Name);

        }
        return tutorNames;
    }

    private void registerChatSelect(){
        final String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        chats.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                UserInfo selectedUser = new UserInfo();
                String s = ((TextView)view).getText().toString();
                for(UserInfo currentUser: users){
                    if(currentUser.getFullName().equals(s)){
                        selectedUser = currentUser;
                    }
                }
                // if(myID.equalsIgnoreCase(((TextView)view).getText().toString())){
                //       myID = tutorId;
                //  }
                intent.putExtra("user", selectedUser);
                // intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

    }

    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        users = new ArrayList<>();
                        chatList = new ArrayList<Chat>();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            Chat currentChat = dataSnapshotChild.getValue(Chat.class);
                            UserInfo user = dataSnapshotChild.getValue(UserInfo.class);
                            if (currentChat.senderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ||
                                    currentChat.receiverUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user);
                                listOfChats.add(user.getFullName());
                            }
                        }
                        // All users are retrieved except the one who is currently logged
                        // in device.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backToProfile:
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
        }

    }
}