package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.PermissionsChecker;
import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.RestClient;
import com.mcsaatchi.gmfit.common.Constants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.joda.time.LocalDate;

@Module public class AppModule {
  private final GMFitApplication application;

  public AppModule(GMFitApplication app) {
    this.application = app;
  }

  @Provides @Singleton Context providesApplicationContext() {
    return application;
  }

  @Provides @Singleton Resources provideActivityResources(Context app) {
    return app.getResources();
  }

  @Provides DBHelper providesDBHelper(Context app) {
    return OpenHelperManager.getHelper(app, DBHelper.class);
  }

  @Provides @Singleton ApiCallsHandler provideApiCallsHandler(Context app) {
    return new ApiCallsHandler(app);
  }

  @Provides @Singleton RestClient providesRestClient(Context app) {
    return new RestClient(app);
  }

  @Provides @Singleton SharedPreferences providesSharedPreferences(Context app) {
    return app.getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
  }

  @Provides LocalDate provideLocalDate() {
    return new LocalDate();
  }

  @Provides @Singleton DataAccessHandler provideDataAccessHandler(Context app) {
    return new DataAccessHandler(app);
  }

  @Provides @Singleton PermissionsChecker providePermissionsChcecker(Context app) {
    return new PermissionsChecker(app);
  }
}