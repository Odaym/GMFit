package com.mcsaatchi.gmfit.common.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import javax.inject.Inject;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {
  @Inject public SharedPreferences prefs;
  @Inject public DataAccessHandler dataAccessHandler;
  @Inject public DBHelper dbHelper;

  private FirebaseAnalytics firebaseAnalytics;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    Timber.d("Firebase object initialised");
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

  public void setupToolbar(String activityName, Toolbar toolbar, String toolbarTitle,
      boolean backEnabled) {
    Timber.d("Firebase event logged");

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
}