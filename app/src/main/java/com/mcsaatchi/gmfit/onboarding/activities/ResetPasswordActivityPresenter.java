package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ResetPasswordActivityPresenter {
  private BaseActivityPresenter.BaseActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ResetPasswordActivityPresenter(BaseActivityPresenter.BaseActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void resetPassword(String token, String newPassword) {
    if (view.checkInternetAvailable()) {
      view.callDisplayWaitingDialog(R.string.resetting_password_dialog_title);

      dataAccessHandler.finalizeResetPassword(token, newPassword,
          new Callback<DefaultGetResponse>() {
            @Override public void onResponse(Call<DefaultGetResponse> call,
                Response<DefaultGetResponse> response) {
              switch (response.code()) {
                case 200:
                  view.callDismissWaitingDialog();
                  view.finishActivity();

                  break;
              }
            }

            @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
              view.displayRequestErrorDialog(t.getMessage());
            }
          });
    }
  }
}
