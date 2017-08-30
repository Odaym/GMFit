package com.mcsaatchi.gmfit.insurance.activities.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.FilterChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.FilterChoice;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import java.util.ArrayList;
import java.util.List;

import static com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementTrackActivity.SEARCH_CRITERIA_SELECTED;

public class DirectorySearchFilterActivity extends BaseActivity
    implements DirectorySearchFilterActivityPresenter.DirectorySearchFilterActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.countryPicker) CustomPicker countryPicker;
  @Bind(R.id.cityPicker) CustomPicker cityPicker;
  @Bind(R.id.typePicker) CustomPicker typePicker;
  @Bind(R.id.servicesPicker) CustomPicker servicesPicker;
  @Bind(R.id.statusPicker) CustomPicker statusPicker;
  @Bind(R.id.networkPicker) CustomPicker networkPicker;
  @Bind(R.id.workingDaysRecycler) RecyclerView workingDaysRecycler;

  private String countrySelectedCode, citySelected, typeSelected, serviceSelected, networkSelected,
      statusSelected;

  private DirectorySearchFilterActivityPresenter presenter;
  private FilterChoiceRecyclerAdapter filterChoiceRecyclerAdapter;
  private ArrayList<FilterChoice> workingDays;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_directory_search_filter);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.advanced_search_activity_title), true);

    presenter = new DirectorySearchFilterActivityPresenter(this, dataAccessHandler);

    presenter.getCountriesList();

    presenter.getCitiesList();

    setupCitiesPicker();
    setupTypePicker();
    setupServicesPicker();
    setupStatusPicker();
    setupNetworkPicker();

    workingDays = getWorkingDayChoices();
    setupWorkingDaysRecycler(workingDays);
  }

  @Override
  public void populateCountriesDropdown(List<CountriesListResponseDatum> countriesResponse) {
    ArrayList<String> countries = new ArrayList<>();

    for (int i = 0; i < countriesResponse.size(); i++) {
      if (countriesResponse.get(i) != null) {
        countries.add(countriesResponse.get(i).getLabel());
      }
    }

    if (!countries.isEmpty()) {
      countryPicker.setUpDropDown("Country", "Choose a Country",
          countries.toArray(new String[countries.size()]), (index, selected) -> {
            for (int i = 0; i < countriesResponse.size(); i++) {
              if (countriesResponse.get(i).getLabel().equals(selected)) {
                countrySelectedCode = countriesResponse.get(i).getIsoCode();
              }
            }
          });
    }

    cityPicker.setUpDropDown("City", "Choose a City", new String[] { "Empty", "Empty", "Empty" },
        (index, selected) -> serviceSelected = selected);
  }

  @OnClick(R.id.searchFiltersBTN) public void handleApplySearchTerms() {
    ArrayList<String> finalWorkingDays = new ArrayList<>();

    for (int i = 0; i < workingDays.size(); i++) {
      if (workingDays.get(i).isSelected() && workingDays.get(i).getName().equals("Any")) {
        finalWorkingDays.clear();

        for (int i1 = 0; i1 < workingDays.size(); i1++) {
          if (!workingDays.get(i1).getName().equals("Any")) {
            finalWorkingDays.add(workingDays.get(i1).getName());
          }
        }
      } else if (workingDays.get(i).isSelected()) {
        finalWorkingDays.add(workingDays.get(i).getName());
      }
    }

    Intent intent = new Intent();
    intent.putStringArrayListExtra("WORKING_DAYS", finalWorkingDays);
    intent.putExtra("Country", countrySelectedCode);
    intent.putExtra("Service", serviceSelected);
    intent.putExtra("Network", networkSelected);
    intent.putExtra("Status", statusSelected);
    intent.putExtra("City", citySelected);
    intent.putExtra("Type", typeSelected);
    setResult(SEARCH_CRITERIA_SELECTED, intent);
    finish();
  }

  private ArrayList<FilterChoice> getWorkingDayChoices() {
    ArrayList<FilterChoice> choicesToReturn = new ArrayList<>();
    choicesToReturn.add(new FilterChoice("Any", false));
    choicesToReturn.add(new FilterChoice("Always Open (24/7)", false));

    return choicesToReturn;
  }

  private void setupWorkingDaysRecycler(ArrayList<FilterChoice> workingDays) {
    filterChoiceRecyclerAdapter = new FilterChoiceRecyclerAdapter(workingDays);
    workingDaysRecycler.setLayoutManager(new LinearLayoutManager(this));
    workingDaysRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
    workingDaysRecycler.setAdapter(filterChoiceRecyclerAdapter);
  }

  private void setupCitiesPicker() {
    typePicker.setUpDropDown("City", "Choose a City", new String[] { "" },
        (index, selected) -> citySelected = selected);
  }

  private void setupTypePicker() {
    typePicker.setUpDropDown("Type", "Choose a Type",
        new String[] { "Hospital", "Doctor", "Clinic" },
        (index, selected) -> typeSelected = selected);
  }

  private void setupServicesPicker() {
    servicesPicker.setUpDropDown("Services", "Choose a Service",
        new String[] { "Empty", "Empty", "Empty" },
        (index, selected) -> serviceSelected = selected);
  }

  private void setupStatusPicker() {
    statusPicker.setUpDropDown("Status", "Choose a Status", new String[] { "Online", "Offline" },
        (index, selected) -> statusSelected = selected);
  }

  private void setupNetworkPicker() {
    networkPicker.setUpDropDown("Network", "Choose a Network",
        new String[] { "All", "Within Network" }, (index, selected) -> networkSelected = selected);
  }
}
