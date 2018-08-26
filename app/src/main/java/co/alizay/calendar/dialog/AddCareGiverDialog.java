package co.alizay.calendar.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import co.alizay.calendar.R;

public class AddCareGiverDialog extends DialogFragment {

    public interface AddCareGiverDialogListener {
        void onDialogAddClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    AddCareGiverDialogListener addCareGiverDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            addCareGiverDialogListener = (AddCareGiverDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement AddCareGiverDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_add_caregiver, null))
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        addCareGiverDialogListener.onDialogAddClick(AddCareGiverDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addCareGiverDialogListener.onDialogCancelClick(AddCareGiverDialog.this);
                        AddCareGiverDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}