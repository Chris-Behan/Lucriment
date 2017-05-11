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
import java.util.Map;
import java.util.Set;
import android.widget.AdapterView;
import android.widget.TextView;

public class ViewMessagesActivity extends AppCompatActivity {

    private ListView chats;
    private EditText chatName;
    private Button addButton;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfChats = new ArrayList<>();
    private DatabaseReference chatRoot = FirebaseDatabase.getInstance().getReference().child("Chats");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        chats = (ListView) findViewById(R.id.currentChats);
        chatName = (EditText) findViewById(R.id.addChatField);
        addButton = (Button) findViewById(R.id.addButton);


        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listOfChats);
        chats.setAdapter(arrayAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(chatName.getText().toString(),"");
                chatRoot.updateChildren(map);
            }
        });

        chatRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                listOfChats.clear();
                listOfChats.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        registerChatSelect();

    }

    private void registerChatSelect(){
        final String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        chats.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("chatName",((TextView)view).getText().toString());
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

    }
}
