package com.mcsaatchi.gmfit.rest;

import android.util.Log;

import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.GMFit_Application;

import java.io.File;
import java.io.IOException;
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

public class RestClient {
    private static final String TAG = "RestClient";
    private GMFit_Service apiService;

    public RestClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(loggingInterceptor);
        httpClient.addInterceptor(provideOfflineCacheInterceptor());
        httpClient.addNetworkInterceptor(provideCacheInterceptor());
        httpClient.cache(provideCache());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_ADDRESS)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiService = retrofit.create(GMFit_Service.class);
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(GMFit_Application.getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024);
        } catch (Exception e) {
            Log.d(TAG, "provideCache: Could not create cache!");
        }
        return cache;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(15, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!GMFit_Application.hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    public GMFit_Service getGMFitService() {
        return apiService;
    }
}