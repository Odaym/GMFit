package com.mcsaatchi.gmfit.classes;

import com.mcsaatchi.gmfit.models.LogoutResponse;
import com.mcsaatchi.gmfit.models.MetricsResponse;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GMFit_Service {

    @GET("logout")
    Call<LogoutResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("register")
    Call<ResponseBody> registerUser(@Body JSONObject credentials);

    @GET("user/metrics")
    Call<MetricsResponse> getChartMetricsByDate(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String
            start_date, @Query("end_date") String end_date, @Query("type") String
            type,
                                                @Query
            ("monitored_metrics") String monitored_metrics);
}
