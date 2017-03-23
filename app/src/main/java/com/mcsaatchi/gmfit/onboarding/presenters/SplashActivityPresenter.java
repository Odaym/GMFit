package com.mcsaatchi.gmfit.onboarding.presenters;

import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivityPresenter {
  private SplashActivityView view;
  private DataAccessHandler dataAccessHandler;

  public SplashActivityPresenter(SplashActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void login(boolean loggedIn, String email, String password, String facebookToken) {
    if (!loggedIn) {
      view.showLoginActivity();
    } else {
      if (view.checkInternetAvailable()) {
        if (facebookToken.isEmpty()) {
          signInUserSilently(email, password);
        } else {
          loginWithFacebook(facebookToken);
        }
      } else {
        view.showNoInternetDialog();
      }
    }
  }

  private void signInUserSilently(String email, String password) {
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
        view.showRequestErrorDialog(t.getMessage());
      }
    });
  }

  private void loginWithFacebook(String accessToken) {
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
            view.showWrongCredentialsError();
            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        view.showRequestErrorDialog(t.getMessage());
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
        view.showRequestErrorDialog(t.getMessage());
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

  public interface SplashActivityView {
    void showNoInternetDialog();

    void showLoginActivity();

    void showMainActivity(List<AuthenticationResponseChart> chartsMap);

    void saveAccessToken(String accessToken);

    void showRequestErrorDialog(String responseMessage);

    void showWrongCredentialsError();

    void handleSuccessfulSignUp();

    boolean checkInternetAvailable();
  }
}
