package com.lucriment.lucriment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    private EditText messageField;
    private TextView conversation;
    private String userName, chatName;
    private DatabaseReference root;
    private String tempKey;
    private String chatString, displayNameString;
    private Button backButton;
    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendButton = (Button) findViewById(R.id.sentButton);
        messageField = (EditText) findViewById(R.id.messageField);
        conversation = (TextView) findViewById(R.id.convo);
        backButton = (Button) findViewById(R.id.prevButton);

            chatID = getIntent().getExtras().get("chatID").toString();
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
           //userName = getIntent().getExtras().get("userName").toString();
      //  chatName = getIntent().getExtras().get("chatName").toString();
       // DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("Chats");

        root = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID);

        setTitle(chatID);

        backButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatCovnversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatCovnversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void appendChatCovnversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
                chatString = (String) ((DataSnapshot)i.next()).getValue();
                displayNameString = (String) ((DataSnapshot)i.next()).getValue();
                conversation.append(chatString + " : " + displayNameString + " \n");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sentButton:
                Map<String,Object> map = new HashMap<String,Object>();
                tempKey = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(tempKey);
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("msg", messageField.getText().toString());
                map2.put("User Name", userName);
                messageRoot.updateChildren(map2);
                messageField.setText("");
                break;
            case R.id.prevButton:
                finish();
                startActivity(new Intent(MessageActivity.this, ViewMessagesActivity.class));


        }
    }
}
