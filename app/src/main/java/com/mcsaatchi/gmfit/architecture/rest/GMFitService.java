package com.mcsaatchi.gmfit.architecture.rest;

import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GMFitService {

  @POST("login") Call<AuthenticationResponse> signInUser(
      @Body ApiCallsHandler.SignInRequest userCredentails);

  @POST("login") Call<AuthenticationResponse> signInUserSilently(
      @Body ApiCallsHandler.SignInRequest userCredentails);

  @POST("register") Call<AuthenticationResponse> registerUser(
      @Body ApiCallsHandler.RegisterRequest userCredentials);

  @GET("logout") Call<DefaultGetResponse> signOutUser();

  @POST("user/update-profile") Call<DefaultGetResponse> updateUserProfile(
      @Body ApiCallsHandler.UpdateProfileRequest updateProfileRequest);

  @POST("user/update-profile") Call<DefaultGetResponse> updateUserWeight(
      @Body ApiCallsHandler.UpdateUserWeightRequest updateUserWeightRequest);

  @Multipart @POST("user/profile/picture") Call<DefaultGetResponse> updateUserPicture(
      @PartMap() Map<String, RequestBody> profilePicture);

  @GET Call<UiResponse> getUiForSection(@Url String fullUrl);

  @GET("refresh-token") Call<AuthenticationResponse> refreshAccessToken();

  @POST("user/add-metric") Call<DefaultGetResponse> updateMetrics(
      @Body ApiCallsHandler.UpdateMetricsRequest updateMetricsRequest);

  @GET("user/metrics/range") Call<ChartMetricBreakdownResponse> getPeriodicalChartData(
      @Query("start_date") String start_date, @Query("end_date") String end_date,
      @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);

  @GET("user/goals/metrics") Call<UserGoalMetricsResponse> getUserGoalMetrics(
      @Query("date") String start_date, @Query("type") String type);

  @GET("meta") Call<MetaTextsResponse> getMetaTexts(@Query("section") String section);

  @POST("verify") Call<DefaultGetResponse> verifyRegistrationCode(
      @Body ApiCallsHandler.VerificationRequest verificationRequest);

  @GET("medical-conditions") Call<MedicalConditionsResponse> getMedicalConditions();

  @POST("facebook") Call<AuthenticationResponse> handleFacebookProcess(
      @Body ApiCallsHandler.HandleFacebookRequest handleFacebookRequest);

  @POST("forgot-password") Call<DefaultGetResponse> sendResetPasswordLink(
      @Body ApiCallsHandler.ForgotPasswordRequest sendResetPasswordLinkRequest);

  @POST("user/change-password") Call<DefaultGetResponse> changePassword(
      @Body ApiCallsHandler.ChangePasswordRequest changePasswordRequests);

  @POST("forgot-password-change") Call<DefaultGetResponse> finalizeResetPassword(
      @Body ApiCallsHandler.ResetPasswordRequest resetPasswordRequest);

  @GET Call<SlugBreakdownResponse> getBreakdownForSlug(@Url String finalURL);

  @POST("user/widgets/update") Call<DefaultGetResponse> updateUserWidgets(
      @Body ApiCallsHandler.UpdateWidgetsRequest updateWidgetsRequest);

  @POST("user/charts/update") Call<DefaultGetResponse> updateUserCharts(
      @Body ApiCallsHandler.UpdateChartsRequest updateChartsRequest);

  @POST("user/meals/update") Call<DefaultGetResponse> updateUserMeals(
      @Body ApiCallsHandler.UpdateMealsRequest updateMealsRequest);

  @POST("user/charts/add") Call<DefaultGetResponse> addMetricChart(
      @Body ApiCallsHandler.AddMetricChartRequest addMetricChartRequest);

  @GET("user/meals") Call<UserMealsResponse> getUserAddedMeals();

  @GET("user/meals") Call<UserMealsResponse> getUserAddedMealsOnDate(
      @Query("start_date") String start_date, @Query("end_date") String end_date);

  @GET Call<ChartsBySectionResponse> getChartsBySection(@Url String fullUrl);

  @GET("meals") Call<SearchMealItemResponse> searchForMeals(@Query("name") String mealName);

  @GET("meals") Call<SearchMealItemResponse> searchForMealBarcode(@Query("barcode") String barcode);

  @GET Call<MealMetricsResponse> getMealMetrics(@Url String fullUrl);

  @POST("user/meals/store") Call<DefaultGetResponse> storeNewMeal(
      @Body ApiCallsHandler.StoreNewMealRequest storeNewMealRequest);

  @POST("user/meals/delete") Call<DefaultGetResponse> deleteUserMeal(
      @Body ApiCallsHandler.DeleteMealRequest deleteMealRequest);

  @POST("user/medical/delete") Call<DefaultGetResponse> deleteUserTest(
      @Body ApiCallsHandler.DeleteTestRequest deleteTestRequest);

  @GET Call<RecentMealsResponse> getRecentMeals(@Url String fullUrl);

  @POST("user/meals/request") Call<DefaultGetResponse> requestNewMeal(
      @Body ApiCallsHandler.RequestMealRequest requestMealRequest);

  @GET("medical-tests") Call<MedicalTestsResponse> getMedicalTests();

  @GET("user/medical/metrics") Call<MedicalTestMetricsResponse> getTesticularMetrics();

  @GET("user/widgets") Call<WidgetsResponse> getWidgets(@Query("section") String sectionName);

  @GET("user/widgets") Call<WidgetsResponse> getWidgetsWithDate(
      @Query("section") String sectionName, @Query("date") String date);

  @Multipart @POST("user/medical/store") Call<DefaultGetResponse> storeNewHealthTest(
      @Part("name") RequestBody test_name, @Part("date_taken") RequestBody date_taken,
      @PartMap() Map<String, RequestBody> metrics, @PartMap() Map<String, RequestBody> imageFiles);

  @Multipart @POST("user/medical/edit") Call<DefaultGetResponse> editExistingHealthTest(
      @Part("instance_id") RequestBody instance_id, @Part("name") RequestBody test_name,
      @Part("date_taken") RequestBody date_taken, @PartMap() Map<String, RequestBody> metrics,
      @PartMap() Map<String, RequestBody> imageFiles,
      @Part("delete_image_ids") RequestBody deletedImages);

  @GET("user/medical") Call<TakenMedicalTestsResponse> getTakenMedicalTests();

  @GET("user/profile") Call<UserProfileResponse> getOnboardingStatus();

  @GET("user/profile") Call<UserProfileResponse> getUserProfile();

  @GET("emergency") Call<EmergencyProfileResponse> getEmergencyProfile();

  @POST("user/charts/delete") Call<DefaultGetResponse> deleteUserChart(
      @Body ApiCallsHandler.DeleteUserChartRequest chart_id);

  @GET("activity_levels") Call<ActivityLevelsResponse> getActivityLevels();

  @GET("user_goals") Call<UserGoalsResponse> getUserGoals();

  @GET("user/weight/history") Call<WeightHistoryResponse> getUserWeightHistory();

  /**
   * INSURANCE API's
   */
  @POST("insurance/insurance-card-details") Call<CardDetailsResponse> getCardDetails(
      @Body ApiCallsHandler.SimpleInsuranceRequest simpleInsuranceRequest);

  @POST("insurance/medecines/most-popular")
  Call<MostPopularMedicationsResponse> getMostPopularMedications(
      @Body ApiCallsHandler.DefaultBodyForInsuranceRequests mostPopularMedicationsRequest);

  @POST("insurance/medecines/search") Call<SearchMedicinesResponse> searchMedicines(
      @Body ApiCallsHandler.SearchMedicinesRequest searchMedicinesRequest);

  @POST("insurance/login") Call<InsuranceLoginResponse> insuranceUserLogin(
      @Body ApiCallsHandler.InsuranceLoginRequest insuranceUserLoginRequest);

  @POST("insurance/coverage") Call<CoverageDescriptionResponse> getCoverageDescription(
      @Body ApiCallsHandler.CoverageDescriptionRequest coverageDescriptionRequest);

  @POST("insurance/update-info") Call<UpdateInsurancePasswordResponse> updateInsurancePassword(
      @Body ApiCallsHandler.UpdateInsurancePasswordRequest updateInsurancePasswordRequest);

  @POST("insurance/sub-categories") Call<SubCategoriesResponse> getSubCategories(
      @Body ApiCallsHandler.SimpleInsuranceRequest simpleInsuranceRequest);

  @POST("insurance/network/advanced-search") Call<GetNearbyClinicsResponse> getNearbyClinics(
      @Body ApiCallsHandler.NearbyClinicsRequest simpleInsuranceRequest);

  @Multipart @POST("insurance/request/create") Call<CreateNewRequestResponse> createNewRequest(
      @Part("contractNo") RequestBody contractNo, @Part("categ") RequestBody categ,
      @Part("subCategId") RequestBody subCategId, @Part("requestTypeId") RequestBody requestTypeId,
      @Part("claimedAmount") RequestBody claimedAmount,
      @Part("currencyCode") RequestBody currencyCode, @Part("serviceDate") RequestBody serviceDate,
      @Part("providerCode") RequestBody providerCode, @Part("remarks") RequestBody remarks,
      @PartMap() Map<String, RequestBody> attachements);

  @POST("insurance/claims/list") Call<ClaimsListResponse> getClaimsList(
      @Body ApiCallsHandler.ClaimsListRequest claimsListRequest);

  @POST("insurance/claims/list") Call<ClaimsListDetailsResponse> getClaimsListDetails(
      @Body ApiCallsHandler.ClaimsListDetailsRequest claimsListDetailsRequest);

  @POST("insurance/snapshot/pdf") Call<SnapshotResponse> getSnapshot(
      @Body ApiCallsHandler.SnapShotRequest snapShotRequest);
}
