package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.activities.GetStarted_Activity;
import com.mcsaatchi.gmfit.activities.SignIn_Activity;
import com.mcsaatchi.gmfit.activities.SignUp_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_3_Fragment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GMFit_Service {

    @POST("login")
    Call<AuthenticationResponse> signInUser(@Body SignIn_Activity.SignInRequest userCredentails);

    @POST("register")
    Call<AuthenticationResponse> registerUser(@Body SignUp_Activity.RegisterRequest userCredentials);

    @GET("logout")
    Call<DefaultGetResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/update-profile")
    Call<DefaultGetResponse> updateUserProfile(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body Setup_Profile_3_Fragment
            .UpdateProfileRequest updateProfileRequest);

    @GET("user-policy")
    Call<UserPolicyResponse> getUserPolicy(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/add-metric")
    Call<DefaultGetResponse> updateMetrics(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body Fitness_Fragment.UpdateMetricsRequest
            updateMetricsRequest);

    @GET("user/metrics")
    Call<DefaultGetResponse> getMetricsForChart(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String
            start_date, @Query("end_date") String end_date, @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);

    @POST("verify")
    Call<DefaultGetResponse> verifyRegistrationCode(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body GetStarted_Activity.VerificationRequest
            verificationRequest);
}
