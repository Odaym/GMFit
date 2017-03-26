package com.mcsaatchi.gmfit.onboarding.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountVerificationActivityPresenter {
  private AccountVerificationActivityView view;
  private DataAccessHandler dataAccessHandler;

  public AccountVerificationActivityPresenter(AccountVerificationActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public interface AccountVerificationActivityView extends BaseActivityPresenter.BaseActivityView {
    void openSetupProfileActivity();

    void displayWrongCodeDialog();
  }

  public void setupProfile(String verificationCode){
    if (view.checkInternetAvailable()){
      verifyRegistrationCode(verificationCode);
    }else{
      view.showNoInternetDialog();
    }
  }

  private void verifyRegistrationCode(String verificationCode) {
    view.callShowWaitingDialog(R.string.verifying_email_dialog_title);

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

        view.callDismissDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.showRequestErrorDialog(t.getMessage());
      }
    });
  }
}
