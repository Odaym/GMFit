package com.mcsaatchi.gmfit.data_access;

import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.CachingRestClient;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.rest.HealthWidgetsResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.RecentMealsResponse;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.UiResponse;
import com.mcsaatchi.gmfit.rest.UserGoalsResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.rest.UserPolicyResponse;
import com.mcsaatchi.gmfit.rest.UserProfileResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallsHandler {

    private static ApiCallsHandler apiCallsHandler;

    private ApiCallsHandler() {
    }

    public static ApiCallsHandler getInstance() {
        if (apiCallsHandler == null) {
            apiCallsHandler = new ApiCallsHandler();
        }

        return apiCallsHandler;
    }

    void getSlugBreakdownForChart(final String chartType, String userAccessToken, final Callback<SlugBreakdownResponse> callback) {

        Call<SlugBreakdownResponse> apiCall = new CachingRestClient().getGMFitService().getBreakdownForSlug(Constants.BASE_URL_ADDRESS +
                "user/metrics/breakdown?slug=" + chartType, userAccessToken);

        apiCall.enqueue(new Callback<SlugBreakdownResponse>() {
            @Override
            public void onResponse(Call<SlugBreakdownResponse> call, Response<SlugBreakdownResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void refreshAccessToken(String userAccessToken, final Callback<AuthenticationResponse> callback) {
        Call<AuthenticationResponse> apiCall = new CachingRestClient().getGMFitService().refreshAccessToken(userAccessToken);

        apiCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
            }
        });
    }

    void synchronizeMetricsWithServer(String userAccessToken, String[] slugsArray, int[] valuesArray) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().updateMetrics(userAccessToken, new UpdateMetricsRequest(slugsArray, valuesArray, Helpers.getCalendarDate()));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            }
        });
    }

    void signInUser(final String email, final String password, final Callback<AuthenticationResponse> callback) {
        Call<AuthenticationResponse> apiCall = new CachingRestClient().getGMFitService().signInUser(new SignInRequest(email,
                password));

        apiCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void registerUser(final String full_name, final String email, final String password, final Callback<AuthenticationResponse> callback) {
        Call<AuthenticationResponse> apiCall = new CachingRestClient().getGMFitService().registerUser(new RegisterRequest(full_name, email, password));

        apiCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void signInUserSilently(String email, String password, final Callback<AuthenticationResponse> callback) {
        Call<AuthenticationResponse> apiCall = new CachingRestClient().getGMFitService().signInUserSilently(new SignInRequest(email,
                password));

        apiCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void signOutUser(String userAccessToken, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().signOutUser(userAccessToken);

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void updateUserProfile(String userAccessToken, String finalDateOfBirth, String bloodType, String nationality, int medical_condition, String measurementSystem,
                           int goalId, int activityLevelId, int finalGender, double height, double weight, double BMI, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService().updateUserProfile(userAccessToken, new UpdateProfileRequest(finalDateOfBirth, bloodType,
                nationality, medical_condition, measurementSystem, goalId, activityLevelId, finalGender, height, weight, BMI));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Non-Caching
     *
     * @param userAccessToken
     * @param section
     * @param callback
     */
    void getUiForSection(String userAccessToken, String section, final Callback<UiResponse> callback) {
        Call<UiResponse> apiCall = new RestClient().getGMFitService().getUiForSection(userAccessToken, section);

        apiCall.enqueue(new Callback<UiResponse>() {
            @Override
            public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UiResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getMedicalConditions(String userAccessToken, final Callback<MedicalConditionsResponse> callback) {
        Call<MedicalConditionsResponse> apiCall = new CachingRestClient().getGMFitService().getMedicalConditions(userAccessToken);

        apiCall.enqueue(new Callback<MedicalConditionsResponse>() {
            @Override
            public void onResponse(Call<MedicalConditionsResponse> call, Response<MedicalConditionsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void sendResetPasswordLink(String userAccessToken, String email, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().sendResetPasswordLink(userAccessToken, new ForgotPasswordRequest(email));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void finalizeResetPassword(String resetPasswordToken, String newPassword, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().finalizeResetPassword(new ResetPasswordRequest(resetPasswordToken, newPassword));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void findMeals(String userAccessToken, String mealName, final Callback<SearchMealItemResponse> callback) {
        Call<SearchMealItemResponse> apiCall = new CachingRestClient().getGMFitService().searchForMeals(userAccessToken, mealName);

        apiCall.enqueue(new Callback<SearchMealItemResponse>() {
            @Override
            public void onResponse(Call<SearchMealItemResponse> call, Response<SearchMealItemResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getMealMetrics(String userAccessToken, String fullUrl, final Callback<MealMetricsResponse> callback) {
        Call<MealMetricsResponse> apiCall = new CachingRestClient().getGMFitService().getMealMetrics(userAccessToken, fullUrl);

        apiCall.enqueue(new Callback<MealMetricsResponse>() {
            @Override
            public void onResponse(Call<MealMetricsResponse> call, Response<MealMetricsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void verifyUser(String userAccessToken, String verificationCode, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().verifyRegistrationCode(userAccessToken, new VerificationRequest(verificationCode));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void updateUserWidgets(String userAccessToken, int[] widgetIds, int[] widgetPositions, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().updateUserWidgets(userAccessToken, new UpdateWidgetsRequest(widgetIds, widgetPositions));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void updateUserCharts(String userAccessToken, int[] chartIds, int[] chartPositions, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().updateUserCharts(userAccessToken, new UpdateChartsRequest(chartIds, chartPositions));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Non-Caching
     *
     * @param userAccessToken
     * @param callback
     */
    void getUserAddedMeals(String userAccessToken, final Callback<UserMealsResponse> callback) {
        Call<UserMealsResponse> apiCall = new RestClient().getGMFitService().getUserAddedMeals(userAccessToken);

        apiCall.enqueue(new Callback<UserMealsResponse>() {
            @Override
            public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserMealsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getUserAddedMealsOnDate(String userAccessToken, String chosenDate, final Callback<UserMealsResponse> callback) {
        Call<UserMealsResponse> apiCall = new CachingRestClient().getGMFitService().getUserAddedMealsOnDate(userAccessToken, chosenDate, chosenDate);

        apiCall.enqueue(new Callback<UserMealsResponse>() {
            @Override
            public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserMealsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getUserPolicy(String userAccessToken, final Callback<UserPolicyResponse> callback) {
        Call<UserPolicyResponse> apiCall = new CachingRestClient().getGMFitService().getUserPolicy(userAccessToken);

        apiCall.enqueue(new Callback<UserPolicyResponse>() {
            @Override
            public void onResponse(Call<UserPolicyResponse> call, Response<UserPolicyResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserPolicyResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void registerUserFacebook(String facebookAccessToken, final Callback<AuthenticationResponse> callback) {
        Call<AuthenticationResponse> apiCall = new CachingRestClient().getGMFitService().registerUserFacebook(new RegisterFacebookRequest(facebookAccessToken));

        apiCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void storeNewMeal(String userAccessToken, int meal_id, int servingsAmount, String when, String date, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().storeNewMeal(userAccessToken, new StoreNewMealRequest(meal_id, servingsAmount, when.toLowerCase(), date));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getRecentMeals(String userAccessToken, String fullUrl, final Callback<RecentMealsResponse> callback) {
        Call<RecentMealsResponse> apiCall = new CachingRestClient().getGMFitService().getRecentMeals(userAccessToken, fullUrl);

        apiCall.enqueue(new Callback<RecentMealsResponse>() {
            @Override
            public void onResponse(Call<RecentMealsResponse> call, Response<RecentMealsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<RecentMealsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void updateUserMeals(String userAccessToken, int instance_id, int amount, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().updateUserMeals(userAccessToken, new UpdateMealsRequest(instance_id, amount));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getChartsBySection(String userAccessToken, String chartSection, final Callback<ChartsBySectionResponse> callback) {
        Call<ChartsBySectionResponse> apiCall = new CachingRestClient().getGMFitService().getChartsBySection(userAccessToken, Constants.BASE_URL_ADDRESS +
                "charts?section=" + chartSection);

        apiCall.enqueue(new Callback<ChartsBySectionResponse>() {
            @Override
            public void onResponse(Call<ChartsBySectionResponse> call, Response<ChartsBySectionResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void addMetricChart(String userAccessToken, int chart_id, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().addMetricChart(userAccessToken, new AddMetricChartRequest(chart_id));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getPeriodicalChartData(String userAccessToken, String start_date, String end_date, String type, String monitored_metric, final Callback<ChartMetricBreakdownResponse> callback) {
        Call<ChartMetricBreakdownResponse> apiCall = new CachingRestClient().getGMFitService().getPeriodicalChartData(userAccessToken, start_date, end_date, type, monitored_metric);

        apiCall.enqueue(new Callback<ChartMetricBreakdownResponse>() {
            @Override
            public void onResponse(Call<ChartMetricBreakdownResponse> call, Response<ChartMetricBreakdownResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void requestNewMeal(String userAccessToken, String mealName, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().requestNewMeal(userAccessToken, new RequestMealRequest(mealName));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getMedicalTests(String userAccessToken, final Callback<MedicalTestsResponse> callback) {
        Call<MedicalTestsResponse> apiCall = new CachingRestClient().getGMFitService().getMedicalTests(userAccessToken);

        apiCall.enqueue(new Callback<MedicalTestsResponse>() {
            @Override
            public void onResponse(Call<MedicalTestsResponse> call, Response<MedicalTestsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MedicalTestsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getHealthWidgets(String userAccessToken, String sectionName, final Callback<HealthWidgetsResponse> callback) {
        Call<HealthWidgetsResponse> apiCall = new CachingRestClient().getGMFitService().getHealthWidgets(userAccessToken, sectionName);

        apiCall.enqueue(new Callback<HealthWidgetsResponse>() {
            @Override
            public void onResponse(Call<HealthWidgetsResponse> call, Response<HealthWidgetsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<HealthWidgetsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void storeNewHealthTest(String userAccessToken, RequestBody test_slug, RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().storeNewHealthTest(userAccessToken, test_slug, date_taken, metrics, imageFiles);

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void editExistingHealthTest(String userAccessToken, RequestBody instance_id, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles, Map<String, RequestBody> deletedImages, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().editExistingHealthTest(userAccessToken, instance_id, metrics, deletedImages, imageFiles);

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getTakenMedicalTests(String userAccessToken, final Callback<TakenMedicalTestsResponse> callback) {
        Call<TakenMedicalTestsResponse> apiCall = new CachingRestClient().getGMFitService().getTakenMedicalTests(userAccessToken);

        apiCall.enqueue(new Callback<TakenMedicalTestsResponse>() {
            @Override
            public void onResponse(Call<TakenMedicalTestsResponse> call, Response<TakenMedicalTestsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getUserProfile(String userAccessToken, final Callback<UserProfileResponse> callback) {
        Call<UserProfileResponse> apiCall = new RestClient().getGMFitService().getUserProfile(userAccessToken);

        apiCall.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getEmergencyProfile(String userAccessToken, final Callback<EmergencyProfileResponse> callback) {
        Call<EmergencyProfileResponse> apiCall = new CachingRestClient().getGMFitService().getEmergencyProfile(userAccessToken);

        apiCall.enqueue(new Callback<EmergencyProfileResponse>() {
            @Override
            public void onResponse(Call<EmergencyProfileResponse> call, Response<EmergencyProfileResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<EmergencyProfileResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void deleteUserChart(String userAccessToken, String chart_id, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().deleteUserChart(userAccessToken, new DeleteUserChartRequest(chart_id));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void deleteUserMeal(String userAccessToken, int instance_id, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new CachingRestClient().getGMFitService().deleteUserMeal(userAccessToken, new DeleteMealRequest(instance_id));

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void updateUserPicture(String userAccessToken, Map<String, RequestBody> profilePicture, final Callback<DefaultGetResponse> callback) {
        Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService().updateUserPicture(userAccessToken, profilePicture);

        apiCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getActivityLevels(String userAccessToken, final Callback<ActivityLevelsResponse> callback) {
        Call<ActivityLevelsResponse> apiCall = new RestClient().getGMFitService().getActivityLevels("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjYwLCJpc3MiOiJodHRwOlwvXC9nbWZpdC5tY3NhYXRjaGkubWVcL2FwaVwvdjFcL2xvZ2luIiwiaWF0IjoxNDc4MDkxNTk3LCJleHAiOjE0Nzg2OTYzOTcsIm5iZiI6MTQ3ODA5MTU5NywianRpIjoiZDE2MTAzNTAwNjYzOTA2M2EyYjk5ZTE0MGNmNjBiOGYifQ.9Dzd0uU6a8RBtuLt35GdP7QMlFJqF7OL895h5dLe22s");

        apiCall.enqueue(new Callback<ActivityLevelsResponse>() {
            @Override
            public void onResponse(Call<ActivityLevelsResponse> call, Response<ActivityLevelsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ActivityLevelsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    void getUserGoals(String userAccessToken, final Callback<UserGoalsResponse> callback) {
        Call<UserGoalsResponse> apiCall = new RestClient().getGMFitService().getUserGoals("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjYwLCJpc3MiOiJodHRwOlwvXC9nbWZpdC5tY3NhYXRjaGkubWVcL2FwaVwvdjFcL2xvZ2luIiwiaWF0IjoxNDc4MDkxNTk3LCJleHAiOjE0Nzg2OTYzOTcsIm5iZiI6MTQ3ODA5MTU5NywianRpIjoiZDE2MTAzNTAwNjYzOTA2M2EyYjk5ZTE0MGNmNjBiOGYifQ.9Dzd0uU6a8RBtuLt35GdP7QMlFJqF7OL895h5dLe22s");

        apiCall.enqueue(new Callback<UserGoalsResponse>() {
            @Override
            public void onResponse(Call<UserGoalsResponse> call, Response<UserGoalsResponse> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserGoalsResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public class UpdateMetricsRequest {
        final String[] slug;
        final int[] value;
        final String date;

        UpdateMetricsRequest(String[] slug, int[] value, String date) {
            this.slug = slug;
            this.value = value;
            this.date = date;
        }
    }

    public class SignInRequest {
        final String email;
        final String password;

        SignInRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public class RegisterRequest {
        final String name;
        final String email;
        final String password;

        RegisterRequest(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    public class UpdateProfileRequest {
        final String date_of_birth;
        final String blood_type;
        final String country;
        final String metric_system;
        final int medical_conditions;
        final int user_goal;
        final int activity_level;
        final int gender;
        final double height;
        final double weight;
        final double BMI;

        UpdateProfileRequest(String date_of_birth, String blood_type, String country, int medical_conditions, String metric_system, int user_goal, int activity_level,
                             int gender, double height, double weight, double BMI) {
            this.date_of_birth = date_of_birth;
            this.blood_type = blood_type;
            this.country = country;
            this.metric_system = metric_system;
            this.medical_conditions = medical_conditions;
            this.user_goal = user_goal;
            this.activity_level = activity_level;
            this.gender = gender;
            this.height = height;
            this.weight = weight;
            this.BMI = BMI;
        }
    }

    public class ForgotPasswordRequest {
        final String email;

        ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }

    public class ResetPasswordRequest {
        final String password;
        final String token;

        ResetPasswordRequest(String token, String password) {
            this.token = token;
            this.password = password;
        }
    }

    public class VerificationRequest {
        final String code;

        VerificationRequest(String code) {
            this.code = code;
        }
    }

    public class UpdateWidgetsRequest {
        final int[] widgets;
        final int[] positions;

        UpdateWidgetsRequest(int[] widgets, int[] positions) {
            this.widgets = widgets;
            this.positions = positions;
        }
    }

    public class UpdateChartsRequest {
        final int[] charts;
        final int[] positions;

        UpdateChartsRequest(int[] charts, int[] positions) {
            this.charts = charts;
            this.positions = positions;
        }
    }

    public class RegisterFacebookRequest {
        final String access_token;

        RegisterFacebookRequest(String access_token) {
            this.access_token = access_token;
        }
    }

    public class StoreNewMealRequest {
        final int meal_id;
        final int amount;
        final String when;
        final String date;

        StoreNewMealRequest(int meal_id, int amount, String when, String date) {
            this.meal_id = meal_id;
            this.amount = amount;
            this.when = when;
            this.date = date;
        }
    }

    public class UpdateMealsRequest {
        final int instance_id;
        final int amount;

        UpdateMealsRequest(int instance_id, int amount) {
            this.instance_id = instance_id;
            this.amount = amount;
        }
    }

    public class AddMetricChartRequest {
        final int chart_id;

        AddMetricChartRequest(int chart_id) {
            this.chart_id = chart_id;
        }
    }

    public class RequestMealRequest {
        final String name;

        RequestMealRequest(String name) {
            this.name = name;
        }
    }

    public class DeleteUserChartRequest {
        final String chart_id;

        public DeleteUserChartRequest(String chart_id) {
            this.chart_id = chart_id;
        }
    }

    public class DeleteMealRequest {
        final int instance_id;

        public DeleteMealRequest(int instance_id) {
            this.instance_id = instance_id;
        }
    }
}
