package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.AddNewMealItem_Activity;
import com.mcsaatchi.gmfit.activities.BarcodeCapture_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidgetsAndCharts_Activity;
import com.mcsaatchi.gmfit.adapters.Widgets_GridAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.ParcelableNutritionString;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    Button addNewEntryBTN_BREAKFAST;
    @Bind(R.id.scanEntryBTN_BREAKFAST)
    Button scanEntryBTN_BREAKFAST;
    @Bind(R.id.entriesContainerLayout_BREAKFAST)
    LinearLayout entriesContainerLayout_BREAKFAST;
    /**
     * LUNCH CHART
     */
    @Bind(R.id.chartTitleTV_LUNCH)
    TextView chartTitleTV_LUNCH;
    @Bind(R.id.addEntryBTN_LUNCH)
    Button addNewEntryBTN_LUNCH;
    @Bind(R.id.scanEntryBTN_LUNCH)
    Button scanEntryBTN_LUNCH;
    @Bind(R.id.entriesContainerLayout_LUNCH)
    LinearLayout entriesContainerLayout_LUNCH;
    /**
     * DINNER CHART
     */
    @Bind(R.id.chartTitleTV_DINNER)
    TextView chartTitleTV_DINNER;
    @Bind(R.id.addEntryBTN_DINNER)
    Button addNewEntryBTN_DINNER;
    @Bind(R.id.scanEntryBTN_DINNER)
    Button scanEntryBTN_DINNER;
    @Bind(R.id.entriesContainerLayout_DINNER)
    LinearLayout entriesContainerLayout_DINNER;
    /**
     * SNACKS CHART
     */
    @Bind(R.id.chartTitleTV_SNACKS)
    TextView chartTitleTV_SNACKS;
    @Bind(R.id.addEntryBTN_SNACKS)
    Button addNewEntryBTN_SNACKS;
    @Bind(R.id.scanEntryBTN_SNACKS)
    Button scanEntryBTN_SNACKS;
    @Bind(R.id.entriesContainerLayout_SNACKS)
    LinearLayout entriesContainerLayout_SNACKS;
    /**
     * ADD CHART BUTTON
     */
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    private Widgets_GridAdapter widgets_GridAdapter;
    private ParcelableSparseArray widgetsMap = new ParcelableSparseArray() {{
        put(0, new ParcelableNutritionString("PROTEIN", 152, "g", 17));
        put(1, new ParcelableNutritionString("FAT", 87, "g", 29));
        put(2, new ParcelableNutritionString("SODIUM", 200, "mg", 105));
        put(3, new ParcelableNutritionString("SUGAR", 42, "g", 112));
    }};
    /**
     * TOP LAYOUT WITH WIDGETS
     */
    private NestedScrollView parentScrollView;
    private Activity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

        EventBus_Singleton.getInstance().register(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nutrition_tab_title);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        setUpWidgetsGridView(widgetsMap);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Cons.EXTRAS_ADD_CHART_WHAT_TYPE, Cons.EXTRAS_ADD_NUTRIITION_CHART);
                startActivityForResult(intent,
                        ADD_NEW_NUTRITION_CHART_REQUEST);
            }
        });

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
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (Activity) context;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndCharts_Activity.class);
        intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE, "NUTRITION");
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    private void setUpWidgetsGridView(ParcelableSparseArray widgetsMap) {
        widgets_GridAdapter = new Widgets_GridAdapter(getActivity(), widgetsMap, R.layout.grid_item_nutrition_widgets);

        widgetsGridView.setAdapter(widgets_GridAdapter);
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
//                itemsMap = ebp.getSparseArrayExtra();


                break;
        }
    }

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
                    addNewBarChart(data.getStringExtra(Cons.EXTRAS_CHART_TYPE_SELECTED));
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
        Log.d(TAG, "openMealEntryPickerActivity: Meal Name when Adding " + mainMealName);
        startActivity(intent);
    }

    private void addMealEntryLayout(String mealTitle, LinearLayout targetLayoutForMeal) {
        final View mealEntryLayout = parentActivity.getLayoutInflater().inflate(R.layout.list_item_add_new_meal, null);

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

        Button removeChartBTN = (Button) barChartLayout.findViewById(R.id.removeChartBTN);
        final CardView cardLayout = (CardView) barChartLayout.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        //TODO: apply the same change you did here as in the Fitness Fragment

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

}