package messaging;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import students.SelectedTutorActivity;
import students.UserInfo;
import tutors.TutorInfo;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView sendButton;
    private EditText messageField;

    private String userName, chatName;
    private DatabaseReference root;
    private String tempKey;
    private String chatString, displayNameString;
    private Button backButton;
    private String chatID;
    private String receiverID;
    private String senderID;
    private String sender;
    private String receiver;
    private FirebaseAuth firebaseAuth;
    private String userType;
    public static UserInfo userInfo;
    private RecyclerView recyclerView;
    private ArrayList<Chat> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    static String Sender_Name;
    private boolean existingConvo = false;
    private TutorInfo tutorInfo;
    private double score;
    private DatabaseReference notificationRef;

   // public DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
   // public static int Device_Width = metrics.widthPixels;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_converastion);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        Sender_Name = userInfo.getFullName();
        firebaseAuth = FirebaseAuth.getInstance();
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageField = (EditText) findViewById(R.id.messageArea);
        UserInfo tutor = getIntent().getParcelableExtra("user");
        recyclerView = (RecyclerView) findViewById(R.id.fragment_chat_recycler_view);
        messageAdapter = new MessageAdapter(this,messages,tutor.getProfileImage());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(messageAdapter);
        notificationRef = FirebaseDatabase.getInstance().getReference().child("messageNotifications");
        sender = firebaseAuth.getCurrentUser().getDisplayName();
        receiver = tutor.getFullName();
        senderID = firebaseAuth.getCurrentUser().getUid().toString();
        receiverID = tutor.getId();
        getMessageFromFirebaseUser(senderID,receiverID);
        // chatID = getIntent().getExtras().get("chatID").toString();
        userName = userInfo.getFullName();
        actionBar.setTitle(receiver);

        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(receiverID);
        tutorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   tutorInfo = dataSnapshot.getValue(TutorInfo.class);
                if(tutorInfo!=null) {
                    if(tutorInfo.getRating()!=null) {
                        score = tutorInfo.getRating().getTotalScore() / tutorInfo.getRating().getNumberOfReviews();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //IMAGE IN ACTION BAR

        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this)
                .load(tutor.getProfileImage())
                .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                 .apply(RequestOptions.circleCropTransform())
                .into(imageView);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                150,
                150, Gravity.END
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 40;
        imageView.setLayoutParams(layoutParams);
        actionBar.setCustomView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tutorInfo!=null) {
                    Intent i = new Intent(MessageActivity.this, SelectedTutorActivity.class);
                    i.putExtra("selectedTutor", tutorInfo);
                    i.putExtra("tutorScore", score);
                    i.putExtra("userType", userType);
                    i.putExtra("userInfo", userInfo);

                    startActivity(i);
                }
            }
        });
           //userName = getIntent().getExtras().get("userName").toString();
      //  chatName = getIntent().getExtras().get("chatName").toString();
       // DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("Chats");

      //  root = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID);

       // setTitle(chatID);


        sendButton.setOnClickListener(this);
/*
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
*/

    }


    public void getMessageFromFirebaseUser(final String senderUid, final String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats").child(userInfo.getId())
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            //Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats").child(userInfo.getId()).child(receiverUid)

                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                            chatString = chat.text;

                                            displayNameString = chat.senderName;
                                           messages.add(chat);
                                            messageAdapter.notifyDataSetChanged();
                                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                                            existingConvo = true;
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }

    private void appendChatCovnversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){

                chatString = (String) ((DataSnapshot)i.next()).getValue();

                displayNameString = (String) ((DataSnapshot)i.next()).getValue();
                //messages.add(chat);
        }
    }

    public void sendMessageToFirebaseUser(
            final Chat chat
            ) {
        final String room_type_1 = chat.senderId + "_" + chat.receiverId;
        final String room_type_2 = chat.receiverId + "_" + chat.senderId;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats").child(userInfo.getId())
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverID)) {
                            //  Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                            databaseReference.child("chats")
                                    .child(senderID)
                                    .child(receiverID)
                                    .push()
                                    .setValue(chat);
                            databaseReference.child("chats")
                                    .child(receiverID)
                                    .child(senderID)
                                    .push()
                                    .setValue(chat);
                            HashMap<String, String> notificationMap = new HashMap<String, String>();
                            notificationMap.put("from", senderID);
                            notificationRef.child(receiverID).push().setValue(notificationMap);
                            if(!existingConvo){
                               // messages.add(chat);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                            }
                        }  else {
                            // Log.e(TAG, "sendMessageToFirebaseUser: success");
                            databaseReference.child("chats")
                                    .child(senderID)
                                    .child(receiverID)
                                    .push()
                                    .setValue(chat);
                            databaseReference.child("chats")
                                    .child(receiverID)
                                    .child(senderID)
                                    .push()
                                    .setValue(chat);
                            HashMap<String, String> notificationMap = new HashMap<String, String>();
                            notificationMap.put("from", senderID);
                            notificationRef.child(receiverID).push().setValue(notificationMap);
                           // messages.add(chat);
                            //messageAdapter.notifyDataSetChanged();
                            //recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                         //   HashMap<String, Object> myMap = new HashMap<String, Object>();
                         //   myMap.put(receiverID,true);
                        //    databaseReference.child("users").child(userInfo.getId()).child("chatsWith").updateChildren(myMap);
                       //     myMap.clear();
                      //      myMap.put(userInfo.getId(),true);
                      //      databaseReference.child("users").child(receiverID).child("chatsWith").updateChildren(myMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent y = new Intent(MessageActivity.this, ViewMessagesActivity.class);
        y.putExtra("userType", userType);
        y.putExtra("userInfo",userInfo);
        startActivity(y);
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendButton:
                Calendar cc = Calendar.getInstance();
                Date date = cc.getTime();
                // SimpleDateFormat format1 = new SimpleDateFormat("dd MMM");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String tsLong = FirebaseDatabase.getInstance().getReference().push().getKey();
                Map<String, String> testM  = ServerValue.TIMESTAMP;

              ///long timeStamp = (long) ServerValue.TIMESTAMP;
               Chat chat = new Chat(sender, receiver, senderID, receiverID,messageField.getText().toString(), date.getTime());
                sendMessageToFirebaseUser(chat);
                messageField.setText("");

                /*
                Map<String,Object> map = new HashMap<String,Object>();
                tempKey = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(tempKey);
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("msg", messageField.getText().toString());
                map2.put("User Name", userName);
                messageRoot.updateChildren(map2);
                messageField.setText("");
                */
                break;



        }
    }


}
