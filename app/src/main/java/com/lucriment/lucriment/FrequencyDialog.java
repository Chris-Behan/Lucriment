package com.lucriment.lucriment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;

/**
 * Created by ChrisBehan on 5/18/2017.
 */

public class FrequencyDialog extends DialogFragment {
    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    final CharSequence[] options = {"Never", "Daily", "Weekly", "Monthly"};
    private   String selection = new String();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Repeate Frequency").setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        selection = (String) options[which];
                        break;
                    case 1:
                        selection = (String) options[which];
                        break;
                    case 2:
                        selection = (String) options[which];
                        break;
                    case 3:
                        selection = (String) options[which];
                        break;

                }

            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
