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
    private int message;

    public interface NoPasswordDialogListener {
        void onPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getInt("message");
        } else {
            throw new IllegalArgumentException("This dialog needs to be handed a Bundle as an arguement with the key \"message\" which is a Android string resource int.");
        }
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
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, id) -> listener.onPositiveClick(NoPasswordDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog --> Do nothing
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
