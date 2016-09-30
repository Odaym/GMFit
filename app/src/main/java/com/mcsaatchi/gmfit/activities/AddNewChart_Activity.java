package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.rest.ChartsBySectionResponseDatum;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private SharedPreferences prefs;

    private SparseArray<String[]> fitnessItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Number of Steps", "Steps", "steps-count"});
        put(1, new String[]{"Distance Traveled", "KM", "distance-traveled"});
        put(2, new String[]{"Active Calories", "kcal", "active-calories"});
    }};

    private SparseArray<String[]> nutritionItemsMap = new SparseArray<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_chart);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.add_new_chart_activity_title, true);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

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
                        getChartsBySection("Nutritition");
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

    private void getChartsBySection(String sectionName){
        DataAccessHandler.getInstance().getChartsBySection(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), sectionName, new Callback<ChartsBySectionResponse>() {
            @Override
            public void onResponse(Call<ChartsBySectionResponse> call, Response<ChartsBySectionResponse> response) {
                switch (response.code()) {
                    case 200:
                        Log.d("TAG", "onResponse: Successfully add a new chart!");

                        topLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));

                        List<ChartsBySectionResponseDatum> chartsFromResponse = response.body().getData().getBody().getData();

                        for (int i = 0; i < chartsFromResponse.size(); i++){
                            nutritionItemsMap.put(i, new String[]{chartsFromResponse.get(i).getName(), chartsFromResponse.get(i).getUnit(), String.valueOf(chartsFromResponse.get(i).getId())});
                        }

                        chartsList.setAdapter(new TwoItem_Sparse_ListAdapter(AddNewChart_Activity.this, nutritionItemsMap));
                        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                String chartName = nutritionItemsMap.get(position)[0];

                                if (!checkIfChartExistsInDB(chartName)) {
                                    /**
                                     * Send ID along with this request
                                     */
                                    addMetricChart(Integer.parseInt(nutritionItemsMap.get(position)[2]));

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

            @Override
            public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
            }
        });
    }

    private void addMetricChart(int chart_id){
        DataAccessHandler.getInstance().addMetricChart(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), chart_id, new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        Log.d("TAG", "onResponse: Successfully add a new chart!");
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            }
        });
    }
}
