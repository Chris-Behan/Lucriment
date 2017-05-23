package com.lucriment.lucriment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RequestSessionActivity extends AppCompatActivity {

    private Availability selectedAvailability;
    private TutorInfo tutor;
    private TextView nameView;
    private TextView rateView;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private ArrayList<TwoItemField> itemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_session);
        selectedAvailability = getIntent().getParcelableExtra("Availability");
        tutor = getIntent().getParcelableExtra("tutor");
        nameView = (TextView) findViewById(R.id.textView5);
        rateView = (TextView) findViewById(R.id.textView6);
        imageView = (ImageView) findViewById(R.id.imageView2);

        storageReference = FirebaseStorage.getInstance().getReference();

        nameView.setText(tutor.getName());
        rateView.setText("$"+String.valueOf(tutor.getRate())+"/hr");


        StorageReference pathReference = storageReference.child("ProfilePics").child(tutor.getID());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(RequestSessionActivity.this).load(uri).fit().centerCrop().into(imageView);
            }
        });

        populateItemList();
        populateSelectionList();
        registerFieldClicks();
    }



    private void populateItemList(){
        TwoItemField field1 = new TwoItemField("Subject", "Select");
        TwoItemField field2 = new TwoItemField("Location", "Select");
        TwoItemField field3 = new TwoItemField("Time", "Select");

        if(selectedAvailability!=null){
            field1.setData(tutor.getClasses());
            field3.setData(selectedAvailability.getTime());
        }
        itemList.add(field1);
        itemList.add(field2);
        itemList.add(field3);



    }


    private void populateSelectionList(){
        ArrayAdapter<TwoItemField> adapter = new RequestSessionActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.sessionInfoList);
        list.setAdapter(adapter);


    }

    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(RequestSessionActivity.this, R.layout.session_request_field, itemList);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.session_request_field, parent, false);
            }
            TwoItemField currentTwo = itemList.get(position);
            //Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView category = (TextView) itemView.findViewById(R.id.category);
            category.setText(currentTwo.getLabel());


            TextView dataText = (TextView) itemView.findViewById(R.id.input);
            dataText.setText(currentTwo.getData());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

    private void registerFieldClicks() {
        ListView list = (ListView) findViewById(R.id.sessionInfoList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 //timeField = itemList.get(position);
                // selectedTutor1 = TutorListActivity.this.selectedTutor;
                Intent i = new Intent(RequestSessionActivity.this, TimePickerActivity.class);
                i.putExtra("timeField", selectedAvailability);
                i.putExtra("tutor",tutor);
                startActivity(i);

            }
        });

    }
}
