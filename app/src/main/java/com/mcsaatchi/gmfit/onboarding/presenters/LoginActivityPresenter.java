package com.mcsaatchi.gmfit.onboarding.presenters;

import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivityPresenter {
  private LoginActivityView view;
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

        switch (response.code()) {
          case 200:
            responseBody = response.body().getData().getBody();

            ((BaseActivity) view).saveAccessToken(responseBody.getToken());

            getOnboardingStatus();

            break;
          case 201:
            responseBody = response.body().getData().getBody();

            ((BaseActivity) view).saveAccessToken(responseBody.getToken());

            view.openSetupProfileActivity();

            break;
          case 401:
            ((BaseActivity) view).showWrongCredentialsError();
            break;
        }

        view.hideWaitingDialog();
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        ((BaseActivity) view).showRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getOnboardingStatus() {
    dataAccessHandler.getOnboardingStatus(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {

        switch (response.code()) {
          case 200:
            String userOnBoard = response.body().getData().getBody().getData().getOnboard();

            if (userOnBoard.equals("1")) {
              getUiForSection("fitness");
            } else {
              ((BaseActivity) view).finishActivity();
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
                view.hideWaitingDialog();

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                ((BaseActivity) view).finishActivity();

                view.openMainActivity(chartsMap);

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  public interface LoginActivityView {
    void initializeFacebookLogin();

    void hideWaitingDialog();

    void openSetupProfileActivity();

    void openMainActivity(List<AuthenticationResponseChart> chartsMap);
  }
}
