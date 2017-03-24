package com.mcsaatchi.gmfit.onboarding.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivityPresenter {
  private ForgotPasswordActivityView view;
  private DataAccessHandler dataAccessHandler;

  public interface ForgotPasswordActivityView extends BaseActivityPresenter.BaseActivityView {
    void openResetPasswordActivity();
  }

  public ForgotPasswordActivityPresenter(ForgotPasswordActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void handleForgotPassword(String email) {
    view.callShowWaitingDialog(R.string.sending_reset_password_dialog_title);

    dataAccessHandler.sendResetPasswordLink(email, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.openResetPasswordActivity();
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
