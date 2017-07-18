package com.lucriment.lucriment;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PaymentActivity extends BaseActivity {

    private UserInfo userInfo;
    private String userType;
    private Button addPaymentButton;
    private HashMap<String,String> paymentInfoMap;
    private HashMap<String,Long> transactionMap;
    private TextView cardInfo;
    private String last4,brand;
    private ListView transactionHistory;
    private ArrayList<TwoItemField> transactions = new ArrayList<>();
    private ArrayAdapter<TwoItemField> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardInfo = (TextView) findViewById(R.id.cardInfo);
        addPaymentButton = (Button) findViewById(R.id.addPayment);
        transactionHistory = (ListView) findViewById(R.id.transactionHistory);
        //INITIALIZE BOTTOM NAVIGATION VIEW
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        DatabaseReference currentPaymentMethod = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("paymentInfo");
        currentPaymentMethod.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot card: dataSnapshot.getChildren()) {
                    paymentInfoMap = (HashMap<String, String>) card.getValue();
                    if(paymentInfoMap==null){
                        addPaymentButton.setText("Add");
                    }
                }
                 last4 = paymentInfoMap.get("last4");
                brand = paymentInfoMap.get("brand");
                cardInfo.setText(brand+"-"+last4);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("charges");
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot transaction: dataSnapshot.getChildren()){
                    transactionMap = (HashMap<String, Long>) transaction.getValue();
                   // Object testamount = transactionMap.get("amount");
                    double amount = Double.valueOf(transactionMap.get("amount"));
                    amount = amount/100;
                    long created = Long.valueOf(transactionMap.get("created"));
                    created = created*1000;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(created));
                    TwoItemField thisTransaction = new TwoItemField("$"+amount,dateString);
                    transactions.add(thisTransaction);
                }
                populateTransactionList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddPaymentMethodDialog addPaymentMethodDialog = new AddPaymentMethodDialog();
                Bundle args = new Bundle();

                args.putParcelable("userInfo",userInfo);
                addPaymentMethodDialog.setArguments(args);
                addPaymentMethodDialog.show(getFragmentManager(),"add");
                /*
                Card cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    Toast.makeText(PaymentActivity.this,"Invalid Card Info",Toast.LENGTH_LONG).show();
                }


                Stripe stripe = new Stripe(PaymentActivity.this, "pk_test_kRaU4qwEDlsbB9HL0JeAPFmP");
                stripe.createToken(
                        cardToSave,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to your server
                                DatabaseReference tokenref = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("paymentInfo");
                                tokenref.setValue(token);
                            }
                            public void onError(Exception error) {
                                // Show localized error message

                            }
                        }
                );

                cardToSave.setName(userInfo.getFullName());
                */

            }
        });

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_payment;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(PaymentActivity.this, SettingsActivity.class);
        i.putExtra("userType", userType);
        i.putExtra("userInfo",userInfo);
        startActivity(i);
        return true;
    }

    private void populateTransactionList(){
        adapter = new PaymentActivity.myListAdapter();
        ListView list = (ListView) findViewById(R.id.transactionHistory);
        list.setAdapter(adapter);


    }

    private class myListAdapter extends ArrayAdapter<TwoItemField> {

        public myListAdapter(){
            super(PaymentActivity.this, R.layout.transactionitem, transactions);
        }


        // @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            // make sure we have a view to work with
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.transactionitem, parent, false);
            }
            TwoItemField currentTwo = transactions.get(position);
            //Availability currentAva = avaList.get(position);
            // TutorInfo currentTutor = tutors.get(position);


            // set image imageVIew.setImageResource();
            TextView category = (TextView) itemView.findViewById(R.id.amount);
            category.setText(currentTwo.getLabel());


            TextView dataText = (TextView) itemView.findViewById(R.id.date);
            dataText.setText(currentTwo.getData());


            return itemView;
            // return super.getView(position, convertView, parent);
        }
    }

}
