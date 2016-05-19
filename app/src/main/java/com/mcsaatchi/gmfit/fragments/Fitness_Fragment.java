package com.mcsaatchi.gmfit.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.hookedonplay.decoviewlib.DecoView;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidget_Activity;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.squareup.otto.Subscribe;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fitness_Fragment extends Fragment {

    private NestedScrollView parentScrollView;

    @Bind(R.id.dynamicArcView)
    DecoView dynamicArc;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.bar_chart)
    HorizontalBarChart barChart;
    @Bind(R.id.addChartBTN)
    Button addNewBarChartBTN;

    @Bind(R.id.metricCounterTV)
    TextSwitcher metricCounterTV;

    @Bind(R.id.firstMetricTV)
    TextView firstMetricTV;
    @Bind(R.id.firstMetricIMG)
    ImageView firstMetricIMG;
    @Bind(R.id.secondMetricTV)
    TextView secondMetricTV;
    @Bind(R.id.secondMetricIMG)
    ImageView secondMetricIMG;
    @Bind(R.id.thirdMetricTV)
    TextView thirdMetricTV;
    @Bind(R.id.thirdMetricIMG)
    ImageView thirdMetricIMG;
    @Bind(R.id.fourthMetricTV)
    TextView fourthMetricTV;
    @Bind(R.id.fourthMetricIMG)
    ImageView fourthMetricIMG;

    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;

    public static final String TAG = "GMFit";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private GoogleApiClient googleApiFitnessClient;
    private OnDataPointListener mListener;

    private Activity parentActivity;
    private View fragmentView;

    private List<Integer> itemIndeces;
    private SparseArray<String[]> itemsMap;

    private SharedPreferences prefs;

    private final String numberOfStepsChartType = "Number of Steps";
    private final String walkingDistanceChartType = "Walking and Running Distance";
    private final String cyclingDistanceChartType = "Cycling Distance";
    private final String totalDistanceChartType = "Total Distance Traveled";
    private final String flightsClimbedChartType = "Flights Climbed";
    private final String activeCaloriesChartType = "Active Calories";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
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

        prefs = getActivity().getSharedPreferences(Constants.EXTRAS_PREFS, Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        Helpers.setUpDecoViewArc(getActivity(), dynamicArc);

        Helpers.setChartData(barChart, 20, 20);

        addNewBarChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_FITNESS_CHART);
                startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        setUpMetricCounterTextSwitcherAnimation();

        return fragmentView;
    }

    private void setUpMetricCounterTextSwitcherAnimation() {
        metricCounterTV.setInAnimation(getActivity(), R.anim.fade_in);
        metricCounterTV.setOutAnimation(getActivity(), R.anim.fade_out);
        TextView textView1 = new TextView(getActivity());
        textView1.setTextSize(22f);
        textView1.setTypeface(null, Typeface.BOLD);
        textView1.setTextColor(Color.BLACK);
        TextView textView2 = new TextView(getActivity());
        textView2.setTextSize(22f);
        textView2.setTextColor(Color.BLACK);
        textView2.setTypeface(null, Typeface.BOLD);

        metricCounterTV.addView(textView1);
        metricCounterTV.addView(textView2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_FITNESS_CHART_REQUEST_CODE) {
            if (data != null) {
                switch (data.getStringExtra(Constants.EXTRAS_CHART_TYPE_SELECTED)) {
                    case numberOfStepsChartType:
                        addNewBarChart(numberOfStepsChartType);
                        break;
                    case walkingDistanceChartType:
                        addNewBarChart(walkingDistanceChartType);
                        break;
                    case cyclingDistanceChartType:
                        addNewBarChart(cyclingDistanceChartType);
                        break;
                    case totalDistanceChartType:
                        addNewBarChart(totalDistanceChartType);
                        break;
                    case flightsClimbedChartType:
                        addNewBarChart(flightsClimbedChartType);
                        break;
                    case activeCaloriesChartType:
                        addNewBarChart(activeCaloriesChartType);
                        break;
                }
            } else if (requestCode == Main_Activity.USER_AUTHORISED_REQUEST_CODE && googleApiFitnessClient != null) {
                googleApiFitnessClient.stopAutoManage(getActivity());
                googleApiFitnessClient.disconnect();
                googleApiFitnessClient.connect();
            }
        }
    }

    private void buildFitnessClient() {
        googleApiFitnessClient = new GoogleApiClient.Builder(parentActivity)
                .addApi(Fitness.SENSORS_API)
//                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
//                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                findFitnessDataSources();
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

    private void findFitnessDataSources() {
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
                                registerFitnessDataListener(dataSource,
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
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    final Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            metricCounterTV.setText(NumberFormat.getInstance().format(Double.parseDouble(val.toString())));
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
            Snackbar.make(
                    fragmentView.findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(parentActivity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
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

    public void addNewBarChart(String chartTitle) {
        final View barChartLayout = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        Button removeChartBTN = (Button) barChartLayout.findViewById(R.id.removeChartBTN);
        final CardView cardLayout = (CardView) barChartLayout.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        Helpers.setChartData(barChart, 10, 10);

        removeChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards_container.removeView(cardLayout);
            }
        });

        barChartLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.chart_height)));

        cards_container.addView(barChartLayout);

        parentScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Constants.EXTRAS_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getSparseArrayExtra() != null) {
                    itemsMap = ebp.getSparseArrayExtra();

                    firstMetricTV.setText(itemsMap.get(0)[0].split(" ")[0]);
                    firstMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.walking));
                    secondMetricTV.setText(itemsMap.get(1)[0].split(" ")[0]);
                    secondMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.biking));
                    thirdMetricTV.setText(itemsMap.get(2)[0].split(" ")[0]);
                    thirdMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.calories));
                    fourthMetricTV.setText(itemsMap.get(3)[0].split(" ")[0]);
                    fourthMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.stairs));
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), CustomizeWidget_Activity.class);
        startActivity(intent);

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
            googleApiFitnessClient.connect();
        }
    }
}