package com.mcsaatchi.gmfit.common.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.PermissionsChecker;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.common.Constants;
import javax.inject.Inject;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity
    implements BaseActivityPresenter.BaseActivityView {
  @Inject public SharedPreferences prefs;
  @Inject public ConnectivityManager connectivityManager;
  @Inject public DataAccessHandler dataAccessHandler;
  @Inject public DBHelper dbHelper;
  @Inject public PermissionsChecker permChecker;

  private FirebaseAnalytics firebaseAnalytics;

  private ProgressDialog waitingDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        super.onBackPressed();
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      super.onBackPressed();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (dbHelper != null) {
      OpenHelperManager.releaseHelper();
      dbHelper = null;
    }
  }

  @Override public void finishActivity() {
    finish();
  }

  public void setupToolbar(String activityName, Toolbar toolbar, String toolbarTitle,
      boolean backEnabled) {

    Bundle params = new Bundle();
    params.putString("activity_name", activityName);
    firebaseAnalytics.logEvent("activity_visited", params);

    if (toolbarTitle == null) {
      toolbar.setTitle(R.string.app_name);
    } else {
      toolbar.setTitle(toolbarTitle);
    }

    setSupportActionBar(toolbar);

    if (backEnabled) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
    }

    toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleTextStyle);
  }

  @Override public void displayNoInternetDialog() {
    int NO_INTERNET_DIALOG_TIMEOUT = 3000;

    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.no_internet_conection_dialog_title);
    alertDialog.setMessage(getString(R.string.no_internet_connection_dialog_message));
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.show();

    new Handler().postDelayed(this::finish, NO_INTERNET_DIALOG_TIMEOUT);
  }

  @Override public void saveAccessToken(String accessToken) {
    prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + accessToken).apply();
  }

  @Override public void displayRequestErrorDialog(String responseMessage) {
    Timber.d("Call failed with error : %s", responseMessage);
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.error_occurred_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.setMessage(responseMessage);
    alertDialog.show();
  }

  @Override public void displayWrongCredentialsError() {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
    alertDialog.show();
  }

  @Override public boolean checkInternetAvailable() {
    return connectivityManager.getActiveNetworkInfo() != null
        && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
  }

  @Override public ProgressDialog displayWaitingDialog(String dialogTitle) {
    ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(dialogTitle);
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    return waitingDialog;
  }

  @Override public void dismissWaitingDialog(ProgressDialog waitingDialog) {
    waitingDialog.dismiss();
  }

  @Override public void callDisplayWaitingDialog(int dialogTitleResource) {
    waitingDialog = displayWaitingDialog(getString(dialogTitleResource));
  }

  @Override public void callDismissWaitingDialog() {
    dismissWaitingDialog(waitingDialog);
  }
}