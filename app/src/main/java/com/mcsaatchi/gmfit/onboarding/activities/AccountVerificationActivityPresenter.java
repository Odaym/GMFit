package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AccountVerificationActivityPresenter {
  private AccountVerificationActivityView view;
  private DataAccessHandler dataAccessHandler;

  AccountVerificationActivityPresenter(AccountVerificationActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void setupProfile(String verificationCode){
    if (view.checkInternetAvailable()){
      verifyRegistrationCode(verificationCode);
    }else{
      view.displayNoInternetDialog();
    }
  }

  private void verifyRegistrationCode(String verificationCode) {
    view.callDisplayWaitingDialog(R.string.verifying_email_dialog_title);

    dataAccessHandler.verifyUser(verificationCode, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.openSetupProfileActivity();
            break;
          case 401:
            view.displayWrongCodeDialog();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface AccountVerificationActivityView extends BaseActivityPresenter.BaseActivityView {
    void openSetupProfileActivity();

    void displayWrongCodeDialog();
  }
}
