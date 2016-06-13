package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.models.DefaultResponse;
import com.mcsaatchi.gmfit.models.MetricsResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface GMFit_Service {

    @GET("logout")
    Observable<DefaultResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("register")
    Observable<DefaultResponse> registerUser(@Body JSONObject userCredentials);

    @GET("user/metrics")
    Call<MetricsResponse> getChartMetricsByDate(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);
}
