package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.adapters.Widgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.ParcelableFitnessString;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.pedometer.PedometerSettings;
import com.mcsaatchi.gmfit.pedometer.StepService;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fitness_Fragment extends Fragment {

    public static final String TAG = "Fitness_Fragment";

    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;

    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;

    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.bar_chart)
    BarChart defaultBarChart;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.metricCounterTV)
    TextView metricCounterTV;
    double paceValue, speedValue;
    private NestedScrollView parentScrollView;
    private Activity parentActivity;
    private SharedPreferences prefs;
    private Widgets_GridAdapter widgets_GridAdapter;
    private ParcelableSparseArray widgetsMap;
    private StepService stepService;
    private boolean mIsRunning;
    private String chartName;
    private String chartType;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView widgetTextView;

            switch (msg.what) {
                case STEPS_MSG:
//                    Log.d(TAG, "handleMessage: Steps count changed");

                    int stepsFromPrefs = prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT, 0);

                    metricCounterTV.setText(String.valueOf(stepsFromPrefs));

                    break;
                case PACE_MSG:
//                    Log.d(TAG, "handleMessage: Pace changed");

                    paceValue = msg.arg1;
                    widgetTextView = findWidgetInGrid("Stairs");
                    widgetTextView.setText(String.valueOf(paceValue));
                    break;
                case DISTANCE_MSG:
//                    Log.d(TAG, "handleMessage: Distance changed");

                    widgetTextView = findWidgetInGrid("Biking");

                    int distanceFromprefs = prefs.getInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0);

                    widgetTextView.setText(String.valueOf(distanceFromprefs));
                    break;
                case SPEED_MSG:
//                    Log.d(TAG, "handleMessage: Speed changed");

                    speedValue = msg.arg1 / 1000f;
                    widgetTextView = findWidgetInGrid("Walking");
                    widgetTextView.setText(String.valueOf((int) speedValue));
                    break;
                case CALORIES_MSG:
//                    Log.d(TAG, "handleMessage: Calories changed");

                    //Current value for Calories in prefs

                    widgetTextView = findWidgetInGrid("Calories");

                    widgetTextView.setText(String.valueOf(prefs.getInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, 0)));

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }

        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }

        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int) (value * 1000), 0));
        }

        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int) (value * 1000), 0));
        }

        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int) (value), 0));
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            stepService = ((StepService.StepBinder) service).getService();
            stepService.registerCallback(mCallback);
        }

        public void onServiceDisconnected(ComponentName className) {
            stepService = null;
        }
    };

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

        JodaTimeAndroid.init(getActivity());

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fitness_tab_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

//        if (getArguments() != null) {
//            widgetsFromServer = getArguments().getParcelableArrayList("widgets");
//
//            Log.d(TAG, "Got widgets from Main Activity ");
//
//            if (widgetsFromServer != null) {
//                Log.d(TAG, "onCreateView: Widgets from Server (from Main Activity, from SignIn Activity have size : " + widgetsFromServer.size());
//
//                for (int i = 0; i < widgetsFromServer.size(); i++) {
//                    Log.d(TAG, "onCreateView: Widget " + widgetsFromServer.get(i).getWidgetId() + " position " +
//                            widgetsFromServer.get(i).getPosition() + " Name " + widgetsFromServer.get(i).getName());
//                }
//            }
//        } else {
//            Log.d(TAG, "onCreateView: Bundle is null");
//        }

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

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE
                , Context.MODE_PRIVATE);

        metricCounterTV.setText(String.valueOf(prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT, 0)));

        widgetsMap = new ParcelableSparseArray() {{
            put(0, new ParcelableFitnessString(R.drawable.ic_running, 0.0, "Walking", "Km/hour"));
            put(1, new ParcelableFitnessString(R.drawable.ic_biking, prefs.getInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0), "Biking",
                    "meters"));
            put(3, new ParcelableFitnessString(R.drawable.ic_steps, 0.0, "Stairs", "steps/minute"));
            put(2, new ParcelableFitnessString(R.drawable.ic_calories, prefs.getInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, 0), "Calories",
                    "Calories"));
        }};

        setUpWidgetsGridView(widgetsMap);

        SharedPreferences pedometerPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PedometerSettings mPedometerSettings = new PedometerSettings(pedometerPreferences);

        mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        } else if (mIsRunning) {
            bindStepService();
        }

        return fragmentView;
    }

    private void startStepService() {
        if (!mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            parentActivity.startService(new Intent(getActivity(),
                    StepService.class));
        }
    }

    private void bindStepService() {
        if (!mIsRunning) {
            boolean isServiceBound = parentActivity.bindService(new Intent(getActivity(),
                    StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);

            Log.i(TAG, "[SERVICE] Bind " + isServiceBound);
        }
    }

    public void addNewBarChart(String chartTitle, ArrayList<Float> floatArrayExtra) {
        final View barChartLayout_NEW_CHART = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        final CardView cardLayout_NEW_CHART = (CardView) barChartLayout_NEW_CHART.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV_NEW_CHART = (TextView) barChartLayout_NEW_CHART.findViewById(R.id.chartTitleTV);
        BarChart barChart_NEW_CHART = (BarChart) barChartLayout_NEW_CHART.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV_NEW_CHART.setText(chartTitle);

        Helpers.setBarChartData(barChart_NEW_CHART, floatArrayExtra);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.chart_height_2));
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);
        barChartLayout_NEW_CHART.setLayoutParams(lp);

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

    private TextView findWidgetInGrid(String widgetName) {
        View fitnessWidgetView;
        TextView stepCountTextView = null;

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

                addNewBarChart(chartName, new ArrayList<Float>() {{
                    add(0, 200f);
                    add(1, 201f);
                    add(2, 120f);
                    add(3, 100f);
                    add(4, 50f);
                    add(5, 200f);
                    add(6, 61f);
                    add(7, 66f);
                    add(8, 140f);
                    add(9, 195f);
                    add(10, 60f);
                    add(11, 209f);
                    add(11, 109f);
                }});
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
                List<DataChart> allDataCharts = ebp.getDataChartsListExtra();

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
}