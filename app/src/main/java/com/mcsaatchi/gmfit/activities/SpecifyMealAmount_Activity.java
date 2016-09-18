package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ListView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.TwoItem_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponseDatum;
import com.mcsaatchi.gmfit.rest.RestClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecifyMealAmount_Activity extends Base_Activity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nutritionFactsList)
    ListView nutritionFactsList;

    private SharedPreferences prefs;
    private TwoItem_Sparse_ListAdapter nutritionFactsListAdapter;

    private SparseArray<String[]> nutritionalFacts = new SparseArray<>();

    private int meal_id;
    private String meal_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_meal_amount);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            meal_id = getIntent().getExtras().getInt(Cons.EXTRAS_MEAL_ID_FOR_METRIC_DATA, 0);
            meal_name = getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);

            if (meal_name != null) {
                setupToolbar(toolbar, meal_name, true);
            }

            getMealMetrics(meal_id);
        }
    }

    private void getMealMetrics(int meal_id) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.fetching_meal_info_dialog_title));
        waitingDialog.setMessage(getString(R.string.fetching_meal_info_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.fetching_meal_info_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<MealMetricsResponse> getMealMetricsCall = new RestClient().getGMFitService().getMealMetrics(prefs.getString(Cons
                .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), String.valueOf(meal_id));

        getMealMetricsCall.enqueue(new Callback<MealMetricsResponse>() {
            @Override
            public void onResponse(Call<MealMetricsResponse> call, Response<MealMetricsResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        Log.d("TAGTRAG", "onResponse: Success!");

                        List<MealMetricsResponseDatum> mealMetricsResponseDatumList = response.body().getData().getBody().getMetrics();

                        for (int i = 0; i < mealMetricsResponseDatumList.size(); i++) {
                            nutritionalFacts.put(i, new String[]{mealMetricsResponseDatumList.get(i).getName(), mealMetricsResponseDatumList.get(i).getName() + "" + mealMetricsResponseDatumList.get(i).getName()});
                        }

                        Log.d("TAAG", "onResponse: nutrition size " + nutritionalFacts.size());
                        Log.d("TAAG", "onResponse: response size " + mealMetricsResponseDatumList.size());

//                        initNutritionFactsList(nutritionalFacts);

                        break;
                    case 449:
                        alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
                        alertDialog.show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    private void initNutritionFactsList(SparseArray<String[]> nutritionalFacts) {
        nutritionFactsListAdapter = new TwoItem_Sparse_ListAdapter(this, nutritionalFacts);

        nutritionFactsList.setAdapter(nutritionFactsListAdapter);
    }
}
