package com.mcsaatchi.gmfit.health.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicationItemCreatedEvent;
import com.mcsaatchi.gmfit.architecture.rest.MostPopularMedicationsResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SearchMedicinesResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.SearchMedicationsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.Medication;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;

public class SearchMedicationsActivity extends BaseActivity
    implements SearchMedicationsActivityPresenter.SearchMedicationsActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.searchMedicationsTV) EditText searchMedicationsTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;
  @Bind(R.id.pb_loading_indicator) ProgressBar pb_loading_indicator;
  @Bind(R.id.pb_loading_medications_indicator) ProgressBar pb_loading_medications_indicator;
  @Bind(R.id.mealsAvailableRecyclerView) RecyclerView mealsAvailableRecyclerView;
  @Bind(R.id.searchResultsHintTV) TextView searchResultsHintTV;
  @Bind(R.id.searchResultsListLayout) LinearLayout searchResultsListLayout;
  @Bind(R.id.noSearchResultsFoundLayout) LinearLayout noSearchResultsFoundLayout;

  private SearchMedicationsActivityPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_search_medications);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.add_medication_activity_title), true);

    EventBusSingleton.getInstance().register(this);

    presenter = new SearchMedicationsActivityPresenter(this, dataAccessHandler);

    presenter.getPopularMedicines(prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""),
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""), "2", "2013");

    searchMedicationsTV.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
          searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
          searchIconIV.setOnClickListener(null);

          searchResultsHintTV.setText(getString(R.string.most_popular_hint));
          pb_loading_indicator.setVisibility(View.GONE);

          presenter.getPopularMedicines(
              prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""),
              prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
              prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""), "2", "2013");
        } else if (charSequence.toString().length() > 2) {
          searchResultsHintTV.setText(getString(R.string.search_results_list_label));
          pb_loading_indicator.setVisibility(View.VISIBLE);

          new Handler().postDelayed(() -> {
            searchIconIV.setImageResource(R.drawable.ic_clear_search);
            searchIconIV.setOnClickListener(view -> {
              searchMedicationsTV.setText("");

              InputMethodManager imm =
                  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            });

            presenter.searchMedicines(prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""),
                prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
                prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""), "2", "2013",
                charSequence.toString());
          }, 500);
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override
  public void displayMedicineResults(List<SearchMedicinesResponseDatum> medicationsFromResponse) {
    final List<Medication> medicationsReturned = new ArrayList<>();

    if (medicationsFromResponse != null) {
      for (int i = 0; i < medicationsFromResponse.size(); i++) {

        SearchMedicinesResponseDatum medicationDatum = medicationsFromResponse.get(i);

        Medication medication = new Medication();

        medication.setName(medicationDatum.getDescr());
        medication.setUnitForm(medicationDatum.getUnitForm());
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
  }

  @Override public void displayPopularMedicines(
      List<MostPopularMedicationsResponseDatum> medicationsFromResponse) {
    final List<Medication> medicationsReturned = new ArrayList<>();

    for (int i = 0; i < medicationsFromResponse.size(); i++) {

      MostPopularMedicationsResponseDatum medicationDatum = medicationsFromResponse.get(i);

      Medication medication = new Medication();

      medication.setName(medicationDatum.getDescr());
      medication.setUnitForm(medicationDatum.getUnitForm());
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
  }

  @Subscribe public void reactToMedicationAdded(MedicationItemCreatedEvent event) {
    finish();
  }

  private void setupMedicationsList(List<Medication> medicationList) {
    mealsAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    SearchMedicationsRecyclerAdapter medicationsRecyclerAdapter =
        new SearchMedicationsRecyclerAdapter(this, medicationList);
    mealsAvailableRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    mealsAvailableRecyclerView.setAdapter(medicationsRecyclerAdapter);

    pb_loading_medications_indicator.setVisibility(View.GONE);
  }
}
