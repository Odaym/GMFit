package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class SignInActivityPresenter {
  private SignInActivityView view;
  private DataAccessHandler dataAccessHandler;

  SignInActivityPresenter(SignInActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void signIn(String email, String password) {
    view.callDisplayWaitingDialog(R.string.signing_in_dialog_title);

    dataAccessHandler.signInUser(email, password, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        switch (response.code()) {
          case 200:
            AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

            view.saveUserSignInDetails(responseBody.getToken(), email, password);

            getOnboardingStatus();

            break;
          case 401:
            view.displayWrongCredentialsError();
            break;
          case 403:
            view.openAccountVerificationActivity();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  private void getOnboardingStatus() {
    dataAccessHandler.getOnboardingStatus(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {

        switch (response.code()) {
          case 200:
            String userOnBoard = response.body().getData().getBody().getData().getOnboard();

            if (userOnBoard.equals("1")) {
              getUiForSection("fitness");
            } else {
              view.openSetupProfileActivity();
            }

            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  private void getUiForSection(String section) {
    dataAccessHandler.getUiForSection(Constants.BASE_URL_ADDRESS + "user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                view.callDismissWaitingDialog();

                EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                view.openMainActivity(chartsMap);

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  interface SignInActivityView extends BaseActivityPresenter.BaseActivityView {
    void saveUserSignInDetails(String accessToken, String email, String password);

    void openAccountVerificationActivity();

    void openSetupProfileActivity();

    void openMainActivity(List<AuthenticationResponseChart> chartsMap);
  }
}
