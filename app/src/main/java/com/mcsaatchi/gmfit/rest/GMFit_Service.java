package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.data_access.ApiCallsHandler;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
    Call<DefaultGetResponse> signOutUser(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/update-profile")
    Call<DefaultGetResponse> updateUserProfile(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.UpdateProfileRequest updateProfileRequest);

    @Multipart
    @POST("user/profile/picture")
    Call<DefaultGetResponse> updateUserPicture(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @PartMap() Map<String, RequestBody> profilePicture);

    @GET
    Call<UiResponse> getUiForSection(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @GET("user-policy")
    Call<UserPolicyResponse> getUserPolicy(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("refresh-token")
    Call<AuthenticationResponse> refreshAccessToken(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/add-metric")
    Call<DefaultGetResponse> updateMetrics(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.UpdateMetricsRequest
            updateMetricsRequest);

    @GET("user/metrics/range")
    Call<ChartMetricBreakdownResponse> getPeriodicalChartData(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String
            start_date, @Query("end_date") String end_date, @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);

    @POST("verify")
    Call<DefaultGetResponse> verifyRegistrationCode(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.VerificationRequest
            verificationRequest);

    @GET("medical-conditions")
    Call<MedicalConditionsResponse> getMedicalConditions(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("facebook")
    Call<AuthenticationResponse> registerUserFacebook(@Body ApiCallsHandler.RegisterFacebookRequest registerFacebookRequest);

    @POST("forgot-password")
    Call<DefaultGetResponse> sendResetPasswordLink(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.ForgotPasswordRequest
            sendResetPasswordLinkRequest);

    @POST("forgot-password-change")
    Call<DefaultGetResponse> finalizeResetPassword(@Body ApiCallsHandler.ResetPasswordRequest resetPasswordRequest);

    @GET
    Call<SlugBreakdownResponse> getBreakdownForSlug(@Url String finalURL, @Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/widgets/update")
    Call<DefaultGetResponse> updateUserWidgets(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                               @Body ApiCallsHandler.UpdateWidgetsRequest updateWidgetsRequest);

    @POST("user/charts/update")
    Call<DefaultGetResponse> updateUserCharts(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                              @Body ApiCallsHandler.UpdateChartsRequest updateChartsRequest);

    @POST("user/meals/update")
    Call<DefaultGetResponse> updateUserMeals(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken,
                                             @Body ApiCallsHandler.UpdateMealsRequest updateMealsRequest);

    @POST("user/charts/add")
    Call<DefaultGetResponse> addMetricChart(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.AddMetricChartRequest addMetricChartRequest);

    @GET("user/meals")
    Call<UserMealsResponse> getUserAddedMeals(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("user/meals")
    Call<UserMealsResponse> getUserAddedMealsOnDate(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("start_date") String
            start_date, @Query("end_date") String end_date);

    @GET
    Call<ChartsBySectionResponse> getChartsBySection(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @GET("meals")
    Call<SearchMealItemResponse> searchForMeals(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("name") String mealName);

    @GET
    Call<MealMetricsResponse> getMealMetrics(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @POST("user/meals/store")
    Call<DefaultGetResponse> storeNewMeal(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.StoreNewMealRequest storeNewMealRequest);

    @POST("user/meals/delete")
    Call<DefaultGetResponse> deleteUserMeal(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.DeleteMealRequest deleteMealRequest);

    @GET
    Call<RecentMealsResponse> getRecentMeals(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Url String fullUrl);

    @POST("user/meals/request")
    Call<DefaultGetResponse> requestNewMeal(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.RequestMealRequest requestMealRequest);

    @GET("medical-tests")
    Call<MedicalTestsResponse> getMedicalTests(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("user/widgets")
    Call<HealthWidgetsResponse> getHealthWidgets(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Query("section") String sectionName);

    @Multipart
    @POST("user/medical/store")
    Call<DefaultGetResponse> storeNewHealthTest(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Part("test_slug") RequestBody test_slug,
                                                @Part("date_taken") RequestBody date_taken, @PartMap() Map<String, RequestBody> metrics, @PartMap() Map<String, RequestBody> imageFiles);

    @Multipart
    @POST("user/medical/edit")
    Call<DefaultGetResponse> editExistingHealthTest(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Part("instance_id") RequestBody instance_id, @PartMap() Map<String, RequestBody> metrics,
                                                    @PartMap() Map<String, RequestBody> imageFiles, @PartMap() Map<String, RequestBody> deletedImages);

    @GET("user/medical")
    Call<TakenMedicalTestsResponse> getTakenMedicalTests(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("user/profile")
    Call<UserProfileResponse> getUserProfile(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @GET("emergency")
    Call<EmergencyProfileResponse> getEmergencyProfile(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/charts/delete")
    Call<DefaultGetResponse> deleteUserChart(@Header(Constants.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body ApiCallsHandler.DeleteUserChartRequest chart_id);
}
