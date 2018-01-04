package Students;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import Misc.BottomNavHelper;
import com.lucriment.lucriment.R;

import Misc.TwoItemField;

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
    private Button stripeBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardInfo = (TextView) findViewById(R.id.cardInfo);
        addPaymentButton = (Button) findViewById(R.id.addPayment);
        transactionHistory = (ListView) findViewById(R.id.transactionHistory);
        stripeBadge = (Button) findViewById(R.id.stripeBadge1);
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
        currentPaymentMethod.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot card: dataSnapshot.getChildren()) {
                    paymentInfoMap = (HashMap<String, String>) card.getValue();
                    if(paymentInfoMap==null){
                        addPaymentButton.setText("Add");
                    }
                }
                if(paymentInfoMap!=null) {
                    last4 = paymentInfoMap.get("last4");
                    brand = paymentInfoMap.get("brand");
                    cardInfo.setText(brand + "-" + last4);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        stripeBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://stripe.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("charges");
       /* HashMap<String,Object> charge = new HashMap<>();
        charge.put("amount",5000);
        charge.put("destination","acct_1Ax1b9Fng0ouUOMP");
        transactionRef.push().setValue(charge); */
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot transaction: dataSnapshot.getChildren()){
                    transactionMap = (HashMap<String, Long>) transaction.getValue();
                   // Object testamount = transactionMap.get("amount");
                    double amount = Double.valueOf(transactionMap.get("amount"));
                    amount = amount/100;
                    if(transactionMap.containsKey("created")) {
                        long created = Long.valueOf(transactionMap.get("created"));
                        created = created * 1000;

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(created));
                        TwoItemField thisTransaction = new TwoItemField("$" + amount, dateString);
                        transactions.add(thisTransaction);
                    }
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


            }
        });

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_payment;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
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
