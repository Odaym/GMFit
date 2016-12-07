package com.mcsaatchi.gmfit.architecture;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.crashlytics.android.Crashlytics;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.architecture.dagger.AppComponent;
import com.mcsaatchi.gmfit.architecture.dagger.AppModule;
import com.mcsaatchi.gmfit.architecture.dagger.DaggerAppComponent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.architecture.timber.TimberReleaseTree;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class GMFit_Application extends Application {

  private static GMFit_Application instance;

  private AppComponent component;

  private SharedPreferences prefs;

  public static boolean hasNetwork() {
    return instance.checkIfHasNetwork();
  }

  public static GMFit_Application getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();

    initDagger();

    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    instance = this;

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + " - " + element.getLineNumber();
        }
      });
    } else {
      Timber.plant(new TimberReleaseTree());
      Fabric.with(this, new Crashlytics());
    }

    addMealAlarms();
  }

  private void initDagger() {
    component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

    component.inject(this);
  }

  public AppComponent getAppComponent() {
    return component;
  }

  public boolean checkIfHasNetwork() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  private void addMealAlarms() {
    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    if (prefs.getBoolean(Constants.APP_FIRST_RUN, true)) {
      prefs.edit().putBoolean(Constants.APP_FIRST_RUN, false).apply();
      Helpers.setupAlarmForMeal(this, prefs, "Breakfast");
      Helpers.setupAlarmForMeal(this, prefs, "Lunch");
      Helpers.setupAlarmForMeal(this, prefs, "Dinner");
    } else {
      Timber.d("App has run before");
    }
  }
}
