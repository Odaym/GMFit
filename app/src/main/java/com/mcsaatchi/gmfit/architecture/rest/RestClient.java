package com.mcsaatchi.gmfit.architecture.rest;

import android.content.Context;
import android.content.SharedPreferences;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.joda.time.DateTime;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
  @Inject SharedPreferences prefs;

  private DateTime dt = new DateTime();

  private GMFitService apiService;

  public RestClient(Context context) {
    ((GMFitApplication) context).getAppComponent().inject(this);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.readTimeout(45, TimeUnit.SECONDS);
    httpClient.writeTimeout(45, TimeUnit.SECONDS);
    //httpClient.addInterceptor(new ChuckInterceptor(context));
    httpClient.addInterceptor(loggingInterceptor);
    httpClient.addInterceptor(chain -> {
      Request original = chain.request();

      Request.Builder requestBuilder = null;
      try {
        requestBuilder = original.newBuilder()
            .header("Authorization", prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
                Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS))
            //.header("Authorization",
            //    "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIxLCJpc3MiOiJodHRwczpcL1wvbW9iaWxlYXBwLmdsb2JlbWVkZml0LmNvbVwvYXBpXC92MVwvbG9naW4iLCJpYXQiOjE0OTIwMDEyNzUsImV4cCI6MTQ5NDcwMTI3NSwibmJmIjoxNDkyMDAxMjc1LCJqdGkiOiIyYzM1YmFmNzA3ODhmMDcyNWIyNmM5NjA4NzczMmU3YiJ9.jGxO37klbrj_SeFsYmWJd5M2YibhM3bLSGm7njyR73k")
            .header("Date", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US).format(
                new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.US).parse(dt.getYear()
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
    });

    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL_ADDRESS)
        .client(httpClient.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    apiService = retrofit.create(GMFitService.class);
  }

  public GMFitService getGMFitService() {
    return apiService;
  }
}