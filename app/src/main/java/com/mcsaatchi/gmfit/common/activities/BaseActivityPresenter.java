package com.mcsaatchi.gmfit.common.activities;

import android.app.ProgressDialog;

public class BaseActivityPresenter {
  public interface BaseActivityView {
    void finishActivity();

    void saveAccessToken(String accessToken);

    boolean checkInternetAvailable();

    void displayNoInternetDialog();

    void displayRequestErrorDialog(String responseMessage);

    void displayWrongCredentialsError();

    ProgressDialog displayWaitingDialog(String dialogTitle);

    void dismissWaitingDialog(ProgressDialog waitingDialog);

    void callDisplayWaitingDialog(int dialogTitleResource);

    void callDismissWaitingDialog();
  }
}
