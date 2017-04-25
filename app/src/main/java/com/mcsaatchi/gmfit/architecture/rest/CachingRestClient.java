package com.mcsaatchi.gmfit.architecture.rest;

import android.util.Log;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.net.http.Headers.CACHE_CONTROL;

public class CachingRestClient {
  private static final String TAG = "CachingRestClient";
  private GMFitService apiService;

  public CachingRestClient() {

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    httpClient.addInterceptor(loggingInterceptor);
    //        httpClient.addInterceptor(provideOfflineCacheInterceptor());
    //        httpClient.addNetworkInterceptor(provideCacheInterceptor());
    //        httpClient.cache(provideCache());

    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL_ADDRESS)
        .client(httpClient.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    apiService = retrofit.create(GMFitService.class);
  }

  private static Cache provideCache() {
    Cache cache = null;
    try {
      cache = new Cache(new File(GMFitApplication.getInstance().getCacheDir(), "http-cache"),
          10 * 1024 * 1024);
    } catch (Exception e) {
      Log.d(TAG, "provideCache: Could not create cache!");
    }
    return cache;
  }

  private static Interceptor provideCacheInterceptor() {
    return chain -> {
      Response response = chain.proceed(chain.request());

      // re-write response header to force use of cache
      CacheControl cacheControl = new CacheControl.Builder().maxAge(1, TimeUnit.MINUTES).build();

      return response.newBuilder().header(CACHE_CONTROL, cacheControl.toString()).build();
    };
  }

  private static Interceptor provideOfflineCacheInterceptor() {
    return chain -> {
      Request request = chain.request();

      if (!GMFitApplication.hasNetwork()) {
        CacheControl cacheControl =
            new CacheControl.Builder().maxStale(1, TimeUnit.MINUTES).build();

        request = request.newBuilder().cacheControl(cacheControl).build();
      }

      return chain.proceed(request);
    };
  }

  public GMFitService getGMFitService() {
    return apiService;
  }
}