package menion.android.whereyougo.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import menion.android.whereyougo.R;

public class NoPasswordDialogFragment extends DialogFragment {

    private NoPasswordDialogListener listener;

    public interface NoPasswordDialogListener {
        void onPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NoPasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement NoPasswordDialogListener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_no_password)
                .setPositiveButton(R.string.ok, (dialog, id) -> listener.onPositiveClick(NoPasswordDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog --> Do nothing
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
