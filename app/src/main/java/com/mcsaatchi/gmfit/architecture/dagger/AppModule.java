package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.GMFit_Application;
import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.joda.time.LocalDate;

@Module public class AppModule {
  private final GMFit_Application application;

  public AppModule(GMFit_Application app) {
    this.application = app;
  }

  @Provides @Singleton Context providesApplicationContext() {
    return application;
  }

  @Provides @Singleton Resources provideActivityResources(Context app) {
    return app.getResources();
  }

  @Provides @Singleton ApiCallsHandler provideApiCallsHandler(Context app) {
    return new ApiCallsHandler(app);
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
}