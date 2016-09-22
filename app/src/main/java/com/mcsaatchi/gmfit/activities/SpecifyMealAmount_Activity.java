package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.TwoItem_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponseDatum;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecifyMealAmount_Activity extends Base_Activity {
    private static final int MEAL_AMOUNT_SPECIFIED = 536;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nutritionFactsList)
    ListView nutritionFactsList;
    @Bind(R.id.addToDiaryBTN)
    Button addToDiaryBTN;
    @Bind(R.id.mealAmountET)
    FormEditText mealAmountET;

    private ArrayList<FormEditText> allFields = new ArrayList<>();
    private SharedPreferences prefs;
    private TwoItem_Sparse_ListAdapter nutritionFactsListAdapter;
    private SparseArray<String[]> nutritionalFacts = new SparseArray<>();

    private MealItem mealItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_meal_amount);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        allFields.add(mealAmountET);

        if (getIntent().getExtras() != null) {
            mealItem = getIntent().getExtras().getParcelable(Cons.EXTRAS_MEAL_OBJECT_DETAILS);

            if (mealItem != null) {
                setupToolbar(toolbar, mealItem.getName(), true);

                getMealMetrics(mealItem.getMeal_id());
            }
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

        DataAccessHandler.getInstance().getMealMetrics(prefs.getString(Cons
                .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "http://gmfit.mcsaatchi.me/api/v1/meals/" + meal_id, new Callback<MealMetricsResponse>() {
            @Override
            public void onResponse(Call<MealMetricsResponse> call, Response<MealMetricsResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();


                        List<MealMetricsResponseDatum> mealMetricsResponseDatumList = response.body().getData().getBody().getMetrics();

                        for (int i = 0; i < mealMetricsResponseDatumList.size(); i++) {
                            nutritionalFacts.put(i, new String[]{mealMetricsResponseDatumList.get(i).getName(), (int) Double.parseDouble(mealMetricsResponseDatumList.get(i).getValue()) + " " + mealMetricsResponseDatumList.get(i).getUnit()});
                        }

                        if (nutritionalFacts.size() > 0) {
                            int caloriesForThisMeal = 0;

                            for (int i = 0; i < nutritionalFacts.size(); i++) {
                                if (nutritionalFacts.get(i)[0].equals("Calories")) {
                                    caloriesForThisMeal = Integer.parseInt(nutritionalFacts.get(i)[1].split(" ")[0]);
                                }
                            }

                            initNutritionFactsList(nutritionalFacts, caloriesForThisMeal);
                        }

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

    private void initNutritionFactsList(SparseArray<String[]> nutritionalFacts, final int caloriesForThisMeal) {

        nutritionFactsListAdapter = new TwoItem_Sparse_ListAdapter(this, nutritionalFacts);

        nutritionFactsList.setAdapter(nutritionFactsListAdapter);

        addToDiaryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.validateFields(allFields)) {

                    final ProgressDialog waitingDialog = new ProgressDialog(SpecifyMealAmount_Activity.this);
                    waitingDialog.setTitle(getString(R.string.adding_new_meal_dialog_title));
                    waitingDialog.setMessage(getString(R.string.adding_new_meal_dialog_message));
                    waitingDialog.show();

                    final AlertDialog alertDialog = new AlertDialog.Builder(SpecifyMealAmount_Activity.this).create();
                    alertDialog.setTitle(R.string.adding_new_meal_dialog_title);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    if (waitingDialog.isShowing())
                                        waitingDialog.dismiss();
                                }
                            });

                    DataAccessHandler.getInstance().storeNewMeal(prefs.getString(Cons
                                    .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealItem.getMeal_id(), Integer.parseInt(mealAmountET.getText().toString()),
                            mealItem.getType(), new Callback<DefaultGetResponse>() {
                                @Override
                                public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {

                                    Log.d("TAG", "onResponse: Response is : " + response.code());

                                    switch (response.code()) {
                                        case 200:
                                            waitingDialog.dismiss();

                                            mealItem.setAmount(mealAmountET.getText().toString());
                                            mealItem.setTotalCalories(Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra(Cons.EXTRAS_MEAL_OBJECT_DETAILS, mealItem);
                                            setResult(MEAL_AMOUNT_SPECIFIED, resultIntent);
                                            finish();

                                            break;
                                    }
                                }

                                @Override
                                public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

                                }
                            });
                }
            }
        });
    }
}
