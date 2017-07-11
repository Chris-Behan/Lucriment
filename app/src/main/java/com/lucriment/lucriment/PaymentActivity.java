package com.lucriment.lucriment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

public class PaymentActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private String userType;
    private Button addPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }

        // INITIALIZE CARD WIDGET
        final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        addPaymentButton = (Button) findViewById(R.id.addPayment);



        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    Toast.makeText(PaymentActivity.this,"Invalid Card Info",Toast.LENGTH_LONG).show();
                }
                

                Stripe stripe = new Stripe(PaymentActivity.this, "pk_test_Mewqx841ZRYF97i3eECg4H9u");
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

            }
        });

        /*

        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        DatabaseReference tokenref = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("paymentInfo");
                        tokenref.setValue(token);
                        // Send token to your server
                    }
                    public void onError(Exception error) {
                        // Show localized error message

                    }
                }
        ); */

    }

}
