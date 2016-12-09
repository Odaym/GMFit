package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFit_Application;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Poster;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Singleton;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Splash_Activity extends AppCompatActivity {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private Intent intent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ((GMFit_Application) getApplication()).getAppComponent().inject(this);

    int SPLASH_TIME_OUT = 1000;
    int NO_INTERNET_DIALOG_TIMEOUT = 3000;

    Timber.d("onCreate outside if : %s", prefs.getBoolean(
        prefs.getString(Constants.EXTRAS_USER_EMAIL, "")
            + "_"
            + Constants.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, false));

    /**
     * User is not logged in
     */
    if (!prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
      Timber.d("User not logged in");

      new Handler().postDelayed(new Runnable() {

        @Override public void run() {
          intent = new Intent(Splash_Activity.this, Login_Activity.class);
          startActivity(intent);
        }
      }, SPLASH_TIME_OUT);

      /**
       * User did not finish setting up profile
       */
    } else if (!prefs.getBoolean(prefs.getString(Constants.EXTRAS_USER_EMAIL, "")
        + "_"
        + Constants.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, false)) {

      Timber.d("onCreate: user has not yet finished setup profile process");
      intent = new Intent(Splash_Activity.this, SetupProfile_Activity.class);
      startActivity(intent);

      /**
       * User is logged in and they did finish setting up their profile
       */
    } else if (prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
      if (Helpers.isInternetAvailable(Splash_Activity.this)) {
        Timber.d("onCreate: signing the user in silently now");

        if (prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
          signInUserSilently(prefs.getString(Constants.EXTRAS_USER_EMAIL, ""),
              prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""));
        } else {
          loginUserWithFacebook(prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1"));
        }
      } else {
        Helpers.showNoInternetDialog(Splash_Activity.this);
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
        switch (response.code()) {
          case 200:
            AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            /**
             * Don't send the widgets over to the Main Activity here
             */
            List<AuthenticationResponseChart> chartsMap = responseBody.getCharts();

            Intent intent = new Intent(Splash_Activity.this, Main_Activity.class);
            intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                (ArrayList<AuthenticationResponseChart>) chartsMap);
            startActivity(intent);

            break;
        }
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog =
            new AlertDialog.Builder(Splash_Activity.this).create();
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
        switch (response.code()) {
          case 200:

            AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();

            List<AuthenticationResponseChart> chartsMap = responseBody.getCharts();

            EventBus_Singleton.getInstance()
                .post(new EventBus_Poster(
                    Constants.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

            Intent intent = new Intent(Splash_Activity.this, Main_Activity.class);
            intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                (ArrayList<AuthenticationResponseChart>) chartsMap);
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
        alertDialog.setMessage(
            getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }
}
