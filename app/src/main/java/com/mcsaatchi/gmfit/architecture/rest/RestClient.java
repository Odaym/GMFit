package com.mcsaatchi.gmfit.architecture.rest;

import android.content.Context;
import android.content.SharedPreferences;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.joda.time.DateTime;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
  @Inject SharedPreferences prefs;

  private DateTime dt = new DateTime();

  private GMFit_Service apiService;

  public RestClient(Context context) {
    ((GMFitApplication) context).getAppComponent().inject(this);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.addInterceptor(loggingInterceptor);
    httpClient.addInterceptor(new Interceptor() {
      @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = null;
        try {
          requestBuilder = original.newBuilder()
              .header("Authorization", prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
                  Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS))
              .header("Date",
                  new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.getDefault()).format(
                      new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.getDefault()).parse(
                          dt.getYear()
                              + " "
                              + dt.getMonthOfYear()
                              + " "
                              + dt.getDayOfMonth()
                              + " "
                              + dt.getHourOfDay()
                              + ":"
                              + dt.getMinuteOfHour()
                              + ":"
                              + dt.getSecondOfMinute())));
        } catch (ParseException e) {
          e.printStackTrace();
        }

        Request request = requestBuilder.build();

        return chain.proceed(request);
      }
    });

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