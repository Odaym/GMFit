package com.mcsaatchi.gmfit.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.adapters.Widgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.ParcelableFitnessString;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fitness_Fragment extends Fragment {

    public static final String TAG = "GMFit";
    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.bar_chart)
    HorizontalBarChart defaultBarChart;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.metricCounterTV)
    TextView metricCounterTV;

    private Value lastKnownValueForStepCount;
    private NestedScrollView parentScrollView;
    private List<DataChart> allDataCharts;
    private RuntimeExceptionDao<DataChart, Integer> dataChartDAO;
    private GoogleApiClient googleApiFitnessClient;
    private OnDataPointListener mListener;
    private Activity parentActivity;
    private View fragmentView;
    private DecimalFormat dFormat = new DecimalFormat("#.00");
    private SharedPreferences prefs;
    private String chartType;
    private String chartName;

    private ArrayList<Integer> itemIndeces = new ArrayList<>();
    private ParcelableSparseArray orderedItemsMap = new ParcelableSparseArray();
    private Widgets_GridAdapter widgets_GridAdapter;

    private ParcelableSparseArray widgetsMap = new ParcelableSparseArray() {{
        put(0, new ParcelableFitnessString(R.drawable.ic_running, 0.0, "Walking"));
        put(1, new ParcelableFitnessString(R.drawable.ic_biking, 0.0, "Biking"));
        put(2, new ParcelableFitnessString(R.drawable.ic_calories, 0.0, "Calories"));
        put(3, new ParcelableFitnessString(R.drawable.ic_steps, 0.0, "Stairs"));
    }};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
            dataChartDAO = ((Base_Activity) parentActivity).getDBHelper().getDataChartDAO();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus_Singleton.getInstance().register(this);

        getActivity().setTitle(R.string.fitness_tab_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        Log.d(TAG, "onCreateView: Device info : " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") - "
                + Build.VERSION.RELEASE);

        Helpers.temporarySetHorizontalChartData(defaultBarChart, 20, 20);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_FITNESS_CHART);
                startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        setUpWidgetsGridView(widgetsMap);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                allDataCharts = dataChartDAO.queryForAll();
//
//                if (!allDataCharts.isEmpty()) {
//                    parentActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (DataChart chart :
//                                    allDataCharts) {
//                                addNewBarChart(chart.getName(), ebp.getFloatArrayExtra()());
//                            }
//                        }
//                    });
//                }
//            }
//        }).start();

        return fragmentView;
    }

    public void addNewBarChart(String chartTitle, ArrayList<Float> floatArrayExtra) {
        final View barChartLayout_NEW_CHART = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        Button removeChartBTN_NEW_CHART = (Button) barChartLayout_NEW_CHART.findViewById(R.id.removeChartBTN);
        final CardView cardLayout_NEW_CHART = (CardView) barChartLayout_NEW_CHART.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV_NEW_CHART = (TextView) barChartLayout_NEW_CHART.findViewById(R.id.chartTitleTV);
        BarChart barChart_NEW_CHART = (BarChart) barChartLayout_NEW_CHART.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV_NEW_CHART.setText(chartTitle);

        Helpers.setBarChartData(barChart_NEW_CHART, floatArrayExtra);

        removeChartBTN_NEW_CHART.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards_container.removeView(cardLayout_NEW_CHART);
            }
        });

        barChartLayout_NEW_CHART.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .chart_height)));

        cards_container.addView(barChartLayout_NEW_CHART);

        parentScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
    }

    private void setUpWidgetsGridView(ParcelableSparseArray widgetsMap) {
        widgets_GridAdapter = new Widgets_GridAdapter(getActivity(), widgetsMap, R.layout.grid_item_fitness_widgets);

        widgetsGridView.setAdapter(widgets_GridAdapter);
    }

    private TextView findFitnessWidgetInGrid() {
        View fitnessWidgetView;
        TextView stepCountTextView = null;

        for (int i = 0; i < widgets_GridAdapter.getCount(); i++) {
            if (widgets_GridAdapter.getItem(i).getTitle().equals("Stairs")) {
                final int firstListItemPosition = widgetsGridView.getFirstVisiblePosition();
                final int lastListItemPosition = firstListItemPosition + widgetsGridView.getChildCount() - 1;

                if (i < firstListItemPosition || i > lastListItemPosition) {
                    fitnessWidgetView = widgetsGridView.getAdapter().getView(i, null, widgetsGridView);
                } else {
                    final int childIndex = i - firstListItemPosition;
                    fitnessWidgetView = widgetsGridView.getChildAt(childIndex);
                }

                stepCountTextView = (TextView) fitnessWidgetView.findViewById(R.id.metricTV);
            }
        }

        return stepCountTextView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_FITNESS_CHART_REQUEST_CODE) {
            if (data != null) {

                chartType = data.getStringExtra(Cons.EXTRAS_CHART_TYPE_SELECTED);
                chartName = data.getStringExtra(Cons.EXTRAS_CHART_FULL_NAME);


                //Add the chart entry to the database
//                dataChartDAO.create(new DataChart(chartName, chartType, dataChartDAO.queryForAll().size() + 1, Cons.EXTRAS_FITNESS_FRAGMENT));

                //Still pending
//                getMetricsForChart();

            } else if (requestCode == Main_Activity.USER_AUTHORISED_REQUEST_CODE && googleApiFitnessClient != null) {
                googleApiFitnessClient.stopAutoManage(getActivity());
                googleApiFitnessClient.disconnect();
                googleApiFitnessClient.connect();
            }
        }
    }

    private void buildFitnessClient() {
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            googleApiFitnessClient = new GoogleApiClient.Builder(parentActivity)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.HISTORY_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            final String caloriesToday = displayCaloriesDataForToday();
                                            final String stepCountToday = displayStepCountForToday();
                                            final String distanceCoveredToday = displayDistanceCoveredForToday();

                                            parentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    metricCounterTV.setText(NumberFormat.getInstance().format(Double.parseDouble(stepCountToday)));

                                                    for (int i = 0; i < widgetsMap.size(); i++) {
                                                        ParcelableFitnessString fitnessWidget = (ParcelableFitnessString) widgetsMap.valueAt(i);

                                                        switch (fitnessWidget.getTitle()) {
                                                            case "Walking":
                                                                fitnessWidget.setValue(Double.parseDouble(distanceCoveredToday));
                                                                break;
                                                            case "Biking":
                                                                break;
                                                            case "Calories":
                                                                fitnessWidget.setValue(Double.parseDouble(caloriesToday));
                                                                break;
                                                            case "Stairs":
                                                                if (lastKnownValueForStepCount != null) {
                                                                    Log.d(TAG, "run: Value is : " + Double.parseDouble(String.valueOf
                                                                            (lastKnownValueForStepCount)));
                                                                    fitnessWidget.setValue(Double.parseDouble(String.valueOf(lastKnownValueForStepCount)));
                                                                }
                                                                break;
                                                        }

                                                        widgets_GridAdapter.notifyDataSetChanged();
                                                    }

                                                    findStepCounterDataSource();

                                                    if (!prefs.getBoolean("SYNCED_METRICS", false)) {
                                                        Log.d(TAG, "run: PREF DOESN'T EXIST, SYNCING METRICS NOW");

                                                        if (Helpers.isInternetAvailable(getActivity())) {
                                                            if (!stepCountToday.isEmpty() && !caloriesToday.isEmpty() && !distanceCoveredToday.isEmpty()) {

                                                                double[] valuesArray = new double[]{Double
                                                                        .parseDouble(stepCountToday), Double.parseDouble(caloriesToday),
                                                                        Double.parseDouble(distanceCoveredToday)};

                                                                updateMetrics(new String[]{"steps-count", "active-calories",
                                                                        "distance-traveled"}, valuesArray, Helpers.getCalendarDate());
                                                            }
                                                        } else {
                                                            Helpers.showNoInternetDialog(getActivity());
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .addOnConnectionFailedListener(
                            new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult connectionResult) {
                                    Log.d(TAG, "Connection failed! " + connectionResult.getErrorMessage());
                                }
                            }
                    )
                    .enableAutoManage((FragmentActivity) parentActivity, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                        }
                    })
                    .build();
        }
    }

    private void getMetricsForChart() {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getString(R.string.syncing_up_dialog_title));
        waitingDialog.setMessage(getString(R.string.syncing_up_dialog_message));
//        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.syncing_up_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<DefaultGetResponse> updateMetricsCall = new RestClient().getGMFitService().getMetricsForChart(prefs.getString(Cons
                .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "2016-06-01", "2016-06-20", "fitness", "");

        updateMetricsCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                if (response.body() != null) {
                    switch (response.code()) {
                        case Cons.API_REQUEST_SUCCEEDED_CODE:
//                            waitingDialog.dismiss();

                            break;
                    }
                } else {
                    if (isVisible()) {
                        waitingDialog.dismiss();

                        //Handle the error
                        try {
                            JSONObject errorBody = new JSONObject(response.errorBody().string());
                            Log.d(TAG, "onResponse: ERROR IS : " + response.errorBody().string());
                            JSONObject errorData = errorBody.getJSONObject("data");
                            int errorCodeInData = errorData.getInt("code");

                            Log.d(TAG, "updateMetrics onResponse: Body error : " + response.errorBody().string());

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    private void updateMetrics(String[] slugsArray, double[] valuesArray, String date) {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getString(R.string.syncing_up_dialog_title));
        waitingDialog.setMessage(getString(R.string.syncing_up_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.syncing_up_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<DefaultGetResponse> updateMetricsCall = new RestClient().getGMFitService().updateMetrics(prefs.getString(Cons
                .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new UpdateMetricsRequest(slugsArray, valuesArray, date));

        updateMetricsCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                if (response.body() != null) {
                    switch (response.code()) {
                        case Cons.API_REQUEST_SUCCEEDED_CODE:
                            waitingDialog.dismiss();

                            prefs.edit().putBoolean("SYNCED_METRICS", true).apply();

                            break;
                    }
                } else {
                    if (isVisible()) {
                        waitingDialog.dismiss();

                        //Handle the error
                        try {
                            JSONObject errorBody = new JSONObject(response.errorBody().string());
                            Log.d(TAG, "onResponse: ERROR IS : " + response.errorBody().string());
                            JSONObject errorData = errorBody.getJSONObject("data");
                            int errorCodeInData = errorData.getInt("code");

                            Log.d(TAG, "updateMetrics onResponse: Body error : " + response.errorBody().string());

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    private void findStepCounterDataSource() {
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.
        Fitness.SensorsApi.findDataSources(googleApiFitnessClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                                    && mListener == null) {
                                Log.i(TAG, "Data source for " + dataSource.getDataType() + " found!  Registering.");
                                registerStepCountDataSource(dataSource,
                                        DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            }
                        }
                    }
                });
    }

    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerStepCountDataSource(DataSource dataSource, DataType dataType) {
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (final Field field : dataPoint.getDataType().getFields()) {
                    final Value val = dataPoint.getValue(field);

                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastKnownValueForStepCount = val;

                            TextView stepCounterTextView = findFitnessWidgetInGrid();

                            if (stepCounterTextView != null)
                                stepCounterTextView.setText(NumberFormat.getInstance().format(Double.parseDouble(String.valueOf(val))));

                            Log.i(TAG, "Found this field : " + field.getName() + " with value : " + val);
                        }
                    });
                }
            }
        };

        Fitness.SensorsApi.add(
                googleApiFitnessClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // Can't be omitted.
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
    }

    public void saveUserHeight(int heightCentimiters) {
        // to post data
        float height = ((float) heightCentimiters) / 100.0f;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet heightDataSet = createDataForRequest(
                DataType.TYPE_HEIGHT,
                DataSource.TYPE_RAW,
                height,
                startTime,
                endTime,
                TimeUnit.MILLISECONDS
        );

        com.google.android.gms.common.api.Status heightInsertStatus =
                Fitness.HistoryApi.insertData(googleApiFitnessClient, heightDataSet)
                        .await(1, TimeUnit.MINUTES);
    }

    public void saveUserWeight(float weight) {
        // to post data
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet weightDataSet = createDataForRequest(
                DataType.TYPE_WEIGHT,
                DataSource.TYPE_RAW,
                weight,
                startTime,
                endTime,
                TimeUnit.MILLISECONDS
        );

        com.google.android.gms.common.api.Status weightInsertStatus =
                Fitness.HistoryApi.insertData(googleApiFitnessClient, weightDataSet)
                        .await(1, TimeUnit.MINUTES);
    }

    public DataSet createDataForRequest(DataType dataType,
                                        int dataSourceType,
                                        Object values,
                                        long startTime,
                                        long endTime,
                                        TimeUnit timeUnit) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(getActivity())
                .setDataType(dataType)
                .setType(dataSourceType)
                .build();

        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, timeUnit);

        if (values instanceof Integer) {
            dataPoint = dataPoint.setIntValues((Integer) values);
        } else {
            dataPoint = dataPoint.setFloatValues((Float) values);
        }

        dataSet.add(dataPoint);

        return dataSet;
    }

    public String displayCaloriesDataForToday() {
        saveUserHeight(180);
        saveUserWeight(79);

        DailyTotalResult resultingMetrics = Fitness.HistoryApi.readDailyTotal(googleApiFitnessClient, DataType.AGGREGATE_CALORIES_EXPENDED).await();

        return showResultingDataPoints(resultingMetrics.getTotal());
    }

    public String displayStepCountForToday() {
        DailyTotalResult resultingMetrics = Fitness.HistoryApi.readDailyTotal(googleApiFitnessClient, DataType.AGGREGATE_STEP_COUNT_DELTA).await();

        return showResultingDataPoints(resultingMetrics.getTotal());
    }

    public String displayDistanceCoveredForToday() {
        DailyTotalResult resultingMetrics = Fitness.HistoryApi.readDailyTotal(googleApiFitnessClient, DataType.AGGREGATE_DISTANCE_DELTA).await();

        return showResultingDataPoints(resultingMetrics.getTotal());
    }

    private String showResultingDataPoints(DataSet caloriesDataSet) {
        Value val;
        String finalValue = null;

        for (DataPoint dp : caloriesDataSet.getDataPoints()) {

            for (Field field : dp.getDataType().getFields()) {
                val = dp.getValue(field);
                Log.i(TAG, "Detected " + dp.getDataType() + " DataPoint field: " + field.getName());
                Log.i(TAG, "Detected " + dp.getDataType() + " DataPoint value: " + val);

                finalValue = dFormat.format(Double.parseDouble(val != null ? val.toString() : null));
            }
        }

        return finalValue;
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(parentActivity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(parentActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(parentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS},
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(parentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildFitnessClient();
                googleApiFitnessClient.connect();

            } else {
                Snackbar.make(
                        fragmentView.findViewById(R.id.main_activity_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getParcelableSparseExtra() != null) {
                    widgetsMap = ebp.getParcelableSparseExtra();
                    setUpWidgetsGridView(ebp.getParcelableSparseExtra());
                }

                break;
            case Cons.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED:
                allDataCharts = ebp.getDataChartsListExtra();

                cards_container.removeAllViews();

                for (DataChart chart :
                        allDataCharts) {
                    addNewBarChart(chart.getName(), ebp.getFloatArrayExtra());
                }

                break;
            case Cons.EVENT_CHART_METRICS_RECEIVED:
                addNewBarChart(chartName, ebp.getFloatArrayExtra());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
                intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE, Cons.EXTRAS_FITNESS_FRAGMENT);
                intent.putExtra(Cons.BUNDLE_FITNESS_WIDGETS_MAP, widgetsMap);

                for (int i = 0; i < widgetsMap.size(); i++) {
                    Log.d("WIDGETS", "onOptionsItemSelected: WHEN OPTIONS SELECTED : " + ((ParcelableFitnessString) widgetsMap.valueAt(i)).getTitle());
                }
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (googleApiFitnessClient != null) {
            Log.d(TAG, "onStop REACHED, client not null and is connected");
            googleApiFitnessClient.stopAutoManage(getActivity());
            googleApiFitnessClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (googleApiFitnessClient != null) {
            Log.d(TAG, "onResume REACHED, client not null");
            googleApiFitnessClient.stopAutoManage(getActivity());
            googleApiFitnessClient.disconnect();
            googleApiFitnessClient.connect();
        } else {
            Log.d(TAG, "onResume REACHED, client null, buildingClient");
            buildFitnessClient();
        }
    }

    public class UpdateMetricsRequest {
        final String[] slug;
        final double[] value;
        final String date;

        public UpdateMetricsRequest(String[] slug, double[] value, String date) {
            this.slug = slug;
            this.value = value;
            this.date = date;
        }
    }
}