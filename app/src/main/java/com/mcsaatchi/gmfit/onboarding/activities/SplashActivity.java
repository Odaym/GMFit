package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private Intent intent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    int SPLASH_TIME_OUT = 1000;
    int NO_INTERNET_DIALOG_TIMEOUT = 3000;

    /**
     * User is not logged in
     */
    if (!prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
      Timber.d("User not logged in");

      new Handler().postDelayed(new Runnable() {

        @Override public void run() {
          intent = new Intent(SplashActivity.this, LoginActivity.class);
          startActivity(intent);
        }
      }, SPLASH_TIME_OUT);

      /**
       * User is logged in and they did finish setting up their profile
       */
    } else if (prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
      if (Helpers.isInternetAvailable(SplashActivity.this)) {
        if (prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
          signInUserSilently(prefs.getString(Constants.EXTRAS_USER_EMAIL, ""),
              prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""));
          Timber.d("Logging in user normally");
        } else {
          loginUserWithFacebook(prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1"));
          Timber.d("Logging in user with Facebook");
        }
      } else {
        Helpers.showNoInternetDialog(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            finish();
          }
        }, NO_INTERNET_DIALOG_TIMEOUT);
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

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            getOnboardingStatus();

            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
        alertDialog.setMessage(
            getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void loginUserWithFacebook(String accessToken) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    dataAccessHandler.handleFacebookProcess(accessToken, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        AuthenticationResponseInnerBody responseBody;

        switch (response.code()) {
          case 200:

            responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            getOnboardingStatus();

            break;
          case 401:
            alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
            alertDialog.show();
            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  public void getOnboardingStatus() {
    dataAccessHandler.getOnboardingStatus(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {

        Intent intent;

        switch (response.code()) {
          case 200:
            String userOnBoard = response.body().getData().getBody().getData().getOnboard();

            if (userOnBoard.equals("1")) {
              getUiForSection("fitness");
            } else {
              EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

              intent = new Intent(SplashActivity.this, SetupProfileActivity.class);
              startActivity(intent);
              finish();
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
    dataAccessHandler.getUiForSection("http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                    (ArrayList<AuthenticationResponseChart>) chartsMap);
                startActivity(intent);

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }
}
