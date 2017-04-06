package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ForgotPasswordActivityPresenter {
  private ForgotPasswordActivityView view;
  private DataAccessHandler dataAccessHandler;

  ForgotPasswordActivityPresenter(ForgotPasswordActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void handleForgotPassword(String email) {
    view.callDisplayWaitingDialog(R.string.sending_reset_password_dialog_title);

    dataAccessHandler.sendResetPasswordLink(email, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.openResetPasswordActivity();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface ForgotPasswordActivityView extends BaseActivityPresenter.BaseActivityView {
    void openResetPasswordActivity();
  }
}
