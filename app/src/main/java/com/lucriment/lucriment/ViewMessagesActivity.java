package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private DatabaseReference databaseReference;
    private List<TutorInfo> tutors = new ArrayList<>();
    private ListView chats;
    private Button backButton;
    private ArrayAdapter<String> arrayAdapter2;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfChats = new ArrayList<>();
    private ArrayList<String> listOfChatInfos = new ArrayList<>();
    private DatabaseReference chatRoot = FirebaseDatabase.getInstance().getReference().child("Chats");
    private String myID, tutorId;
   // private ChatInfo currentChatInfo;
    private ArrayList<ChatInfo> chatInfoList = new ArrayList<>();

  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);
        getTutors();
        tutorId = getIntent().getParcelableExtra("tutorID");
        chats = (ListView) findViewById(R.id.currentChats);
        backButton = (Button) findViewById(R.id.backToProfile);
        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(getIntent().hasExtra("tutorID"))
        tutorId = getIntent().getExtras().get("tutorID").toString();
        arrayAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listOfChatInfos);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listOfChats);
        backButton.setOnClickListener(this);
        chats.setAdapter(arrayAdapter2);

       // chats.setAdapter(arrayAdapter);
        if(tutorId != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(myID + "_" + tutorId, "");
            chatRoot.updateChildren(map);

        }




        chatRoot.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<String> set2 = new HashSet<String>();
                HashSet<String> set = new HashSet<String>();
                Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){


               //     String currentKey[] = new String[2];
                    String currentKey = ((DataSnapshot)i.next()).getKey();
                    ChatInfo chatInfo = new ChatInfo();
                //    String tutorName = ((DataSnapshot)i.next()).getKey();
                    if(currentKey.contains(myID)){
                        chatInfo.setProperName(currentKey);
                        chatInfoList.add(chatInfo);
                       // String chatIDName = chatInfo.getTheirID();
                        String chatIDName = "";
                        for(TutorInfo t : tutors) {
                                if(t.getID()!=null)
                                if (t.getID().equalsIgnoreCase(chatInfo.getTheirID())) {
                                    chatIDName = t.getName().toString();
                                    break;
                                }

                        }
                      //  int a = currentKey.indexOf('_') +1;
                      //  int b = currentKey.length();
                     //   currentKey = returnProperName(currentKey);
                        set2.add(chatIDName);
                        set.add(currentKey);
                    }
                  //  set.add(((DataSnapshot)i.next()).getKey());

                }
               // HashSet<String> nameSet = convertToTutorUserName(set);
                listOfChatInfos.clear();
                listOfChatInfos.addAll(set2);
                listOfChats.clear();
                listOfChats.addAll(set);
                arrayAdapter2.notifyDataSetChanged();
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        registerChatSelect();

    }

    private void getTutors(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tutors");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tutorSnapShot: dataSnapshot.getChildren()){

                   TutorInfo tutor = tutorSnapShot.getValue(TutorInfo.class);
                    tutors.add(tutor);
                }
               // populateTutorList();
                // tutors =  collectNames((Map<String,Object>) dataSnapshot.getValue());
                //  populateTutorList(tNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private String returnProperName(String key){
        String myName;
        String theirName;
        int middle = key.indexOf('_');
        String s1 = key.substring(0,middle);
        String s2 = key.substring(middle+1, key.length());
        if(s1.equalsIgnoreCase(myID)){
            return s2;
        }else{
            return s1;
        }

      //  return "";
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
                ChatInfo selectedChatInfo = chatInfoList.get(position);
                String t = selectedChatInfo.getChatID();
              //  String t = ((TextView)view).getText().toString();
               // if(myID.equalsIgnoreCase(((TextView)view).getText().toString())){
             //       myID = tutorId;
              //  }
                intent.putExtra("chatID",(t));
               // intent.putExtra("userName", userName);
                startActivity(intent);
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
