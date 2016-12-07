package com.mcsaatchi.gmfit.architecture.rest;

import com.mcsaatchi.gmfit.common.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
  private GMFit_Service apiService;

  public RestClient() {

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    httpClient.addInterceptor(loggingInterceptor);

    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL_ADDRESS)
        .client(httpClient.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    apiService = retrofit.create(GMFit_Service.class);
  }

  public GMFit_Service getGMFitService() {
    return apiService;
  }
}