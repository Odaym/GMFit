package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.activities.SignUp_Activity;
import com.mcsaatchi.gmfit.classes.Cons;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface GMFit_Service {

    @GET("logout")
    Observable<DefaultGetResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("register")
    Observable<RegisterResponse> registerUser(@Body SignUp_Activity.RegisterRequest userCredentials);

    @GET("user/metrics")
    Call<MetricsResponse> getChartMetricsByDate(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);
}
