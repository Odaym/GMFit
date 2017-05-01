package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UiResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class SplashActivityPresenter {
  private SplashActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SplashActivityPresenter(SplashActivityView view, DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void login(String email, String password) {
    if (view.checkInternetAvailable()) {
      signInUserSilently(email, password);
    } else {
      view.displayNoInternetDialog();
    }
  }

  void login(String facebookToken) {
    if (view.checkInternetAvailable()) {
      loginWithFacebook(facebookToken);
    } else {
      view.displayNoInternetDialog();
    }
  }

  public void signInUserSilently(String email, String password) {
    dataAccessHandler.signInUserSilently(email, password, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        AuthenticationResponseInnerBody responseBody;

        switch (response.code()) {
          case 200:
            responseBody = response.body().getData().getBody();

            view.saveAccessToken(responseBody.getToken());

            getOnboardingStatus();

            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void loginWithFacebook(String accessToken) {
    dataAccessHandler.handleFacebookProcess(accessToken, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        AuthenticationResponseInnerBody responseBody;

        switch (response.code()) {
          case 200:
            responseBody = response.body().getData().getBody();

            view.saveAccessToken(responseBody.getToken());

            getOnboardingStatus();

            break;
          case 401:
            view.displayWrongCredentialsError();
            break;
        }
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
              view.handleSuccessfulSignUp();
            }

            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  private void getUiForSection(String section) {
    dataAccessHandler.getUiForSection(Constants.BASE_URL_ADDRESS + "user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                view.showMainActivity(chartsMap);

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  interface SplashActivityView extends BaseActivityPresenter.BaseActivityView {
    void showLoginActivity();

    void showMainActivity(List<AuthenticationResponseChart> chartsMap);

    void displayWrongCredentialsError();

    void handleSuccessfulSignUp();
  }
}
