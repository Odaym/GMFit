package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.classes.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private GMFit_Service apiService;

    public RestClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiService = retrofit.create(GMFit_Service.class);
    }

    public GMFit_Service getGMFitService() {
        return apiService;
    }
}