package com.lucriment.lucriment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.SourceCallback;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by ChrisBehan on 7/14/2017.
 */

public class AddPaymentMethodDialog extends DialogFragment {
    private UserInfo userInfo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Bundle args = getArguments();
        userInfo = args.getParcelable("userInfo");


        View view = inflater.inflate(R.layout.addpaymentmethod,null);
      //  final CardInputWidget mCardInputWidget = (CardInputWidget) view.findViewById(R.id.card_input_widget);
        final CardForm cardForm = (CardForm) view.findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)

                .setup(getActivity());
        builder.setView(view)

                // Add action buttons
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog dialog1  = (Dialog) dialog;
                        Context context = dialog1.getContext();
                        Stripe stripe = new Stripe(context, "pk_test_kRaU4qwEDlsbB9HL0JeAPFmP");
                        DatabaseReference tokenref = FirebaseDatabase.getInstance().getReference().child("users").child(userInfo.getId()).child("paymentInfo");
                        HashMap<String, Object> cardMap = new HashMap<String, Object>();
                        cardMap.put("object","card");
                       // cardMap.put("postal_code", cardForm.getPostalCode());
                        cardMap.put("number",cardForm.getCardNumber());
                        cardMap.put("exp_month",cardForm.getExpirationMonth());
                        cardMap.put("exp_year",cardForm.getExpirationYear());
                        Card card = new Card(cardForm.getCardNumber(), Integer.valueOf(cardForm.getExpirationMonth()),
                                Integer.valueOf(cardForm.getExpirationYear()), cardForm.getCvv());
                        if(card.validateCVC()&&card.validateCard()) {
                            tokenref.removeValue();
                            tokenref.push().child("token").setValue(cardMap);
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"Invalid Card Information",Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPaymentMethodDialog.this.getDialog().cancel();
                    }
                });



        return builder.create();
    }
}
