package com.mcsaatchi.gmfit.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
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
import com.hookedonplay.decoviewlib.DecoView;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.classes.ApiHelper;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.models.DataChart;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fitness_Fragment extends Fragment {

    public static final String TAG = "GMFit";
    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    @Bind(R.id.bar_chart)
    HorizontalBarChart defaultBarChart;
    @Bind(R.id.dynamicArcView)
    DecoView dynamicArc;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewBarChartBTN;
    @Bind(R.id.metricCounterTV)
    TextView metricCounterTV;
    @Bind(R.id.firstMetricTV)
    TextView firstMetricTV;
    @Bind(R.id.firstMetricIMG)
    ImageView firstMetricIMG;
    @Bind(R.id.secondMetricTV)
    TextView secondMetricTV;
    @Bind(R.id.secondMetricIMG)
    ImageView secondMetricIMG;
    @Bind(R.id.thirdMetricTV)
    TextSwitcher thirdMetricTV;
    @Bind(R.id.thirdMetricIMG)
    ImageView thirdMetricIMG;
    @Bind(R.id.fourthMetricTV)
    TextView fourthMetricTV;
    @Bind(R.id.fourthMetricIMG)
    ImageView fourthMetricIMG;
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

        Helpers.setUpDecoViewArc(getActivity(), dynamicArc);

        Log.d(TAG, "onCreateView: Device info : " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") - "
                + Build.VERSION.RELEASE);

        Helpers.temporarySetHorizontalChartData(defaultBarChart, 20, 20);

        addNewBarChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_FITNESS_CHART);
                startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        setUpMetricCounterTextSwitcherAnimation();

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

    private void setUpMetricCounterTextSwitcherAnimation() {
        thirdMetricTV.setInAnimation(getActivity(), R.anim.fade_in);
        thirdMetricTV.setOutAnimation(getActivity(), R.anim.fade_out);
        TextView textView1 = new TextView(getActivity());
        textView1.setTypeface(null, Typeface.BOLD);
        TextView textView2 = new TextView(getActivity());
        textView2.setTypeface(null, Typeface.BOLD);

        thirdMetricTV.addView(textView1);
        thirdMetricTV.addView(textView2);
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

                ApiHelper.getChartMetricsByDate(getActivity(), chartType);

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

                                    findStepCountDataSource();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final String caloriesToday = displayCaloriesDataForToday();
                                            final String stepCountToday = displayStepCountForToday();
                                            final String distanceCoveredToday = displayDistanceCoveredForToday();

                                            parentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    firstMetricTV.setText(distanceCoveredToday);
                                                    metricCounterTV.setText(stepCountToday);
                                                    secondMetricTV.setText(caloriesToday);

                                                    Log.d(TAG, "run: Prefs SYNCED METRICS is : " + prefs.getBoolean("SYNCED_METRICS", false));

                                                    if (!prefs.getBoolean("SYNCED_METRICS", false)) {

                                                        prefs.edit().putBoolean("SYNCED_METRICS", true).apply();

                                                        Log.d(TAG, "run: NOT SYNCED, SYNCING NOW");

                                                        if (Helpers.isInternetAvailable(getActivity())) {
                                                            try {
                                                                final JSONObject jsonForRequest = new JSONObject();

                                                                final JSONArray slugsArray = new JSONArray(Arrays.asList(new String[]{"steps-count", "active-calories", "distance-traveled"}));
                                                                final JSONArray valuesArray;

                                                                double[] tempValuesArray = new double[]{Double
                                                                        .parseDouble(metricCounterTV.getText().toString()), Double.parseDouble(secondMetricTV.getText().toString()),
                                                                        Double.parseDouble(firstMetricTV.getText().toString())};

                                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                                                    valuesArray = new JSONArray(tempValuesArray);
                                                                } else {
                                                                    valuesArray = new JSONArray(Arrays.asList(tempValuesArray));
                                                                }

                                                                jsonForRequest.put(Cons.REQUEST_PARAM_SLUG, slugsArray);
                                                                jsonForRequest.put(Cons.REQUEST_PARAM_VALUE, valuesArray);
                                                                jsonForRequest.put(Cons.REQUEST_PARAM_DATE, Helpers.getCalendarDate());

                                                                ApiHelper.runApiAsyncTask(getActivity(), Cons.API_NAME_ADD_METRIC, Cons.POST_REQUEST_TYPE, jsonForRequest, R.string
                                                                        .syncing_up_dialog_title, R.string.syncing_up_dialog_message, null);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
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

    private void findStepCountDataSource() {
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
                for (Field field : dataPoint.getDataType().getFields()) {
                    final Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);

                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            thirdMetricTV.setText(NumberFormat.getInstance().format(Double.parseDouble(val.toString())));
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
                if (ebp.getSparseArrayExtra() != null) {
                    SparseArray<String[]> widgetsMap = ebp.getSparseArrayExtra();

                    firstMetricTV.setText(widgetsMap.get(0)[0].split(" ")[0]);
//                    firstMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.walking));

                    secondMetricTV.setText(widgetsMap.get(1)[0].split(" ")[0]);
//                    secondMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.biking));

                    thirdMetricTV.setText(widgetsMap.get(2)[0].split(" ")[0]);
//                    thirdMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.calories));

                    fourthMetricTV.setText(widgetsMap.get(3)[0].split(" ")[0]);
//                    fourthMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.stairs));
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
}