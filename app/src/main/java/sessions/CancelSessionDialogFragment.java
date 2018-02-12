package sessions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ChrisBehan on 8/21/2017.
 */

public class CancelSessionDialogFragment extends DialogFragment {
    private String dialogMessage = "By cancelling this session request it will be removed from your requested sessions and the tutors session requests, do you still wish to cancel the session request?";
    private boolean customDialog = false;
    public interface NoticeDialogListener {
        public void onDeclinePositiveClick(DialogFragment dialog);
        public void onDeclineNegativeClick(DialogFragment dialog);
    }

    CancelSessionDialogFragment.NoticeDialogListener mListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CancelSessionDialogFragment.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(!customDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(dialogMessage)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDeclinePositiveClick(CancelSessionDialogFragment.this);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDeclineNegativeClick(CancelSessionDialogFragment.this);
                        }
                    });
            return builder.create();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(dialogMessage)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDeclinePositiveClick(CancelSessionDialogFragment.this);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDeclineNegativeClick(CancelSessionDialogFragment.this);
                        }
                    });
            return builder.create();
        }
        // Create the AlertDialog object and return it

    }

    public void dialogMessage(String message){
        dialogMessage = message;
        customDialog = true;
    }
}
