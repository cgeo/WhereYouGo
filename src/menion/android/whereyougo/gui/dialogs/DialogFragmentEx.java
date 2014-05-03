package menion.android.whereyougo.gui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class DialogFragmentEx extends DialogFragment {

  public DialogFragmentEx() {
    super();
  }

  public boolean shouldRetainInstance() {
    return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // tell the framework to try to keep this fragment around
    // during a configuration change (true), or recreate (false)
    setRetainInstance(shouldRetainInstance());
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = createDialog(savedInstanceState);
    if (dialog != null) {
      dialog.setCancelable(isCancelable());
    }
    return dialog;
  }

  public abstract Dialog createDialog(Bundle savedInstanceState);

  /**
   * This is called when the Fragment's Activity is ready to go, after its content view has been
   * installed; it is called both after the initial fragment creation and after the fragment is
   * re-attached to a new activity.
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    try {
      super.onActivityCreated(savedInstanceState);
    } catch (Exception e) {
      dismissAllowingStateLoss();
    }
  }

  /**
   * This is called right before the fragment is detached from its current activity instance.
   */
  @Override
  public void onDetach() {
    super.onDetach();
  }

  /**
   * This is called when the fragment is going away. It is NOT called when the fragment is being
   * propagated between activity instances.
   */
  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  // hack on this issue http://code.google.com/p/android/issues/detail?id=17423
  // This is to work around what is apparently a bug. If you don't have it
  // here the dialog will be dismissed on rotation, so tell it not to dismiss.
  @Override
  public void onDestroyView() {
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }

  /**
   * My own implementation of visibility check
   */
  public boolean isDialogVisible() {
    return isAdded() && !isHidden();
  }
}
