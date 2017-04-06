package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SignUpActivityPresenter {
  private SignUpActivityView view;
  private DataAccessHandler dataAccessHandler;

  SignUpActivityPresenter(SignUpActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void signUserUp(final String full_name, final String email, final String password) {
    view.callDisplayWaitingDialog(R.string.signing_up_dialog_title);

    dataAccessHandler.registerUser(full_name, email, password,
        new Callback<AuthenticationResponse>() {
          @Override public void onResponse(Call<AuthenticationResponse> call,
              Response<AuthenticationResponse> response) {
            switch (response.code()) {
              case 200:
                AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

                view.saveUserSignUpDetails(responseBody.getToken(), full_name, email, password);

                view.openAccountVerificationActivity();

                break;
              case 449:
                view.showEmailTakenErrorDialog();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface SignUpActivityView
      extends BaseActivityPresenter.BaseActivityView {
    void saveUserSignUpDetails(String accessToken, String full_name, String email, String password);

    void openAccountVerificationActivity();

    void showEmailTakenErrorDialog();
  }
}
