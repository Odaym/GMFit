package com.mcsaatchi.gmfit.common.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import com.mcsaatchi.gmfit.R;
import timber.log.Timber;

public class BaseFragment extends Fragment implements BaseFragmentPresenter.BaseFragmentView {
  private ProgressDialog waitingDialog;

  @Override public void displayRequestErrorDialog(String responseMessage) {
    Timber.d("Call failed with error : %s", responseMessage);
    if (getActivity() != null) {
      final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
          (dialog, which) -> dialog.dismiss());
      alertDialog.setTitle(R.string.error_occurred_dialog_title);
      alertDialog.setMessage(responseMessage);
      alertDialog.show();
    }
  }

  @Override public ProgressDialog displayWaitingDialog(String dialogTitle) {
    ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(dialogTitle);
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    return waitingDialog;
  }

  @Override public void dismissWaitingDialog(ProgressDialog waitingDialog) {
    waitingDialog.dismiss();
  }

  @Override public void callDisplayWaitingDialog(int dialogTitleResource) {
    waitingDialog = displayWaitingDialog(getString(dialogTitleResource));
  }

  @Override public void callDismissWaitingDialog() {
    dismissWaitingDialog(waitingDialog);
  }
}
