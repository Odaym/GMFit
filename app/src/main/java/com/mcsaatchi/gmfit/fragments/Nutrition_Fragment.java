package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.AddNewMealItem_Activity;
import com.mcsaatchi.gmfit.activities.BarcodeCapture_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.activities.SlugBreakdown_Activity;
import com.mcsaatchi.gmfit.adapters.NutritionWidgets_GridAdapter;
import com.mcsaatchi.gmfit.adapters.SimpleItemTouchHelperCallback;
import com.mcsaatchi.gmfit.adapters.UserMeals_RecyclerAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionWidget;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.rest.UiResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponseBreakfast;
import com.mcsaatchi.gmfit.rest.UserMealsResponseDinner;
import com.mcsaatchi.gmfit.rest.UserMealsResponseLunch;
import com.mcsaatchi.gmfit.rest.UserMealsResponseSnack;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mcsaatchi.gmfit.classes.Cons.EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED;

public class Nutrition_Fragment extends Fragment {

    public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;
    private static final String TAG = "Nutrition_Fragment";
    private static final int ITEM_VIEWTYPE = 2;
    private static final int BARCODE_CAPTURE_RC = 773;
    private final LocalDate dt = new LocalDate();
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.metricCounterTV)
    FontTextView metricCounterTV;
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
    @Bind(R.id.breakfastListView)
    RecyclerView breakfastListView;
    /**
     * LUNCH CHART
     */
    @Bind(R.id.chartTitleTV_LUNCH)
    TextView chartTitleTV_LUNCH;
    @Bind(R.id.addEntryBTN_LUNCH)
    TextView addNewEntryBTN_LUNCH;
    @Bind(R.id.scanEntryBTN_LUNCH)
    TextView scanEntryBTN_LUNCH;
    @Bind(R.id.lunchListView)
    RecyclerView lunchListView;
    /**
     * DINNER CHART
     */
    @Bind(R.id.chartTitleTV_DINNER)
    TextView chartTitleTV_DINNER;
    @Bind(R.id.addEntryBTN_DINNER)
    TextView addNewEntryBTN_DINNER;
    @Bind(R.id.scanEntryBTN_DINNER)
    TextView scanEntryBTN_DINNER;
    @Bind(R.id.dinnerListView)
    RecyclerView dinnerListView;
    /**
     * SNACKS CHART
     */
    @Bind(R.id.chartTitleTV_SNACKS)
    TextView chartTitleTV_SNACKS;
    @Bind(R.id.addEntryBTN_SNACKS)
    TextView addNewEntryBTN_SNACKS;
    @Bind(R.id.scanEntryBTN_SNACKS)
    TextView scanEntryBTN_SNACKS;
    @Bind(R.id.snacksListView)
    RecyclerView snacksListView;
    /**
     * ADD CHART BUTTON
     */
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.loadingProgressBar)
    ProgressBar loadingProgressBar;
    private UserMeals_RecyclerAdapter userMealsRecyclerAdapter;
    private ArrayList<NutritionWidget> widgetsMap;
    private SharedPreferences prefs;
    private ArrayList<MealItem> finalBreakfastMeals;
    private ArrayList<MealItem> finalLunchmeals;
    private ArrayList<MealItem> finalDinnerMeals;
    private ArrayList<MealItem> finalSnackMeals;

    private ArrayList<NutritionWidget> finalWidgets;
    private ArrayList<DataChart> finalCharts;

    /**
     * TOP LAYOUT WITH WIDGETS
     */
    private Activity parentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (Activity) context;

        EventBus_Singleton.getInstance().register(this);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nutrition_tab_title);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        hookupMealSectionRowsClickListeners();

        getUserAddedMeals();

        getUiForSection("nutrition");

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_NUTRIITION_CHART);
                startActivityForResult(intent,
                        ADD_NEW_NUTRITION_CHART_REQUEST);
            }
        });

        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
        intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Cons.EXTRAS_NUTRITION_FRAGMENT);
        intent.putParcelableArrayListExtra(Cons.BUNDLE_NUTRITION_WIDGETS_MAP, widgetsMap);
        intent.putParcelableArrayListExtra(Cons.BUNDLE_NUTRITION_CHARTS_MAP, finalCharts);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String scanContent;

        switch (requestCode) {
            case ADD_NEW_NUTRITION_CHART_REQUEST:
                if (data != null) {
                    String chartTitle = data.getStringExtra(Cons.EXTRAS_CHART_FULL_NAME);

                    addNewBarChart(chartTitle);
//TODO:
//                    dataChartDAO.create(new DataChart(chartTitle, "", dataChartDAO.queryForAll().size() + 1, userEmail, Cons.EXTRAS_NUTRITION_FRAGMENT));
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

    private void getUiForSection(String section) {
        DataAccessHandler.getInstance().getUiForSection(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section, new Callback<UiResponse>() {
            @Override
            public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
                switch (response.code()) {
                    case 200:
                        List<AuthenticationResponseWidget> widgetsMapFromAPI = response.body().getData().getBody().getWidgets();
                        List<AuthenticationResponseChart> chartsMapFromAPI = response.body().getData().getBody().getCharts();

                        /**
                         * Update or create widgets and datacharts into the DB
                         */
                        finalWidgets = new ArrayList<>();

                        for (int i = 0; i < widgetsMapFromAPI.size(); i++) {
                            NutritionWidget nutritionWidget = new NutritionWidget();
                            nutritionWidget.setTitle(widgetsMapFromAPI.get(i).getName());
                            nutritionWidget.setPosition(Integer.parseInt(widgetsMapFromAPI.get(i).getPosition()));
                            nutritionWidget.setMeasurementUnit(widgetsMapFromAPI.get(i).getUnit());
                            nutritionWidget.setWidget_id(widgetsMapFromAPI.get(i).getWidgetId());
                            nutritionWidget.setValue(Double.parseDouble(widgetsMapFromAPI.get(i).getTotal()));

                            finalWidgets.add(nutritionWidget);
                        }

                        finalCharts = new ArrayList<>();

                        for (int i = 0; i < chartsMapFromAPI.size(); i++) {
                            DataChart nutritionDataChart = new DataChart();
                            nutritionDataChart.setName(chartsMapFromAPI.get(i).getName());
                            nutritionDataChart.setPosition(Integer.parseInt(chartsMapFromAPI.get(i).getPosition()));
                            nutritionDataChart.setType(chartsMapFromAPI.get(i).getSlug());
                            nutritionDataChart.setUsername(chartsMapFromAPI.get(i).getSlug());
                            nutritionDataChart.setChart_id(chartsMapFromAPI.get(i).getChartId());
                            nutritionDataChart.setChartData((ArrayList<AuthenticationResponseChartData>) chartsMapFromAPI.get(i).getData());

                            finalCharts.add(nutritionDataChart);
                        }

                        setupWidgetViews(finalWidgets);

                        setupChartViews(finalCharts);

                        break;
                }
            }

            @Override
            public void onFailure(Call<UiResponse> call, Throwable t) {

            }
        });
    }

    private void updateUserWidgets(int[] widgetIds, int[] widgetPositions) {
        DataAccessHandler.getInstance().updateUserWidgets(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), widgetIds, widgetPositions, new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        Log.d(TAG, "onResponse: User's widgets updated successfully");
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

            }
        });
    }

    private void updateUserCharts(int[] chartIds, int[] chartPositions) {
        DataAccessHandler.getInstance().updateUserCharts(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), chartIds, chartPositions, new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        Log.d(TAG, "onResponse: User's charts updated successfully");
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

            }
        });
    }

    private void getUserAddedMeals() {
        DataAccessHandler.getInstance().getUserAddedMeals(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<UserMealsResponse>() {
            @Override
            public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
                switch (response.code()) {
                    case 200:

                        /**
                         * Grab all meals from the API
                         */
                        List<UserMealsResponseBreakfast> breakfastMeals = response.body().getData().getBody().getData().getBreakfast();
                        List<UserMealsResponseLunch> lunchMeals = response.body().getData().getBody().getData().getLunch();
                        List<UserMealsResponseDinner> dinnerMeals = response.body().getData().getBody().getData().getDinner();
                        List<UserMealsResponseSnack> snackMeals = response.body().getData().getBody().getData().getSnack();

                        /**
                         * Insert Breakfast meals
                         */
                        finalBreakfastMeals = new ArrayList<>();

                        for (int i = 0; i < breakfastMeals.size(); i++) {
                            MealItem breakfastMeal = new MealItem();
                            breakfastMeal.setMeal_id(breakfastMeals.get(i).getId());
                            breakfastMeal.setInstance_id(breakfastMeals.get(i).getInstance_id());
                            breakfastMeal.setType("Breakfast");
                            breakfastMeal.setName(breakfastMeals.get(i).getName());
                            breakfastMeal.setMeasurementUnit(breakfastMeals.get(i).getMeasurementUnit());
                            breakfastMeal.setAmount(breakfastMeals.get(i).getAmount());
                            breakfastMeal.setSectionType(2);

                            if (breakfastMeals.get(i).getTotalCalories() != null)
                                breakfastMeal.setTotalCalories(breakfastMeals.get(i).getTotalCalories());
                            else
                                breakfastMeal.setTotalCalories(0);

                            finalBreakfastMeals.add(breakfastMeal);
                        }

                        setupMealSectionsListView(finalBreakfastMeals, "Breakfast");

                        /**
                         * Insert Lunch meals
                         */
                        finalLunchmeals = new ArrayList<>();

                        for (int i = 0; i < lunchMeals.size(); i++) {
                            MealItem lunchMeal = new MealItem();
                            lunchMeal.setMeal_id(lunchMeals.get(i).getId());
                            lunchMeal.setInstance_id(lunchMeals.get(i).getInstance_id());
                            lunchMeal.setType("Lunch");
                            lunchMeal.setName(lunchMeals.get(i).getName());
                            lunchMeal.setMeasurementUnit(lunchMeals.get(i).getMeasurementUnit());
                            lunchMeal.setAmount(lunchMeals.get(i).getAmount());
                            lunchMeal.setSectionType(2);

                            if (lunchMeals.get(i).getTotalCalories() != null)
                                lunchMeal.setTotalCalories(lunchMeals.get(i).getTotalCalories());
                            else
                                lunchMeal.setTotalCalories(0);

                            finalLunchmeals.add(lunchMeal);
                        }

                        setupMealSectionsListView(finalLunchmeals, "Lunch");

                        /**
                         * Insert Dinner meals
                         */
                        finalDinnerMeals = new ArrayList<>();

                        for (int i = 0; i < dinnerMeals.size(); i++) {
                            MealItem dinnerMeal = new MealItem();
                            dinnerMeal.setMeal_id(dinnerMeals.get(i).getId());
                            dinnerMeal.setInstance_id(dinnerMeals.get(i).getInstance_id());
                            dinnerMeal.setType("Dinner");
                            dinnerMeal.setName(dinnerMeals.get(i).getName());
                            dinnerMeal.setMeasurementUnit(dinnerMeals.get(i).getMeasurementUnit());
                            dinnerMeal.setAmount(dinnerMeals.get(i).getAmount());
                            dinnerMeal.setSectionType(2);

                            if (dinnerMeals.get(i).getTotalCalories() != null)
                                dinnerMeal.setTotalCalories(dinnerMeals.get(i).getTotalCalories());
                            else
                                dinnerMeal.setTotalCalories(0);

                            finalDinnerMeals.add(dinnerMeal);
                        }

                        setupMealSectionsListView(finalDinnerMeals, "Dinner");

                        /**
                         * Insert Snack meals
                         */
                        finalSnackMeals = new ArrayList<>();

                        for (int i = 0; i < snackMeals.size(); i++) {
                            MealItem snackMeal = new MealItem();
                            snackMeal.setMeal_id(snackMeals.get(i).getId());
                            snackMeal.setInstance_id(snackMeals.get(i).getInstance_id());
                            snackMeal.setType("Snack");
                            snackMeal.setName(snackMeals.get(i).getName());
                            snackMeal.setMeasurementUnit(snackMeals.get(i).getMeasurementUnit());
                            snackMeal.setAmount(snackMeals.get(i).getAmount());
                            snackMeal.setSectionType(2);

                            if (snackMeals.get(i).getTotalCalories() != null)
                                snackMeal.setTotalCalories(snackMeals.get(i).getTotalCalories());
                            else
                                snackMeal.setTotalCalories(0);

                            finalSnackMeals.add(snackMeal);
                        }

                        setupMealSectionsListView(finalSnackMeals, "Snack");

                        break;
                }
            }

            @Override
            public void onFailure(Call<UserMealsResponse> call, Throwable t) {

            }
        });
    }

    private void hookupMealSectionRowsClickListeners() {
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
    }

    private void setupWidgetViews(ArrayList<NutritionWidget> widgetsFromResponse) {

        for (int i = 0; i < widgetsFromResponse.size(); i++) {
            if (widgetsFromResponse.get(i).getTitle().equals("Calories")) {
                metricCounterTV.setText(NumberFormat.getNumberInstance(Locale.US).format((int) widgetsFromResponse.get(i).getValue()));
            }
        }

        loadingProgressBar.setVisibility(View.GONE);

        widgetsMap = new ArrayList<>(widgetsFromResponse.subList(0, 4));

        NutritionWidgets_GridAdapter nutritionWidgets_GridAdapter = new NutritionWidgets_GridAdapter(getActivity(), widgetsMap, R.layout.grid_item_nutrition_widgets);

        widgetsGridView.setAdapter(nutritionWidgets_GridAdapter);
    }

    private void setupChartViews(ArrayList<DataChart> chartsMap) {
        cards_container.removeAllViews();

        if (!chartsMap.isEmpty()) {
            for (DataChart chart :
                    chartsMap) {

                addNewBarChart(chart.getName());
            }
        }
    }

    private void setupMealSectionsListView(ArrayList<MealItem> mealItems, String mealType) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        ItemTouchHelper.Callback callback;
        ItemTouchHelper touchHelper;

        userMealsRecyclerAdapter = new UserMeals_RecyclerAdapter(getActivity(), mealItems);
        callback = new SimpleItemTouchHelperCallback(userMealsRecyclerAdapter);
        touchHelper = new ItemTouchHelper(callback);

        switch (mealType) {
            case "Breakfast":
                hookUpMealSectionListViews(breakfastListView, mLayoutManager, touchHelper);
                break;
            case "Lunch":
                hookUpMealSectionListViews(lunchListView, mLayoutManager, touchHelper);
                break;
            case "Dinner":
                hookUpMealSectionListViews(dinnerListView, mLayoutManager, touchHelper);
                break;
            case "Snack":
                hookUpMealSectionListViews(snacksListView, mLayoutManager, touchHelper);
                break;
        }
    }

    private void hookUpMealSectionListViews(RecyclerView mealListView, RecyclerView.LayoutManager layoutManager, ItemTouchHelper touchHelper) {
        mealListView.setLayoutManager(layoutManager);
        mealListView.setAdapter(userMealsRecyclerAdapter);
        touchHelper.attachToRecyclerView(mealListView);
    }

    @Subscribe
    public void handle_BusEvents(final EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EXTRAS_PICKED_MEAL_ENTRY:
                if (ebp.getMealItemExtra() != null) {
                    MealItem chosenMealFromList = ebp.getMealItemExtra();

                    MealItem newMealItem = new MealItem();
                    newMealItem.setMeal_id(chosenMealFromList.getMeal_id());
                    newMealItem.setTotalCalories(chosenMealFromList.getTotalCalories());
                    newMealItem.setAmount(chosenMealFromList.getAmount());
                    newMealItem.setMeasurementUnit(chosenMealFromList.getMeasurementUnit());
                    newMealItem.setName(chosenMealFromList.getName());
                    newMealItem.setSectionType(ITEM_VIEWTYPE);

                    switch (chosenMealFromList.getType()) {
                        case "Breakfast":

                            newMealItem.setType("Breakfast");

                            finalBreakfastMeals.add(newMealItem);

                            setupMealSectionsListView(finalBreakfastMeals, chosenMealFromList.getType());

                            break;
                        case "Lunch":
                            newMealItem.setType("Lunch");

                            finalLunchmeals.add(newMealItem);

                            setupMealSectionsListView(finalLunchmeals, chosenMealFromList.getType());

                            break;
                        case "Dinner":
                            newMealItem.setType("Dinner");

                            finalDinnerMeals.add(newMealItem);

                            setupMealSectionsListView(finalDinnerMeals, chosenMealFromList.getType());

                            break;
                        case "Snack":
                            newMealItem.setType("Snack");

                            finalSnackMeals.add(newMealItem);

                            setupMealSectionsListView(finalSnackMeals, chosenMealFromList.getType());

                            break;
                    }
                }

                break;
            case Cons.EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getNutritionWidgetsMap() != null) {
                    widgetsMap = ebp.getNutritionWidgetsMap();
                    setupWidgetViews(ebp.getNutritionWidgetsMap());

                    int[] widgets = new int[widgetsMap.size()];
                    int[] positions = new int[widgetsMap.size()];

                    for (int i = 0; i < widgetsMap.size(); i++) {
                        widgets[i] = widgetsMap.get(i).getWidget_id();
                        positions[i] = widgetsMap.get(i).getPosition();
                    }

                    updateUserWidgets(widgets, positions);
                }

                break;
            case EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED:
                List<DataChart> allDataCharts = ebp.getDataChartsListExtra();

                cards_container.removeAllViews();

                for (DataChart chart :
                        allDataCharts) {
                    addNewBarChart(chart.getName());
                }

                int[] charts = new int[allDataCharts.size()];
                int[] chartPositions = new int[allDataCharts.size()];

                for (int i = 0; i < allDataCharts.size(); i++) {
                    charts[i] = allDataCharts.get(i).getChart_id();
                    chartPositions[i] = allDataCharts.get(i).getPosition();
                }

                updateUserCharts(charts, chartPositions);

                break;
            case Cons.EXTRAS_NUTRITION_CHART_DELETED:

                break;
        }
    }

    public void handleScanMealEntry() {
        Intent intent = new Intent(getActivity(), BarcodeCapture_Activity.class);
        intent.putExtra(BarcodeCapture_Activity.AutoFocus, true);
        intent.putExtra(BarcodeCapture_Activity.UseFlash, false);

        startActivityForResult(intent, BARCODE_CAPTURE_RC);
    }

    private void openMealEntryPickerActivity(String mainMealName) {
        Intent intent = new Intent(parentActivity, AddNewMealItem_Activity.class);
        intent.putExtra(Cons.EXTRAS_MAIN_MEAL_NAME, mainMealName);
        startActivity(intent);
    }

    private void addNewBarChart(final String chartTitle) {
        final TextView dateTV_1, dateTV_2, dateTV_3, dateTV_4;

        final View barChartLayout = parentActivity.getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        dateTV_1 = (TextView) barChartLayout.findViewById(R.id.dateTV_1);
        dateTV_2 = (TextView) barChartLayout.findViewById(R.id.dateTV_2);
        dateTV_3 = (TextView) barChartLayout.findViewById(R.id.dateTV_3);
        dateTV_4 = (TextView) barChartLayout.findViewById(R.id.dateTV_4);

        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        getDefaultChartMonthlyBreakdown(barChart, dateTV_1, dateTV_2, dateTV_3, dateTV_4, chartTitle);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.chart_height_2));
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_2);
        barChartLayout.setLayoutParams(lp);

        cards_container.addView(barChartLayout);

        /**
         * Open the breakdown for the chart
         */
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSlugBreakdownForChart(chartTitle, chartTitle);
            }
        });
    }

    private void getSlugBreakdownForChart(final String chartTitle, final String chartType) {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getActivity().getResources().getString(R.string.grabbing_breakdown_data_dialog_title));
        waitingDialog.setMessage(getActivity().getResources().getString(R.string.grabbing_breakdown_data_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.grabbing_breakdown_data_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        DataAccessHandler.getInstance().getSlugBreakdownForChart(chartType, prefs, new Callback<SlugBreakdownResponse>() {
            @Override
            public void onResponse(Call<SlugBreakdownResponse> call, Response<SlugBreakdownResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        Intent intent = new Intent(getActivity(), SlugBreakdown_Activity.class);
                        intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Cons.EXTRAS_NUTRITION_FRAGMENT);
                        intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, chartTitle);
                        intent.putExtra(Cons.EXTRAS_CHART_TYPE_SELECTED, chartType);
                        intent.putExtra(Cons.BUNDLE_SLUG_BREAKDOWN_DATA, response.body().getData().getBody().getData());
                        getActivity().startActivity(intent);
                        break;
                }
            }

            @Override
            public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
                alertDialog.setMessage(getActivity().getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    private void getDefaultChartMonthlyBreakdown(final BarChart barchart, final TextView dateTV_1, final TextView dateTV_2, final TextView dateTV_3, final TextView dateTV_4, final String chart_slug) {
        final String todayDate;
        todayDate = dt.toString();

        DataAccessHandler.getInstance().getPeriodicalChartData(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
                dt.minusMonths(1).toString(), todayDate, "nutrition", chart_slug, new Callback<ChartMetricBreakdownResponse>() {
                    @Override
                    public void onResponse(Call<ChartMetricBreakdownResponse> call, Response<ChartMetricBreakdownResponse> response) {
                        List<ChartMetricBreakdownResponseDatum> rawChartData = response.body().getData().getBody().getData();

                        if (rawChartData != null && rawChartData.size() > 0) {
                            List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

                            for (int i = 0; i < rawChartData.size(); i++) {
                                newChartData.add(new AuthenticationResponseChartData(rawChartData.get(i).getDate(), rawChartData.get(i).getValue()));
                            }

                            for (int i = 0; i < newChartData.size(); i++) {

                                if (i % 7 == 0) {
                                    DateTime date = new DateTime(newChartData.get(i).getDate());

                                    if (i == 7) {
                                        dateTV_4.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                    } else if (i == 14) {
                                        dateTV_3.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                    } else if (i == 21) {
                                        dateTV_2.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                    } else if (i == 28) {
                                        dateTV_1.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                    }
                                }
                            }

                            Helpers.setBarChartData(barchart, newChartData);
                        }
                    }

                    @Override
                    public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
                        //TODO: Add failure code
                    }
                });
    }
}