package com.lucriment.lucriment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ChrisBehan on 7/5/2017.
 */

public class daySelectDialog extends DialogFragment {

    public String getSelection() {
        return selection;
    }


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    daySelectDialog.NoticeDialogListener mListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (daySelectDialog.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    //  final CharSequence[] options = {"Never", "Daily", "Weekly", "Monthly"};
    private   String selection = new String();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        final String[] options = args.getStringArray("days");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Day").setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection = (String) options[which];
              /*  switch (which){
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

                } */

            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(daySelectDialog.this);
            }
        });

        return builder.create();
    }
}
