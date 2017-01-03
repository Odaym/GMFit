package com.mcsaatchi.gmfit.health.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.MostPopularMedicationsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MostPopularMedicationsResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SearchMedicinesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMedicinesResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.SearchMedicationsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SearchMedicationsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.searchMedicationsAutoCompleteTV) EditText searchMedicationsAutoCompleteTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;
  @Bind(R.id.pb_loading_indicator) ProgressBar pb_loading_indicator;
  @Bind(R.id.mealsAvailableRecyclerView) RecyclerView mealsAvailableRecyclerView;
  @Bind(R.id.searchResultsHintTV) TextView searchResultsHintTV;
  @Bind(R.id.searchResultsListLayout) LinearLayout searchResultsListLayout;
  @Bind(R.id.noSearchResultsFoundLayout) LinearLayout noSearchResultsFoundLayout;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_search_medications);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.add_medication_activity_title), true);

    getMostPopularMedications();

    searchMedicationsAutoCompleteTV.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
          searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
          searchIconIV.setOnClickListener(null);

          searchResultsHintTV.setText(getString(R.string.most_popular_hint));
          pb_loading_indicator.setVisibility(View.GONE);

          getMostPopularMedications();
        } else if (charSequence.toString().length() > 2) {
          searchResultsHintTV.setText(getString(R.string.search_results_list_label));
          pb_loading_indicator.setVisibility(View.VISIBLE);

          new Handler().postDelayed(new Runnable() {
            @Override public void run() {
              searchIconIV.setImageResource(R.drawable.ic_clear_search);
              searchIconIV.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                  searchMedicationsAutoCompleteTV.setText("");

                  /**
                   * Hide keyboard
                   */
                  InputMethodManager imm =
                      (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
              });

              searchMedicines(charSequence.toString());
            }
          }, 500);
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  private void searchMedicines(String key) {
    dataAccessHandler.searchMedicines("2012250", "1892870", "422", "2", "2013", key,
        new Callback<SearchMedicinesResponse>() {
          @Override public void onResponse(Call<SearchMedicinesResponse> call,
              Response<SearchMedicinesResponse> response) {
            switch (response.code()) {
              case 200:
                final List<Medication> medicationsReturned = new ArrayList<>();

                List<SearchMedicinesResponseDatum> medicationsFromResponse =
                    response.body().getData().getBody().getData().getItemLst();

                if (medicationsFromResponse != null) {
                  for (int i = 0; i < medicationsFromResponse.size(); i++) {

                    SearchMedicinesResponseDatum medicationDatum = medicationsFromResponse.get(i);

                    Medication medication = new Medication();

                    medication.setName(medicationDatum.getDescr());
                    medication.setDescription(medicationDatum.getMPres()
                        + " "
                        + medicationDatum.getMForm()
                        + " "
                        + medicationDatum.getMDosg()
                        + " "
                        + medicationDatum.getUnitForm());

                    medicationsReturned.add(medication);
                  }

                  setupMedicationsList(medicationsReturned);

                  pb_loading_indicator.setVisibility(View.GONE);
                  searchResultsListLayout.setVisibility(View.VISIBLE);
                  noSearchResultsFoundLayout.setVisibility(View.GONE);
                } else {
                  pb_loading_indicator.setVisibility(View.GONE);
                  searchResultsListLayout.setVisibility(View.GONE);
                  noSearchResultsFoundLayout.setVisibility(View.VISIBLE);
                }

                break;
            }
          }

          @Override public void onFailure(Call<SearchMedicinesResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(SearchMedicationsActivity.this).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void getMostPopularMedications() {
    dataAccessHandler.getMostPopularMedications("2012250", "1892870", "422", "2", "2013",
        new Callback<MostPopularMedicationsResponse>() {
          @Override public void onResponse(Call<MostPopularMedicationsResponse> call,
              Response<MostPopularMedicationsResponse> response) {
            switch (response.code()) {
              case 200:
                final List<Medication> medicationsReturned = new ArrayList<>();

                List<MostPopularMedicationsResponseDatum> medicationsFromResponse =
                    response.body().getData().getBody().getData().getItemLst();

                for (int i = 0; i < medicationsFromResponse.size(); i++) {

                  MostPopularMedicationsResponseDatum medicationDatum =
                      medicationsFromResponse.get(i);

                  Medication medication = new Medication();

                  medication.setName(medicationDatum.getDescr());
                  medication.setDescription(medicationDatum.getMPres()
                      + " "
                      + medicationDatum.getMForm()
                      + " "
                      + medicationDatum.getMDosg()
                      + " "
                      + medicationDatum.getUnitForm());

                  medicationsReturned.add(medication);
                }

                setupMedicationsList(medicationsReturned);

                searchResultsListLayout.setVisibility(View.VISIBLE);
                noSearchResultsFoundLayout.setVisibility(View.GONE);

                break;
            }
          }

          @Override public void onFailure(Call<MostPopularMedicationsResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(SearchMedicationsActivity.this).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void setupMedicationsList(List<Medication> medicationList) {
    mealsAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    SearchMedicationsRecyclerAdapter medicationsRecyclerAdapter =
        new SearchMedicationsRecyclerAdapter(this, medicationList);
    mealsAvailableRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    mealsAvailableRecyclerView.setAdapter(medicationsRecyclerAdapter);
  }
}
