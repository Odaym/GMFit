package com.mcsaatchi.gmfit.common.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.presenters.BaseFragmentPresenter;
import timber.log.Timber;

public class BaseFragment extends Fragment implements BaseFragmentPresenter.BaseFragmentView {
  private ProgressDialog waitingDialog;

  @Override public void displayRequestErrorDialog(String responseMessage) {
    Timber.d("Call failed with error : %s", responseMessage);
    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setMessage(getResources().getString(R.string.server_error_got_returned));
    alertDialog.show();
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
