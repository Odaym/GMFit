package com.mcsaatchi.gmfit.classes;

import okhttp3.MediaType;

public class Cons {
    public static final String SHARED_PREFS_TITLE = "GMFIT_PREFS";
    public static final String TAG = "GMFIT_DEBUG";

    public static final String EXTRAS_USER_LOGGED_IN = "user_logged_in";
    public static final String EXTRAS_USER_FULL_NAME = "user_full_name";
    public static final String EXTRAS_USER_EMAIL = "user_email_address";
    public static final String EXTRAS_USER_DISPLAY_PHOTO = "user_display_photo";
    public static final String EXTRAS_USER_POLICY = "user_policy";
    public static final String EXTRAS_USER_FACEBOOK_TOKEN = "user_facebook_token";

    public static final String EXTRAS_FITNESS_FRAGMENT = "FITNESS";
    public static final String EXTRAS_NUTRITION_FRAGMENT = "NUTRITION";
    public static final String EXTRAS_HEALTH_FRAGMENT = "HEALTH";

    public static final String EXTRAS_ADD_CHART_WHAT_TYPE = "add_chart_what_type";
    public static final String EXTRAS_ADD_CHART_WHAT_NAME = "add_chart_what_name";
    public static final String EXTRAS_CHART_TYPE_SELECTED = "chart_type_selected";
    public static final String EXTRAS_CHART_FULL_NAME = "chart_full_name";

    public static final int BarChart_CHART_TYPE = 1;
    public static final String EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY = "fitness_widgets_order_array";
    public static final String EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY_CHANGED = "fitness_widgets_order_array_changed";
    public static final String EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY = "nutrition_widgets_order_array";
    public static final String EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY_CHANGED = "nutrition_widgets_order_array_changed";
    public static final String EXTRAS_HEALTH_WIDGETS_ORDER_ARRAY = "health_widgets_order_array";
    public static final String EXTRAS_HEALTH_WIDGETS_ORDER_ARRAY_CHANGED = "health_widgets_order_array_changed";
    public static final String EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED = "fitness_charts_order_array_changed";
    public static final String EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED = "nutrition_charts_order_array_changed";
    public static final String EXTRAS_HEALTH_CHARTS_ORDER_ARRAY_CHANGED = "health_charts_order_array_changed";
    public static final String EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE = "customize_widgets_type";
    public static final String EXTRAS_ALL_DATA_CHARTS = "all_data_charts";
    public static final String EXTRAS_ADD_FITNESS_CHART = "add_fitness_chart";
    public static final String EXTRAS_ADD_NUTRIITION_CHART = "add_nutrition_chart";
    public static final String EXTRAS_MAIN_MEAL_NAME = "main_meal_name";
    public static final String EXTRAS_PICKED_MEAL_ENTRY = "picked_meal_entry";
    public static final String BUNDLE_ACTIVITY_TITLE = "activity_title";
    public static final String BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED = "activity_back_button_enabled";
    public static final String BUNDLE_FITNESS_WIDGETS_MAP = "fitness_widgets_map";
    public static final String BASE_URL_ADDRESS = "http://gmfit.mcsaatchi.me/api/v1/";
    public static final MediaType JSON_FORMAT_IDENTIFIER
            = MediaType.parse("application/json; charset=utf-8");

    public static final int POST_REQUEST_TYPE = 500;

    public static final String SLUG_VALUE_STEPS_COUNT = "steps-count";
    public static final String SLUG_VALUE_DISTANCE_TRAVELED = "distance-traveled";
    public static final String SLUG_VALUE_CYCLING_DISTANCE = "cycling-distance";
    public static final String SLUG_VALUE_ACTIVE_CALORIES = "active-calories";

    public static final String EVENT_USER_FINALIZE_SETUP_PROFILE = "user_finalize_setup_profile";
    public static final String EVENT_USER_SETUP_PROFILE_STEP_1 = "user_setup_profile_step_1";
    public static final String EVENT_USER_SETUP_PROFILE_STEP_2 = "user_setup_profile_step_2";

    public static final String EVENT_CHART_METRICS_RECEIVED = "event_chart_metrics_received";
    public static final String EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY = "signned_up_successfully_close_login_activity";

    //REGISTERATION API
    public static final String NO_ACCESS_TOKEN_FOUND_IN_PREFS = "no_access_token_in_prefs";
    public static final String PREF_USER_ACCESS_TOKEN = "user_access_token";
    public static final String USER_ACCESS_TOKEN_HEADER_PARAMETER = "Authorization";
    public static final int REGISTRATION_PROCESS_SUCCEEDED_TOKEN_SAVED = 120;
}
