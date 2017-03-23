package com.mcsaatchi.gmfit.onboarding.presenters;

import android.content.Intent;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.onboarding.activities.LoginActivity;
import com.mcsaatchi.gmfit.onboarding.activities.SetupProfileActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivityPresenter {
  LoginActivityView view;
  private DataAccessHandler dataAccessHandler;

  public LoginActivityPresenter(LoginActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void registerWithFacebook(String accessToken) {
    dataAccessHandler.handleFacebookProcess(accessToken, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        AuthenticationResponseInnerBody responseBody;

        Intent intent;

        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            getOnboardingStatus(waitingDialog);

            break;
          case 201:
            waitingDialog.dismiss();

            responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
            startActivity(intent);

            finish();

            break;
          case 401:
            alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
            alertDialog.show();
            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getResources().getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  public interface LoginActivityView {
    void initializeFacebookLogin();
  }
}
