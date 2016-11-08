package com.mcsaatchi.gmfit.classes;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class GMFit_Application extends Application {

  private static GMFit_Application instance;

  public static boolean hasNetwork() {
    return instance.checkIfHasNetwork();
  }

  public static GMFit_Application getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();

    instance = this;

    Fabric.with(this, new Crashlytics());
  }

  public boolean checkIfHasNetwork() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }
}
