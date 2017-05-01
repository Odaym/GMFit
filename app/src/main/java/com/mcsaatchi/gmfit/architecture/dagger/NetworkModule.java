package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.RestClient;
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

  @Provides @Singleton DataAccessHandlerImpl provideDataAccessHandler(Context app) {
    return new DataAccessHandlerImpl(app);
  }
}
