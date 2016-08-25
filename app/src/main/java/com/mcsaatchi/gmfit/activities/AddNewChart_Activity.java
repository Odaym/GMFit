package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.TwoItem_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.Nutrition_Fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewChart_Activity extends Base_Activity {
    @Bind(R.id.chartsList)
    ListView chartsList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private SparseArray<String[]> fitnessItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Number of Steps", "Steps", "steps-count"});
        put(1, new String[]{"Walking and Running Distance", "KM", "distance-traveled"});
        put(2, new String[]{"Cycling Distance", "KM", "cycling-distance"});
        put(3, new String[]{"Total Distance Traveled", "KM", "distance-traveled"});
        put(4, new String[]{"Flights Climbed", "Steps", "steps-count"});
        put(5, new String[]{"Active Calories", "kcal", "active-calories"});
    }};

    private SparseArray<String[]> nutritionItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Calories", "kcal"});
        put(1, new String[]{"Biotin", "mcg"});
        put(2, new String[]{"Caffeine", "mg"});
        put(3, new String[]{"Calcium", "mg"});
        put(4, new String[]{"Carbohydrates", "g"});
        put(5, new String[]{"Chloride", "mg"});
        put(6, new String[]{"Chromium", "mcg"});
        put(7, new String[]{"Copper", "mg"});
        put(8, new String[]{"Dietary Cholesterol", "mg"});
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_chart);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.add_new_chart_activity_title, true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String CALL_PURPOSE = extras.getString(Cons.EXTRAS_ADD_CHART_WHAT_TYPE);

            if (CALL_PURPOSE != null) {
                switch (CALL_PURPOSE) {
                    case Cons.EXTRAS_ADD_FITNESS_CHART:
                        chartsList.setAdapter(new TwoItem_Sparse_ListAdapter(this, fitnessItemsMap));
                        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent();
                                intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, fitnessItemsMap.get(position)[0]);
                                intent.putExtra(Cons.EXTRAS_CHART_TYPE_SELECTED, fitnessItemsMap.get(position)[2]);
                                setResult(Fitness_Fragment.ADD_NEW_FITNESS_CHART_REQUEST_CODE, intent);
                                finish();
                            }
                        });
                        break;
                    case Cons.EXTRAS_ADD_NUTRIITION_CHART:
                        chartsList.setAdapter(new TwoItem_Sparse_ListAdapter(this, nutritionItemsMap));
                        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent();
                                intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, nutritionItemsMap.get(position)[0]);
                                setResult(Nutrition_Fragment.ADD_NEW_NUTRITION_CHART_REQUEST, intent);
                                finish();
                            }
                        });
                        break;
                }
            }
        }
    }
}
