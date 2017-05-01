package com.mcsaatchi.gmfit.onboarding.activities;

import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UiResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class LoginActivityPresenter {
  private LoginActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  LoginActivityPresenter(LoginActivityView view, DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void handleFacebookSuccessCallback(AccessToken accessToken) {
    Timber.d("onSuccess: FACEBOOK ACCESS TOKEN IS : %s", accessToken.getToken());

    view.saveFacebookAccessToken(accessToken.getToken());

    GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
      try {

        String userID = (String) object.get("id");
        String userName = (String) object.get("name");
        String userEmail = (String) object.get("email");

        view.saveFacebookUserDetails(userID, userName, userEmail);

        view.callDisplayWaitingDialog(R.string.signing_in_dialog_title);

        registerWithFacebook(accessToken.getToken());
      } catch (JSONException e) {
        e.printStackTrace();
      }
    });

    Bundle parameters = new Bundle();
    parameters.putString("fields", "id,name,email,link,birthday,picture");
    request.setParameters(parameters);
    request.executeAsync();
  }

  private void registerWithFacebook(String accessToken) {
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
          case 201:
            responseBody = response.body().getData().getBody();

            view.saveAccessToken(responseBody.getToken());

            view.openSetupProfileActivity();

            break;
          case 401:
            view.displayWrongCredentialsError();
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
              view.finishActivity();
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

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                view.finishActivity();

                view.openMainActivity(chartsMap);

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  interface LoginActivityView extends BaseActivityPresenter.BaseActivityView {
    void initializeFacebookLogin();

    void openSetupProfileActivity();

    void saveFacebookAccessToken(String accessToken);

    void saveFacebookUserDetails(String userID, String userName, String userEmail);

    void openMainActivity(List<AuthenticationResponseChart> chartsMap);
  }
}
