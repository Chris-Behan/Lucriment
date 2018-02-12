package messaging;

import android.app.ProgressDialog;
import android.content.Intent;

import java.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import misc.BottomNavHelper;

import com.lucriment.lucriment.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.RelativeLayout;
import android.widget.TextView;

import students.UserInfo;

public class ViewMessagesActivity extends BaseActivity implements View.OnClickListener {

    private ListView chats;
    private Button backButton;
    private ArrayAdapter<UserInfo> arrayAdapter;
    private ArrayList<Chat> listOfChats = new ArrayList<>();
    private DatabaseReference chatRoot;
    private DatabaseReference userRoot = FirebaseDatabase.getInstance().getReference().child("users");
    private String myID, tutorId;
    private List<UserInfo> users = new ArrayList<>();
    private List<String> usersIds = new ArrayList<>();
    private List<Chat> chatList = new ArrayList<>();
    private UserInfo userInfo;
    private String userType;
    private RelativeLayout noMessages;
    private ProgressDialog progressDialog;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        noMessages = (RelativeLayout) findViewById(R.id.noMessages);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getSupportActionBar().setTitle("Messages");
        if(userType.equals("tutor")) {
             bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
                bottomNavigationView.setVisibility(View.VISIBLE);
        }else{
             bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation2);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
      //  FirebaseDatabase.getInstance().getReference().child("users").child("P6Q1eBzDqBbsrurFVpbOlFLqfQL2").child("lastName").push().setValue("test");
        chatRoot = FirebaseDatabase.getInstance().getReference().child("chats").child(userInfo.getId());
        chatRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Chat> chats = new ArrayList<Chat>();
                for(DataSnapshot ds:dataSnapshot.getChildren()){



                        String convoString = ds.getKey();
                        Chat lastChat = new Chat();
                        for(DataSnapshot innerSnap:ds.getChildren()){
                            lastChat = innerSnap.getValue(Chat.class);
                        }

                        chats.add(lastChat);

                        if(lastChat.receiverName.equals(userInfo.getFullName())) {
                            listOfChats.add(lastChat);
                         //   usersIds.add(lastChat.senderId);
                        }else{
                            listOfChats.add(lastChat);
                          //  usersIds.add(lastChat.receiverId);
                        }

                }

                Collections.sort(listOfChats, new ChatTimeComparator());

                for(Chat c : listOfChats){
                    if(c.receiverName.equals(userInfo.getFullName())){
                        usersIds.add(c.senderId);
                    }else {
                        usersIds.add(c.receiverId);
                    }
                }
                if(listOfChats.isEmpty()){
                    noMessages.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String id:usersIds){
                    users.add(dataSnapshot.child(id).getValue(UserInfo.class));
                }
                registerChatSelect();
                progressDialog.dismiss();
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        tutorId = getIntent().getParcelableExtra("tutorID");
        chats = (ListView) findViewById(R.id.currentChats);

        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(getIntent().hasExtra("tutorID"))
            tutorId = getIntent().getExtras().get("tutorID").toString();

        arrayAdapter = new ViewMessagesActivity.myListAdapter();


        chats.setAdapter(arrayAdapter);

        if(tutorId != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(myID + "_" + tutorId, "");
            chatRoot.updateChildren(map);

        }




    }

    private class myListAdapter extends ArrayAdapter<UserInfo> {

        public myListAdapter(){
            super(ViewMessagesActivity.this, R.layout.tutor_profile_layout, users);
        }


        // @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.message_preview_item, parent, false);
            }
            //Find tutor to work with
            UserInfo currentTutor = users.get(position);
            Chat currentChat = listOfChats.get(position);
            //fill the view
            final ImageView imageView = (ImageView)itemView.findViewById(R.id.imageView5);
            Glide.with(getApplicationContext())
                    .load(currentTutor.getProfileImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.com_facebook_profile_picture_blank_portrait))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
            // set image imageVIew.setImageResource();
            TextView nameText = (TextView) itemView.findViewById(R.id.nameText);

            nameText.setText(currentTutor.getFullName());

           TextView messageText = (TextView) itemView.findViewById(R.id.messageText);
            messageText.setText(currentChat.text);

            TextView timeText = (TextView) itemView.findViewById(R.id.timeText);
            SimpleDateFormat sdf;
            Calendar currentTime = Calendar.getInstance();
            Calendar messageTime = Calendar.getInstance();
            messageTime.setTimeInMillis(currentChat.timestamp);
            int current = currentTime.get(Calendar.WEEK_OF_YEAR);
            int message = messageTime.get(Calendar.WEEK_OF_YEAR);
            if(current == message  ){
                if(currentTime.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)){
                    sdf  = new SimpleDateFormat("hh:mm a");
                }else{
                    sdf  = new SimpleDateFormat("EEE");
                }

            }else{
                sdf  = new SimpleDateFormat("MMM d" );
            }
            timeText.setText(sdf.format(currentChat.timestamp));
           // timeText.setText(sdf.format(1503554400000l));

            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_view_messages;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.inbox;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
        return userInfo;
    }




    private void registerChatSelect(){
        final String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        chats.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                UserInfo selectedUser = new UserInfo();

                selectedUser = users.get(position);
                // if(myID.equalsIgnoreCase(((TextView)view).getText().toString())){
                //       myID = tutorId;
                //  }
                intent.putExtra("user", selectedUser);
                intent.putExtra("userType", userType);
                intent.putExtra("userInfo",userInfo);
                // intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onClick(View v) {


    }
}