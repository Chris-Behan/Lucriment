package com.lucriment.lucriment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ChrisBehan on 6/9/2017.
 */

public class CancelDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onCancelPositiveClick(DialogFragment dialog);

        public void onCancelNegativeClick(DialogFragment dialog);
    }

    DeclineDialogFragment.NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeclineDialogFragment.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("By cancelling this request it will be removed from your pendings sessions,do you still wish to Cancel?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDeclinePositiveClick(CancelDialogFragment.this);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDeclineNegativeClick(CancelDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }
}
