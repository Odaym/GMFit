package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.onboarding.presenters.SplashActivityPresenter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SplashActivity extends BaseActivity
    implements SplashActivityPresenter.SplashActivityView {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject ConnectivityManager connectivityManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    SplashActivityPresenter presenter = new SplashActivityPresenter(this, dataAccessHandler);

    presenter.login(prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false),
        prefs.getString(Constants.EXTRAS_USER_EMAIL, ""),
        prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""),
        prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, ""));
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
