package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.data_access.DBHelper;

public class Base_Activity extends AppCompatActivity {
  private DBHelper dbHelper = null;

  public void setupToolbar(Toolbar toolbar, int toolbarTitleResId, boolean backEnabled) {
    if (toolbarTitleResId == 0) {
      toolbar.setTitle(R.string.app_name);
    } else {
      toolbar.setTitle(toolbarTitleResId);
    }

    setSupportActionBar(toolbar);

    if (backEnabled) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
    }

    toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleTextStyle);
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

  public void addTopPaddingToolbar(Toolbar toolbar) {
    toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
  }

  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
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
}