package com.mcsaatchi.gmfit.common.presenters;

import android.app.ProgressDialog;

public class BaseFragmentPresenter {
  public interface BaseFragmentView {
    void displayRequestErrorDialog(String responseMessage);

    ProgressDialog displayWaitingDialog(String dialogTitle);

    void dismissWaitingDialog(ProgressDialog waitingDialog);

    void callDisplayWaitingDialog(int dialogTitleResource);

    void callDismissWaitingDialog();
  }
}
