package com.mcsaatchi.gmfit.classes;

public class Cons {
    public static final String EXTRAS_PREFS = "GMFIT_PREFS";
    public static final String TAG = "GMFIT_DEBUG";

    public static final String EXTRAS_FITNESS_FRAGMENT = "FITNESS";
    public static final String EXTRAS_NUTRITION_FRAGMENT = "NUTRITION";
    public static final String EXTRAS_HEALTH_FRAGMENT = "HEALTH";


    public static final String EXTRAS_ADD_CHART_WHAT_TYPE = "add_chart_what_type";
    public static final String EXTRAS_CHART_TYPE_SELECTED = "chart_type_selected";

    public static enum ChartTypes {

        BARCHART_CHART_TYPE(1),
        BARLINECHARTBASE_CHART_TYPE(2),
        BUBBLECHART_CHART_TYPE(3),
        CANDLESTICKCHART_CHART_TYPE(4),
        CHART_CHART_TYPE(5),
        COMBINEDCHART_CHART_TYPE(6),
        HORIZONTALBARCHART_CHART_TYPE(7),
        LINECHART_CHART_TYPE(8),
        PIECHART_CHART_TYPE(9),
        PIERADARCHARTBASE_CHART_TYPE(10),
        RADARCHART_CHART_TYPE(11),
        SCATTERCHART_CHART_TYPE(12);

        public int chart_type;

        private ChartTypes(int chart_type) {
            this.chart_type = chart_type;
        }

        public int getChart_type(){
            return this.chart_type;
        }
    }

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

    public static final String EXTRAS_USER_LOGGED_IN = "user_logged_in";
    public static final String EXTRAS_USER_FULL_NAME = "user_full_name";
    public static final String EXTRAS_USER_EMAIL = "user_email_address";
    public static final String EXTRAS_USER_DISPLAY_PHOTO = "user_display_photo";

    public static final String BUNDLE_ACTIVITY_TITLE = "activity_title";
    public static final String BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED = "activity_back_button_enabled";
}
