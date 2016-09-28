package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.TwoItem_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.models.DataChart;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewChart_Activity extends Base_Activity {
    private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
    private static final int ADD_NEW_NUTRITION_CHART_REQUEST_CODE = 2;

    @Bind(R.id.chartsList)
    ListView chartsList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.topLayout)
    LinearLayout topLayout;

    private RuntimeExceptionDao<DataChart, Integer> dataChartDAO;
    private QueryBuilder<DataChart, Integer> dataChartQB;

    private SparseArray<String[]> fitnessItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Number of Steps", "Steps", "steps-count"});
        put(1, new String[]{"Distance Traveled", "KM", "distance-traveled"});
        put(2, new String[]{"Active Calories", "kcal", "active-calories"});
    }};

    private SparseArray<String[]> nutritionItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Calories", "kcal"});
        put(1, new String[]{"Protein", "mcg"});
        put(2, new String[]{"Sugar", "mg"});
        put(3, new String[]{"Fiber", "mg"});
        put(4, new String[]{"Carbs", "g"});
        put(5, new String[]{"Fats", "mg"});
        put(6, new String[]{"Sodium", "mcg"});
        put(7, new String[]{"Copper", "mg"});
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
                        topLayout.setBackground(getResources().getDrawable(R.drawable.fitness_background));

                        chartsList.setAdapter(new TwoItem_Sparse_ListAdapter(this, fitnessItemsMap));
                        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                String chartName = fitnessItemsMap.get(position)[0];

                                if (!chartName.equals("Number of Steps") && !checkIfChartExistsInDB(chartName)) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, fitnessItemsMap.get(position)[0]);
                                    intent.putExtra(Cons.EXTRAS_CHART_TYPE_SELECTED, fitnessItemsMap.get(position)[2]);
                                    setResult(ADD_NEW_FITNESS_CHART_REQUEST_CODE, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddNewChart_Activity.this, R.string.duplicate_chart_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case Cons.EXTRAS_ADD_NUTRIITION_CHART:
                        topLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));

                        chartsList.setAdapter(new TwoItem_Sparse_ListAdapter(this, nutritionItemsMap));
                        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                String chartName = nutritionItemsMap.get(position)[0];

                                if (!checkIfChartExistsInDB(chartName)) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, nutritionItemsMap.get(position)[0]);
                                    setResult(ADD_NEW_NUTRITION_CHART_REQUEST_CODE, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddNewChart_Activity.this, R.string.duplicate_chart_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                }
            }
        }
    }

    private boolean checkIfChartExistsInDB(String chart_title) {
        dataChartDAO = getDBHelper().getDataChartDAO();
        dataChartQB = dataChartDAO.queryBuilder();

        try {
            List<DataChart> dc = dataChartQB.where().eq("name", chart_title).query();

            return !dc.isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
