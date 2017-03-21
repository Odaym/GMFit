package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.RestClient;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class NetworkModule {
  public NetworkModule() {
  }

  @Provides @Singleton static ApiCallsHandler provideApiCallsHandler(Context app) {
    return new ApiCallsHandler(app);
  }

  @Provides @Singleton RestClient providesRestClient(Context app) {
    return new RestClient(app);
  }

  @Provides @Singleton DataAccessHandler provideDataAccessHandler(Context app) {
    return new DataAccessHandler(app);
  }
}
