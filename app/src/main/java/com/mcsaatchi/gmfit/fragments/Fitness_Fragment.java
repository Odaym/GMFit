package com.mcsaatchi.gmfit.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.adapters.Widgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.ParcelableFitnessString;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fitness_Fragment extends Fragment implements SensorEventListener {

    public static final String TAG = "Fitness_Fragment";

    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;

    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.bar_chart)
    BarChart defaultBarChart;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.metricCounterTV)
    FontTextView metricCounterTV;

    private NestedScrollView parentScrollView;
    private Activity parentActivity;
    private SharedPreferences prefs;
    private Widgets_GridAdapter widgets_GridAdapter;
    private String chartName;
    private String chartType;

    private ParcelableSparseArray widgetsMap2;

    private RuntimeExceptionDao<DataChart, Integer> dataChartDAO;
    private List<DataChart> allDataCharts;

    private ArrayList<AuthenticationResponseWidget> widgetsMap;
    private ArrayList<AuthenticationResponseChart> chartsMap;

    private String todayDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
            dataChartDAO = ((Base_Activity) parentActivity).getDBHelper().getDataChartDAO();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus_Singleton.getInstance().register(this);

        /**
         * Initialize JodaTime
         */
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
                            getActivity().finish();
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

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE
                , Context.MODE_PRIVATE);

        if (getArguments() != null) {
            widgetsMap = getArguments().getParcelableArrayList("widgets");
            chartsMap = getArguments().getParcelableArrayList("charts");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        LocalDate dt = new LocalDate();
        todayDate = dt.toString();

        Log.d(TAG, "onCreateView: Device info : " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") - "
                + Build.VERSION.RELEASE);

        Helpers.setBarChartData(defaultBarChart, chartsMap.get(0).getData());

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_FITNESS_CHART);
                startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        metricCounterTV.setText(String.valueOf(prefs.getInt(todayDate + "_steps", 0)));

        widgetsMap2 = new ParcelableSparseArray() {{
            put(0, new ParcelableFitnessString(R.drawable.ic_running, 0, "Walking", "Km/hour"));
            put(1, new ParcelableFitnessString(R.drawable.ic_biking, (int) (prefs.getFloat(todayDate + "_distance", 0) * 1000), "Biking",
                    "meters"));
            put(3, new ParcelableFitnessString(R.drawable.ic_steps, 0, "Stairs", "steps"));
            put(2, new ParcelableFitnessString(R.drawable.ic_calories, (int) prefs.getFloat(todayDate + "_calories", 0), "Calories",
                    "Calories"));
        }};

        setUpWidgetsGridView(widgetsMap2);

        allDataCharts = dataChartDAO.queryForAll();

        if (!allDataCharts.isEmpty()) {
            for (DataChart chart :
                    allDataCharts) {
                addNewBarChart(chart.getName(), chart.getType(), true);
            }
        }

        return fragmentView;
    }

    public void addNewBarChart(String chartTitle, String chartType, boolean appJustLaunched) {
        final View barChartLayout_NEW_CHART = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        final CardView cardLayout_NEW_CHART = (CardView) barChartLayout_NEW_CHART.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV_NEW_CHART = (TextView) barChartLayout_NEW_CHART.findViewById(R.id.chartTitleTV);
        BarChart barChart_NEW_CHART = (BarChart) barChartLayout_NEW_CHART.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV_NEW_CHART.setText(chartTitle);

        switch (chartType) {
            case "steps-count":
                Helpers.setBarChartData(barChart_NEW_CHART, chartsMap.get(0).getData());
                break;
            case "active-calories":
                Helpers.setBarChartData(barChart_NEW_CHART, chartsMap.get(3).getData());
                break;
            case "distance-traveled":
                Helpers.setBarChartData(barChart_NEW_CHART, chartsMap.get(4).getData());
                break;
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.chart_height_2));
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);
        barChartLayout_NEW_CHART.setLayoutParams(lp);

        cards_container.addView(barChartLayout_NEW_CHART);

        if (!appJustLaunched)
            parentScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    parentScrollView.fullScroll(View.FOCUS_DOWN);
                }
            }, 500);
    }

    private void setUpWidgetsGridView(ParcelableSparseArray widgetsMap2) {

        widgets_GridAdapter = new Widgets_GridAdapter(getActivity(), widgetsMap2, R.layout.grid_item_fitness_widgets);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_FITNESS_CHART_REQUEST_CODE) {
            if (data != null) {

                chartType = data.getStringExtra(Cons.EXTRAS_CHART_TYPE_SELECTED);
                chartName = data.getStringExtra(Cons.EXTRAS_CHART_FULL_NAME);

                addNewBarChart(chartName, chartType, false);

                dataChartDAO.create(new DataChart(chartName, chartType, dataChartDAO.queryForAll().size() + 1, Cons.EXTRAS_FITNESS_FRAGMENT));
            }
        }
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();
        TextView fitnessWidget;

        LocalDate dt = new LocalDate();

        todayDate = dt.toString();

        switch (ebpMessage) {
            case Cons.EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getParcelableSparseExtra() != null) {
                    widgetsMap2 = ebp.getParcelableSparseExtra();
                    setUpWidgetsGridView(ebp.getParcelableSparseExtra());
                }

                break;
            case Cons.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED:
                List<DataChart> allDataCharts = ebp.getDataChartsListExtra();

                cards_container.removeAllViews();

                addNewBarChart("Number Of Steps", "steps-count", false);

                for (DataChart chart :
                        allDataCharts) {
                    addNewBarChart(chart.getName(), chart.getType(), false);
                }

                break;
            case Cons.EVENT_CHART_METRICS_RECEIVED:
//                addNewBarChart(chartName, ebp.getFloatArrayExtra());
                break;
            case Cons.EVENT_STEP_COUNTER_INCREMENTED:
                metricCounterTV.setText(String.valueOf(prefs.getInt(todayDate + "_steps", 0)));
                break;
            case Cons.EVENT_CALORIES_COUNTER_INCREMENTED:
                fitnessWidget = findWidgetInGrid("Calories");
                fitnessWidget.setText(String.valueOf((int) prefs.getFloat(todayDate + "_calories", 0)));
                break;
            case Cons.EVENT_DISTANCE_COUNTER_INCREMENTED:
                fitnessWidget = findWidgetInGrid("Biking");
                fitnessWidget.setText(String.valueOf((int) (prefs.getFloat(todayDate + "_distance", 0) * 1000)));
                break;
            case Cons.EVENT_CHART_ADDED_FROM_SETTINGS:
                String[] chartDetails = ebp.getStringsExtra();
                addNewBarChart(chartDetails[0], chartDetails[1], false);

                dataChartDAO.create(new DataChart(chartDetails[0], chartDetails[1], dataChartDAO.queryForAll().size() + 1, Cons.EXTRAS_FITNESS_FRAGMENT));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
                intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE, Cons.EXTRAS_FITNESS_FRAGMENT);
                intent.putExtra(Cons.BUNDLE_FITNESS_WIDGETS_MAP, widgetsMap2);
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}