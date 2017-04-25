package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.classes.PermissionsChecker;
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

  @Provides @Singleton SharedPreferences providesSharedPreferences(Context app) {
    return app.getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
  }

  @Provides @Singleton PermissionsChecker providePermissionsChecker(Context app) {
    return new PermissionsChecker(app);
  }

  @Provides LocalDate provideLocalDate() {
    return new LocalDate();
  }

  @Provides @Singleton ConnectivityManager providesConnectivityManager(Context context) {
    return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }
}