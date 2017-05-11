package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText messageField;
    private TextView conversation;
    private String userName, chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendButton = (Button) findViewById(R.id.sentButton);
        messageField = (EditText) findViewById(R.id.messageField);
        conversation = (TextView) findViewById(R.id.convo);

        userName = getIntent().getExtras().get("userName").toString();
        chatName = getIntent().getExtras().get("chatName").toString();

        setTitle(chatName);


    }
}
