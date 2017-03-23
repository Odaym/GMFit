package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.onboarding.presenters.SplashActivityPresenter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity
    implements SplashActivityPresenter.SplashActivityView {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject ConnectivityManager connectivityManager;

  private SplashActivityPresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    presenter = new SplashActivityPresenter(this, dataAccessHandler);

    presenter.login(prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false),
        prefs.getString(Constants.EXTRAS_USER_EMAIL, ""),
        prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""),
        prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, ""));
  }

  @Override public void showNoInternetDialog() {
    int NO_INTERNET_DIALOG_TIMEOUT = 3000;

    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.no_internet_conection_dialog_title);
    alertDialog.setMessage(getString(R.string.no_internet_connection_dialog_message));
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.show();

    new Handler().postDelayed(this::finish, NO_INTERNET_DIALOG_TIMEOUT);
  }

  @Override public void showLoginActivity() {
    int SPLASH_TIME_OUT = 1000;

    new Handler().postDelayed(() -> {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
    }, SPLASH_TIME_OUT);
  }

  @Override public void showMainActivity(List<AuthenticationResponseChart> chartsMap) {
    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
        (ArrayList<AuthenticationResponseChart>) chartsMap);
    startActivity(intent);
    finish();
  }

  @Override public void saveAccessToken(String accessToken) {
    prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + accessToken).apply();
  }

  @Override public void showRequestErrorDialog(String responseMessage) {
    Timber.d("Call failed with error : %s", responseMessage);
    final AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
    alertDialog.setMessage(getResources().getString(R.string.server_error_got_returned));
    alertDialog.show();
  }

  @Override public void showWrongCredentialsError() {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
    alertDialog.show();
  }

  @Override public void handleSuccessfulSignUp() {
    EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

    Intent intent = new Intent(SplashActivity.this, SetupProfileActivity.class);
    startActivity(intent);
    finish();
  }

  @Override public boolean checkInternetAvailable() {
    return connectivityManager.getActiveNetworkInfo() != null
        && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
  }
}
