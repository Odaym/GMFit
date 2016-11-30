package com.mcsaatchi.gmfit.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.GMFit_Application;
import com.mcsaatchi.gmfit.data_access.DBHelper;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import javax.inject.Inject;

public class Base_Activity extends AppCompatActivity {
  @Inject SharedPreferences prefs;
  @Inject DataAccessHandler dataAccessHandler;
  private DBHelper dbHelper = null;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((GMFit_Application) getApplication()).getAppComponent().inject(this);
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

  public DBHelper getDBHelper() {
    if (dbHelper == null) {
      dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
    }
    return dbHelper;
  }

  public void setupToolbar(Toolbar toolbar, String toolbarTitle, boolean backEnabled) {
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