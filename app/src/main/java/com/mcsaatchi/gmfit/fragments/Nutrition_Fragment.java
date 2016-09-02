package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.AddNewMealItem_Activity;
import com.mcsaatchi.gmfit.activities.BarcodeCapture_Activity;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.adapters.NutritionWidgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.models.NutritionWidget;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.mcsaatchi.gmfit.rest.UiResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nutrition_Fragment extends Fragment {

    public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;
    private static final int BARCODE_CAPTURE_RC = 773;
    private static final String TAG = "Nutrition_Fragment";
    private final String caloriesChartType = "Calories";
    private final String biotinChartType = "Biotin";
    private final String caffeineChartType = "Caffeine";
    private final String calciumChartType = "Calcium";
    private final String carbohydratesChartType = "Carbohydrates";
    private final String chlorideChartType = "Chloride";
    private final String chromiumChartType = "Chromium";
    private final String copperChartType = "Copper";
    private final String dietary_cholesterolChartType = "Dietary Cholesterol";
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.metricCounterTV)
    com.mcsaatchi.gmfit.classes.FontTextView metricCounterTV;
    /**
     * CHARTS
     */
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    /**
     * BREAKFAST CHART
     */
    @Bind(R.id.chartTitleTV_BREAKFAST)
    TextView chartTitleTV_BREAKFAST;
    @Bind(R.id.addEntryBTN_BREAKFAST)
    TextView addNewEntryBTN_BREAKFAST;
    @Bind(R.id.scanEntryBTN_BREAKFAST)
    TextView scanEntryBTN_BREAKFAST;
    @Bind(R.id.entriesContainerLayout_BREAKFAST)
    LinearLayout entriesContainerLayout_BREAKFAST;
    /**
     * LUNCH CHART
     */
    @Bind(R.id.chartTitleTV_LUNCH)
    TextView chartTitleTV_LUNCH;
    @Bind(R.id.addEntryBTN_LUNCH)
    TextView addNewEntryBTN_LUNCH;
    @Bind(R.id.scanEntryBTN_LUNCH)
    TextView scanEntryBTN_LUNCH;
    @Bind(R.id.entriesContainerLayout_LUNCH)
    LinearLayout entriesContainerLayout_LUNCH;
    /**
     * DINNER CHART
     */
    @Bind(R.id.chartTitleTV_DINNER)
    TextView chartTitleTV_DINNER;
    @Bind(R.id.addEntryBTN_DINNER)
    TextView addNewEntryBTN_DINNER;
    @Bind(R.id.scanEntryBTN_DINNER)
    TextView scanEntryBTN_DINNER;
    @Bind(R.id.entriesContainerLayout_DINNER)
    LinearLayout entriesContainerLayout_DINNER;
    /**
     * SNACKS CHART
     */
    @Bind(R.id.chartTitleTV_SNACKS)
    TextView chartTitleTV_SNACKS;
    @Bind(R.id.addEntryBTN_SNACKS)
    TextView addNewEntryBTN_SNACKS;
    @Bind(R.id.scanEntryBTN_SNACKS)
    TextView scanEntryBTN_SNACKS;
    @Bind(R.id.entriesContainerLayout_SNACKS)
    LinearLayout entriesContainerLayout_SNACKS;
    /**
     * ADD CHART BUTTON
     */
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;

    private NutritionWidgets_GridAdapter nutritionWidgets_GridAdapter;

    private RuntimeExceptionDao<NutritionWidget, Integer> nutritionWidgetsDAO;
    private QueryBuilder<NutritionWidget, Integer> nutritionWidgetsQB;
    private PreparedQuery<NutritionWidget> nutritionPQ;

    private ArrayList<NutritionWidget> widgetsMap;

    private SharedPreferences prefs;

    /**
     * TOP LAYOUT WITH WIDGETS
     */
    private NestedScrollView parentScrollView;
    private Activity parentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (Activity) context;
        nutritionWidgetsDAO = ((Base_Activity) parentActivity).getDBHelper().getNutritionWidgetsDAO();
        nutritionWidgetsQB = nutritionWidgetsDAO.queryBuilder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nutrition_tab_title);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        try {
            widgetsMap = (ArrayList<NutritionWidget>) nutritionWidgetsQB.orderBy("position", true).query();

            if (widgetsMap.isEmpty()) {
                getUiForSection("nutrition");
            } else {
                ArrayList<NutritionWidget> subNutritionWidgetsList = new ArrayList<>(widgetsMap.subList(0, 4));

                widgetsMap = subNutritionWidgetsList;

                setUpWidgetsGridView(subNutritionWidgetsList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_NUTRIITION_CHART);
                startActivityForResult(intent,
                        ADD_NEW_NUTRITION_CHART_REQUEST);
            }
        });

        metricCounterTV.setText(String.valueOf((int) prefs.getFloat(Helpers.getTodayDate() + "_calories", 0)));

        addNewEntryBTN_BREAKFAST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMealEntryPickerActivity(chartTitleTV_BREAKFAST.getText().toString());
            }
        });
        scanEntryBTN_BREAKFAST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleScanMealEntry();
            }
        });

        addNewEntryBTN_LUNCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMealEntryPickerActivity(chartTitleTV_LUNCH.getText().toString());
            }
        });
        scanEntryBTN_LUNCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleScanMealEntry();
            }
        });

        addNewEntryBTN_DINNER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMealEntryPickerActivity(chartTitleTV_DINNER.getText().toString());
            }
        });
        scanEntryBTN_DINNER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleScanMealEntry();
            }
        });

        addNewEntryBTN_SNACKS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMealEntryPickerActivity(chartTitleTV_SNACKS.getText().toString());
            }
        });
        scanEntryBTN_SNACKS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleScanMealEntry();
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus_Singleton.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus_Singleton.getInstance().unregister(this);
    }

    private void getUiForSection(String section) {
        Call<UiResponse> getUiForSectionCall = new RestClient().getGMFitService().getUiForSection(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section);

        getUiForSectionCall.enqueue(new Callback<UiResponse>() {
            @Override
            public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {

                switch (response.code()) {
                    case 200:
                        List<AuthenticationResponseWidget> widgetsMapFromAPI = response.body().getData().getBody().getWidgets();
                        List<AuthenticationResponseChart> chartsMapFromAPI = response.body().getData().getBody().getCharts();

                        /**
                         * Stuff already exists in DB
                         */
                        for (int i = 0; i < widgetsMapFromAPI.size(); i++) {
                            NutritionWidget nutritionWidget = new NutritionWidget();
                            nutritionWidget.setTitle(widgetsMapFromAPI.get(i).getName());
                            nutritionWidget.setPosition(Integer.parseInt(widgetsMapFromAPI.get(i).getPosition()));
                            nutritionWidget.setMeasurementUnit(widgetsMapFromAPI.get(i).getUnit());
                            nutritionWidget.setWidget_id(widgetsMapFromAPI.get(i).getWidgetId());
                            nutritionWidget.setValue(Double.parseDouble(widgetsMapFromAPI.get(i).getTotal()));

                            if (checkIfFieldExists(widgetsMapFromAPI.get(i).getWidgetId())) {
                                nutritionWidgetsDAO.update(nutritionWidget);
                            } else {
                                nutritionWidgetsDAO.create(nutritionWidget);
                            }
                        }

                        try {
                            /**
                             * Refresh the query builder
                             */
                            nutritionWidgetsQB = nutritionWidgetsDAO.queryBuilder();

                            /**
                             * Grab the newly created/updated Nutrition widgets from DB
                             */
                            ArrayList<NutritionWidget> widgetsFromDB = (ArrayList<NutritionWidget>) nutritionWidgetsQB.orderBy("position", true).query();

                            /**
                             * Get the sublist from the above list
                             */
                            ArrayList<NutritionWidget> subNutritionWidgetsList = new ArrayList<>(widgetsFromDB.subList(0, 4));

                            /**
                             * Assign to the original widgetsMap to be used when accessing the Options menu to re-order widgets
                             */
                            widgetsMap = subNutritionWidgetsList;

                            setUpWidgetsGridView(subNutritionWidgetsList);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<UiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed");
            }
        });
    }

    private boolean checkIfFieldExists(int widget_id) {
        try {
            List<NutritionWidget> nw = nutritionWidgetsQB.where().eq("widget_id", widget_id).query();

            return !nw.isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
        intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Cons.EXTRAS_NUTRITION_FRAGMENT);
        intent.putParcelableArrayListExtra(Cons.BUNDLE_NUTRITION_WIDGETS_MAP, widgetsMap);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    private void setUpWidgetsGridView(ArrayList<NutritionWidget> widgetsMap) {
        nutritionWidgets_GridAdapter = new NutritionWidgets_GridAdapter(getActivity(), widgetsMap, R.layout.grid_item_nutrition_widgets);

        widgetsGridView.setAdapter(nutritionWidgets_GridAdapter);
    }

    @com.squareup.otto.Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EXTRAS_PICKED_MEAL_ENTRY:
                if (ebp.getMealItemExtra() != null) {
                    switch (ebp.getMealItemExtra().getType()) {
                        case "Breakfast":
                            addMealEntryLayout(ebp.getMealItemExtra().getName(), entriesContainerLayout_BREAKFAST);
                            break;
                        case "Lunch":
                            addMealEntryLayout(ebp.getMealItemExtra().getName(), entriesContainerLayout_LUNCH);
                            break;
                        case "Dinner":
                            addMealEntryLayout(ebp.getMealItemExtra().getName(), entriesContainerLayout_DINNER);
                            break;
                        case "Snacks":
                            addMealEntryLayout(ebp.getMealItemExtra().getName(), entriesContainerLayout_SNACKS);
                            break;
                    }
                }

                break;
            case Cons.EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getNutritionWidgetsMap() != null) {
                    widgetsMap = ebp.getNutritionWidgetsMap();
                    setUpWidgetsGridView(ebp.getNutritionWidgetsMap());
                }

//                int[] widgets = new int[0], positions = new int[0];
//
//                for (int i = 0; i < widgetsMap.size(); i++) {
//                    widgets[i] = ((ParcelableNutritionString) widgetsMap.get(i)).get;
//                    positions[i] = i;
//                }
                break;
        }
    }

//    private void updateUserWidgets() {
//
//        Call<UiResponse> getUiForSectionCall = new RestClient().getGMFitService().updateUserWidgets(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
//                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), );
//
//        getUiForSectionCall.enqueue(new Callback<UiResponse>() {
//            @Override
//            public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
//
//                switch (response.code()) {
//                    case 200:
//                        prefs.edit().putBoolean(Cons.EXTRAS_USER_LOGGED_IN, true).apply();
//                        prefs.edit().putBoolean(prefs.getString(Cons.EXTRAS_USER_EMAIL, "") + "_" + Cons.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, true).apply();
//
//                        List<AuthenticationResponseWidget> widgetsMap = response.body().getData().getBody().getWidgets();
//                        List<AuthenticationResponseChart> chartsMap = response.body().getData().getBody().getCharts();
//
//                        Intent intent = new Intent(getActivity(), Main_Activity.class);
//                        intent.putParcelableArrayListExtra("widgets", (ArrayList<AuthenticationResponseWidget>) widgetsMap);
//                        intent.putParcelableArrayListExtra("charts", (ArrayList<AuthenticationResponseChart>) chartsMap);
//                        startActivity(intent);
//
//                        getActivity().finish();
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UiResponse> call, Throwable t) {
//                Log.d(TAG, "onFailure: Failed");
//            }
//        });
//    }

    public void handleScanMealEntry() {
        Intent intent = new Intent(getActivity(), BarcodeCapture_Activity.class);
        intent.putExtra(BarcodeCapture_Activity.AutoFocus, true);
        intent.putExtra(BarcodeCapture_Activity.UseFlash, false);

        startActivityForResult(intent, BARCODE_CAPTURE_RC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String scanContent;

        switch (requestCode) {
            case ADD_NEW_NUTRITION_CHART_REQUEST:
                if (data != null) {
                    addNewBarChart(data.getStringExtra(Cons.EXTRAS_CHART_FULL_NAME));
                }
                break;
            case BARCODE_CAPTURE_RC:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(BarcodeCapture_Activity.BarcodeObject);
                        scanContent = barcode.displayValue;

                        Toast.makeText(getActivity(), "Barcode value: " + scanContent, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_barcode_detected_here), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void openMealEntryPickerActivity(String mainMealName) {
        Intent intent = new Intent(parentActivity, AddNewMealItem_Activity.class);
        intent.putExtra(Cons.EXTRAS_MAIN_MEAL_NAME, mainMealName);
        startActivity(intent);
    }

    private void addMealEntryLayout(String mealTitle, LinearLayout targetLayoutForMeal) {
        final View mealEntryLayout = parentActivity.getLayoutInflater().inflate(R.layout.list_item_new_meal_entry, null);

        TextView entryTitleTV = (TextView) mealEntryLayout.findViewById(R.id.entryTitleTV);
        TextView entryDescription = (TextView) mealEntryLayout.findViewById(R.id.entryDescriptionTV);
        TextView entryUnitsTV = (TextView) mealEntryLayout.findViewById(R.id.entryUnitsTV);

        entryTitleTV.setText(mealTitle);
        entryDescription.setText("This is a description");
        entryUnitsTV.setText("400 mg");

        targetLayoutForMeal.addView(mealEntryLayout);
    }

    private void addNewBarChart(String chartTitle) {
        final View barChartLayout = parentActivity.getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        //TODO: apply the same change you did here as in the Fitness Fragment

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.chart_height_2));
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_2);
        barChartLayout.setLayoutParams(lp);

        cards_container.addView(barChartLayout);

        parentScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
    }

}