package com.mcsaatchi.gmfit.architecture.classes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.crashlytics.android.Crashlytics;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.architecture.dagger.AppComponent;
import com.mcsaatchi.gmfit.architecture.dagger.AppModule;
import com.mcsaatchi.gmfit.architecture.dagger.DBModule;
import com.mcsaatchi.gmfit.architecture.dagger.DaggerAppComponent;
import com.mcsaatchi.gmfit.architecture.dagger.NetworkModule;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.timber.TimberReleaseTree;
import com.mcsaatchi.gmfit.common.Constants;
import com.onesignal.OneSignal;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;
import net.danlew.android.joda.JodaTimeAndroid;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class GMFitApplication extends Application {

  private static GMFitApplication applicationInstance;
  @Inject SharedPreferences prefs;
  @Inject DataAccessHandlerImpl dataAccessHandler;
  private AppComponent component;

  public static boolean hasNetwork() {
    return applicationInstance.checkIfHasNetwork();
  }

  public static GMFitApplication getInstance() {
    return applicationInstance;
  }

  @Override public void onCreate() {
    super.onCreate();

    component = DaggerAppComponent.builder()
        .appModule(new AppModule(this))
        .dBModule(new DBModule())
        .networkModule(new NetworkModule())
        .build();

    component.inject(this);

    OneSignal.startInit(this).init();

    OneSignal.idsAvailable((userId, registrationId) -> updateOneSignalToken(userId));

    JodaTimeAndroid.init(this);

    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    applicationInstance = this;

    Fabric.with(this, new Crashlytics());

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + " - " + element.getLineNumber();
        }
      });
    } else {
      Timber.plant(new TimberReleaseTree());
    }
  }

  private void updateOneSignalToken(String userOneSignalId) {
    dataAccessHandler.updateOneSignalToken(userOneSignalId, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  public AppComponent getAppComponent() {
    return component;
  }

  public boolean checkIfHasNetwork() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  public String getBaseURL(){
    return "https://mobileapp.globemedfit.com/api/v1/";
  }
}
