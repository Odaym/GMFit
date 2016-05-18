package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.DBHelper;

public class Base_Activity extends AppCompatActivity {
    private DBHelper dbHelper = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null && savedInstanceState != null) {
            if (savedInstanceState.get(Constants.BUNDLE_ACTIVITY_TITLE) != null && savedInstanceState.get(Constants.BUNDLE_ACTIVITY_TITLE) instanceof String) {
                //String was passed
                getSupportActionBar().setTitle(savedInstanceState.getString(Constants.BUNDLE_ACTIVITY_TITLE));
            } else if (savedInstanceState.get(Constants.BUNDLE_ACTIVITY_TITLE) != null) {
                //Integer was passed
                getSupportActionBar().setTitle(getString(savedInstanceState.getInt(Constants.BUNDLE_ACTIVITY_TITLE)));
            } else {
                //Null was passed
                getSupportActionBar().setTitle(getString(R.string.app_name));
            }

            //Set back button only on applicable activities
            if (savedInstanceState.get(Constants.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED) != null && savedInstanceState.getBoolean(Constants.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED)) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    public DBHelper getHelper() {
        if (dbHelper == null) {
            dbHelper =
                    OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return dbHelper;
    }
}