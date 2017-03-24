package com.mcsaatchi.gmfit.common.presenters;

import android.app.ProgressDialog;

public class BaseActivityPresenter {
  public interface BaseActivityView {
    void finishActivity();

    void showNoInternetDialog();

    void saveAccessToken(String accessToken);

    void showRequestErrorDialog(String responseMessage);

    void showWrongCredentialsError();

    boolean checkInternetAvailable();

    ProgressDialog showWaitingDialog(String dialogTitle);

    void dismissWaitingDialog(ProgressDialog waitingDialog);

    void callShowWaitingDialog(int dialogTitleResource);

    void callDismissDialog();
  }
}
