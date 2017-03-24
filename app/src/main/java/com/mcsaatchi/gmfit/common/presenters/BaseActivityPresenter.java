package com.mcsaatchi.gmfit.common.presenters;

public class BaseActivityPresenter {
  public interface BaseActivityView {
    void finishActivity();

    void showNoInternetDialog();

    void saveAccessToken(String accessToken);

    void showRequestErrorDialog(String responseMessage);

    void showWrongCredentialsError();

    boolean checkInternetAvailable();
  }
}
