package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.activities.SlugBreakdown_Activity;
import com.mcsaatchi.gmfit.adapters.FitnessWidgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.FitnessWidget;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fitness_Fragment extends Fragment implements SensorEventListener {

    public static final String TAG = "Fitness_Fragment";
    private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
    private final LocalDate dt = new LocalDate();
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.metricCounterTV)
    FontTextView metricCounterTV;
    private boolean setDrawChartValuesEnabled = false;
    private Activity parentActivity;
    private SharedPreferences prefs;
    private FitnessWidgets_GridAdapter widgets_GridAdapter;
    private String chartName;
    private String chartType;
    private RuntimeExceptionDao<DataChart, Integer> dataChartDAO;

    private ArrayList<FitnessWidget> widgetsMap;
    private ArrayList<DataChart> chartsMap;

    private String userEmail;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;

            RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO = ((Base_Activity) parentActivity).getDBHelper().getFitnessWidgetsDAO();
            QueryBuilder<FitnessWidget, Integer> fitnessQB = fitnessWidgetsDAO.queryBuilder();

            dataChartDAO = ((Base_Activity) parentActivity).getDBHelper().getDataChartDAO();

            EventBus_Singleton.getInstance().register(this);

            JodaTimeAndroid.init(getActivity());

            SensorManager sm =
                    (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (sensor == null) {
                new AlertDialog.Builder(getActivity()).setTitle("Cannot count steps")
                        .setMessage("Your device isn't equipped with this sensor")
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(final DialogInterface dialogInterface) {
//                                getActivity().finish();
                            }
                        }).setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            } else {
                sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
            }

            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fitness_tab_title);

            prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE
                    , Context.MODE_PRIVATE);

            userEmail = prefs.getString(Constants.EXTRAS_USER_EMAIL, "");

            if (getArguments() != null) {
                chartsMap = getArguments().getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);
            }

            try {
                widgetsMap = (ArrayList<FitnessWidget>) fitnessQB.orderBy("position", true).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        metricCounterTV.setText(String.valueOf(prefs.getInt(Helpers.getTodayDate() + "_steps", 0)));

        setupWidgetViews(widgetsMap);

        setupChartViews(chartsMap);

        Log.d(TAG, "onCreateView: Device info : " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") - "
                + Build.VERSION.RELEASE);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_FITNESS_CHART);
                startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        return fragmentView;
    }

    private void getDefaultChartMonthlyBreakdown(final BarChart barchart, final TextView dateTV_1, final TextView dateTV_2, final TextView dateTV_3, final TextView dateTV_4, final TextView dateTV_5, final String chart_slug) {
        final String todayDate;
        todayDate = dt.toString();

        DataAccessHandler.getInstance().getPeriodicalChartData(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
                dt.minusMonths(1).toString(), todayDate, "fitness", chart_slug, new Callback<ChartMetricBreakdownResponse>() {
                    @Override
                    public void onResponse(Call<ChartMetricBreakdownResponse> call, Response<ChartMetricBreakdownResponse> response) {
                        if (response.body() != null) {
                            List<ChartMetricBreakdownResponseDatum> rawChartData = response.body().getData().getBody().getData();

                            if (rawChartData != null && rawChartData.size() > 0) {
                                List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

                                for (int i = 0; i < rawChartData.size(); i++) {
                                    newChartData.add(new AuthenticationResponseChartData(rawChartData.get(i).getDate(), rawChartData.get(i).getValue()));
                                }

                                DateTime date;

                                Collections.reverse(newChartData);

                                for (int i = 0; i < newChartData.size(); i++) {
                                    date = new DateTime(newChartData.get(i).getDate());

                                    switch (i) {
                                        case 0:
                                            dateTV_1.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                            break;
                                        case 7:
                                            dateTV_2.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                            break;
                                        case 14:
                                            dateTV_3.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                            break;
                                        case 21:
                                            dateTV_4.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                            break;
                                    }

                                    if (i == newChartData.size() - 1) {
                                        dateTV_5.setText(date.monthOfYear().getAsText().substring(0, 3) + "-" + date.getDayOfMonth());
                                    }
                                }

                                Helpers.setBarChartData(barchart, newChartData);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
                        //TODO: Add failure code
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Added a new chart for Fitness, get the chart details from the result and use them to actually create that chart in the UI
         * Save it in the DB under the user's email
         */
        if (requestCode == ADD_NEW_FITNESS_CHART_REQUEST_CODE) {
            if (data != null) {

                chartType = data.getStringExtra(Constants.EXTRAS_CHART_TYPE_SELECTED);
                chartName = data.getStringExtra(Constants.EXTRAS_CHART_FULL_NAME);

                addNewBarChart(chartName, chartType);

                dataChartDAO.create(new DataChart(chartName, chartType, dataChartDAO.queryForAll().size() + 1, userEmail, Constants.EXTRAS_FITNESS_FRAGMENT));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
                intent.putExtra(Constants.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Constants.EXTRAS_FITNESS_FRAGMENT);
                intent.putExtra(Constants.BUNDLE_FITNESS_WIDGETS_MAP, widgetsMap);
                intent.putExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, chartsMap);
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

    public void addNewBarChart(final String chartTitle, final String chartType) {
        final TextView dateTV_1, dateTV_2, dateTV_3, dateTV_4, dateTV_5;

        final View barChartLayout = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        dateTV_1 = (TextView) barChartLayout.findViewById(R.id.dateTV_1);
        dateTV_2 = (TextView) barChartLayout.findViewById(R.id.dateTV_2);
        dateTV_3 = (TextView) barChartLayout.findViewById(R.id.dateTV_3);
        dateTV_4 = (TextView) barChartLayout.findViewById(R.id.dateTV_4);
        dateTV_5 = (TextView) barChartLayout.findViewById(R.id.dateTV_5);

        final TextView chartTitleTV_NEW_CHART = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        final BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);
        Button showChartValuesBTN = (Button) barChartLayout.findViewById(R.id.showChartValuesBTN);

        showChartValuesBTN.setBackgroundResource(R.drawable.ic_format_list_numbered_white_24dp);

        if (chartTitle != null)
            chartTitleTV_NEW_CHART.setText(chartTitle);

        /**
         * Depending on the chart type passed here,
         * grab the appropriate element from the array within the API response that was passed to this fragment from the host activity
         */
        switch (chartType) {
            case "steps-count":
                getDefaultChartMonthlyBreakdown(barChart, dateTV_1, dateTV_2, dateTV_3, dateTV_4, dateTV_5,
                        "steps-count");
                break;
            case "active-calories":
                getDefaultChartMonthlyBreakdown(barChart, dateTV_1, dateTV_2, dateTV_3, dateTV_4, dateTV_5,
                        "active-calories");
                break;
            case "distance-traveled":
                getDefaultChartMonthlyBreakdown(barChart, dateTV_1, dateTV_2, dateTV_3, dateTV_4, dateTV_5,
                        "distance-traveled");
                break;
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.chart_height_2));
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);
        barChartLayout.setLayoutParams(lp);

        cards_container.addView(barChartLayout);

        /**
         * Open the breakdown for the chart
         */
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSlugBreakdownForChart(chartTitle, chartType);
            }
        });

        showChartValuesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barChart.getBarData() != null) {
                    if (setDrawChartValuesEnabled) {
                        barChart.getBarData().setDrawValues(true);
                    } else {
                        barChart.getBarData().setDrawValues(false);
                    }

                    setDrawChartValuesEnabled = !setDrawChartValuesEnabled;

                    barChart.invalidate();
                }
            }
        });
    }

    private void getSlugBreakdownForChart(final String chartTitle, final String chartType) {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getActivity().getResources().getString(R.string.grabbing_breakdown_data_dialog_title));
        waitingDialog.setMessage(getActivity().getResources().getString(R.string.please_wait_dialog_message));
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

        DataAccessHandler.getInstance().getSlugBreakdownForChart(chartType, prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<SlugBreakdownResponse>() {
            @Override
            public void onResponse(Call<SlugBreakdownResponse> call, Response<SlugBreakdownResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        Intent intent = new Intent(getActivity(), SlugBreakdown_Activity.class);
                        intent.putExtra(Constants.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Constants.EXTRAS_FITNESS_FRAGMENT);
                        intent.putExtra(Constants.EXTRAS_CHART_FULL_NAME, chartTitle);
                        intent.putExtra(Constants.EXTRAS_CHART_TYPE_SELECTED, chartType);
                        intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA, response.body().getData().getBody().getData());
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

    /**
     * Grab all charts from DB, remove all containing views, add the default bar chart, add the remaining barcharts
     * (the ones from DB)
     */
    private void setupChartViews(List<DataChart> dataChartsMap) {
        cards_container.removeAllViews();

        addNewBarChart("Number Of Steps", "steps-count");

        if (!dataChartsMap.isEmpty()) {
            for (DataChart chart :
                    dataChartsMap) {

                addNewBarChart(chart.getName(), chart.getType());
            }
        }
    }

    private void setupWidgetViews(ArrayList<FitnessWidget> widgetsMap) {
        widgets_GridAdapter = new FitnessWidgets_GridAdapter(getActivity(), widgetsMap, R.layout.grid_item_fitness_widgets);

        widgetsGridView.setAdapter(widgets_GridAdapter);
    }

    private TextView findWidgetInGrid(String widgetName) {
        View fitnessWidgetView;
        TextView metricCountTextView = null;

        for (int i = 0; i < widgets_GridAdapter.getCount(); i++) {
            if (widgets_GridAdapter.getItem(i).getTitle().equals(widgetName)) {
                final int firstListItemPosition = widgetsGridView.getFirstVisiblePosition();
                final int lastListItemPosition = firstListItemPosition + widgetsGridView.getChildCount() - 1;

                if (i < firstListItemPosition || i > lastListItemPosition) {
                    fitnessWidgetView = widgetsGridView.getAdapter().getView(i, null, widgetsGridView);
                } else {
                    final int childIndex = i - firstListItemPosition;
                    fitnessWidgetView = widgetsGridView.getChildAt(childIndex);
                }

                metricCountTextView = (TextView) fitnessWidgetView.findViewById(R.id.metricTV);
            }
        }

        return metricCountTextView;
    }

    /**
     * Events are received from the Event Bus and handled here.
     * Notable events:
     * - Steps counter
     * - Distance counter (widget needs finding in the gridview before its value can be changed)
     * - Calories counter (widget needs finding in the gridview before its value can be changed)
     *
     * @param ebp
     */
    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();
        TextView fitnessWidget;

        switch (ebpMessage) {
            case Constants.EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getFitnessWidgetsMap() != null) {
                    widgetsMap = ebp.getFitnessWidgetsMap();
                    setupWidgetViews(widgetsMap);
                }
                break;
//            case Constants.EXTRAS_FITNESS_CHART_DELETED:
            case Constants.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED:
                List<DataChart> allDataCharts = ebp.getDataChartsListExtra();

                cards_container.removeAllViews();

                chartsMap = (ArrayList<DataChart>) allDataCharts;

                setupChartViews(chartsMap);

                int[] charts = new int[allDataCharts.size()];
                int[] chartPositions = new int[allDataCharts.size()];

                for (int i = 0; i < allDataCharts.size(); i++) {
                    charts[i] = allDataCharts.get(i).getChart_id();
                    chartPositions[i] = allDataCharts.get(i).getPosition();
                }

                updateUserCharts(charts, chartPositions);

                break;
            case Constants.EVENT_STEP_COUNTER_INCREMENTED:
                metricCounterTV.setText(String.valueOf(prefs.getInt(Helpers.getTodayDate() + "_steps", 0)));
                break;
            case Constants.EVENT_CALORIES_COUNTER_INCREMENTED:
                fitnessWidget = findWidgetInGrid("Calories");
                fitnessWidget.setText(String.valueOf((int) prefs.getFloat(Helpers.getTodayDate() + "_calories", 0)));
                break;
            case Constants.EVENT_DISTANCE_COUNTER_INCREMENTED:
                fitnessWidget = findWidgetInGrid("Distance");
                fitnessWidget.setText(String.valueOf((int) (prefs.getFloat(Helpers.getTodayDate() + "_distance", 0) * 1000)));
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateUserCharts(int[] chartIds, int[] chartPositions) {
        DataAccessHandler.getInstance().updateUserCharts(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
                Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), chartIds, chartPositions, new Callback<DefaultGetResponse>() {
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
}