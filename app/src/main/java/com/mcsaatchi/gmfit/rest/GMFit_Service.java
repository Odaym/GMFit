package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.data_access.ApiCallsHandler;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GMFit_Service {

    @POST("login")
    Call<AuthenticationResponse> signInUser(@Body ApiCallsHandler.SignInRequest userCredentails);

    @POST("login")
    Call<AuthenticationResponse> signInUserSilently(@Body ApiCallsHandler.SignInRequest userCredentails);

    @POST("register")
    Call<AuthenticationResponse> registerUser(@Body ApiCallsHandler.RegisterRequest userCredentials);

    @GET("logout")
    Call<DefaultGetResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/update-profile")
    Call<DefaultGetResponse> updateUserProfile(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.UpdateProfileRequest updateProfileRequest);

    @GET
    Call<UiResponse> getUiForSection(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @GET("user-policy")
    Call<UserPolicyResponse> getUserPolicy(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("refresh-token")
    Call<AuthenticationResponse> refreshAccessToken(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/add-metric")
    Call<DefaultGetResponse> updateMetrics(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.UpdateMetricsRequest
            updateMetricsRequest);

    @GET("user/metrics/range")
    Call<ChartMetricBreakdownResponse> getPeriodicalChartData(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String
            start_date, @Query("end_date") String end_date, @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);

    @POST("verify")
    Call<DefaultGetResponse> verifyRegistrationCode(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.VerificationRequest
            verificationRequest);

    @GET("medical-conditions")
    Call<MedicalConditionsResponse> getMedicalConditions(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("facebook")
    Call<AuthenticationResponse> registerUserFacebook(@Body ApiCallsHandler.RegisterFacebookRequest registerFacebookRequest);

    @POST("forgot-password")
    Call<DefaultGetResponse> sendResetPasswordLink(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.ForgotPasswordRequest
            sendResetPasswordLinkRequest);

    @POST("forgot-password-change")
    Call<DefaultGetResponse> finalizeResetPassword(@Body ApiCallsHandler.ResetPasswordRequest resetPasswordRequest);

    @GET
    Call<SlugBreakdownResponse> getBreakdownForSlug(@Url String finalURL, @Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/widgets/update")
    Call<DefaultGetResponse> updateUserWidgets(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                               @Body ApiCallsHandler.UpdateWidgetsRequest updateWidgetsRequest);

    @POST("user/charts/update")
    Call<DefaultGetResponse> updateUserCharts(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                               @Body ApiCallsHandler.UpdateChartsRequest updateChartsRequest);

    @POST("user/meals/update")
    Call<DefaultGetResponse> updateUserMeals(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                              @Body ApiCallsHandler.UpdateMealsRequest updateMealsRequest);

    @GET("user/meals")
    Call<UserMealsResponse> getUserAddedMeals(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("meals")
    Call<SearchMealItemResponse> searchForMeals(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("name") String mealName);

    @GET
    Call<MealMetricsResponse> getMealMetrics(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @POST("user/meals/store")
    Call<DefaultGetResponse> storeNewMeal(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.StoreNewMealRequest storeNewMealRequest);

    @GET
    Call<RecentMealsResponse> getRecentMeals(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);
}
