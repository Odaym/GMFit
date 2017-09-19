package com.mcsaatchi.gmfit.insurance.activities.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CitiesListResponseCityVOArr;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ServicesListResponseServiceVOArr;
import com.mcsaatchi.gmfit.common.Constants;
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

  private String countrySelectedCode, citySelectedCode, typeSelected, serviceSelectedCode,
      networkSelected, statusSelected;

  private DirectorySearchFilterActivityPresenter presenter;
  private ArrayList<FilterChoice> workingDays;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_directory_search_filter);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.advanced_search_activity_title), true);

    presenter = new DirectorySearchFilterActivityPresenter(this, dataAccessHandler);

    presenter.getCountriesList();

    presenter.getServicesList();

    setupTypePicker();
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

    setupDefaultCountryAndCities(countriesResponse);

    if (!countries.isEmpty()) {
      countryPicker.setUpDropDown(getString(R.string.country_dropdown_title), getString(R.string.country_dropdown_message),
          countries.toArray(new String[countries.size()]), (index, selected) -> {
            for (int i = 0; i < countriesResponse.size(); i++) {
              if (countriesResponse.get(i).getLabel().equals(selected)) {
                countrySelectedCode = countriesResponse.get(i).getIsoCode();
                presenter.getCitiesList(countrySelectedCode);
              }
            }
          });
    }
  }

  @Override
  public void populateCitiesDropdown(List<CitiesListResponseCityVOArr> citiesListResponses) {
    ArrayList<String> cities = new ArrayList<>();

    for (int i = 0; i < citiesListResponses.size(); i++) {
      if (citiesListResponses.get(i) != null) {
        cities.add(citiesListResponses.get(i).getCityName());
      }
    }

    if (!cities.isEmpty()) {
      cityPicker.setUpDropDown(getString(R.string.city_dropdown_title), getString(R.string.city_dropdown_message), cities.toArray(new String[cities.size()]),
          (index, selected) -> {
            for (int i = 0; i < citiesListResponses.size(); i++) {
              if (citiesListResponses.get(i).getCityName() != null) {
                if (citiesListResponses.get(i).getCityName().equals(selected)) {
                  citySelectedCode = citiesListResponses.get(i).getCityCode();
                }
              }
            }
          });
    }
  }

  @Override public void populateServicesDropdown(
      List<ServicesListResponseServiceVOArr> servicesListResponseServiceVOArrs) {
    ArrayList<String> services = new ArrayList<>();

    for (int i = 0; i < servicesListResponseServiceVOArrs.size(); i++) {
      if (servicesListResponseServiceVOArrs.get(i) != null) {
        services.add(servicesListResponseServiceVOArrs.get(i).getServiceName());
      }
    }

    if (!services.isEmpty()) {
      servicesPicker.setUpDropDown(getString(R.string.service_dropdown_title), getString(R.string.service_dropdown_message),
          services.toArray(new String[services.size()]), (index, selected) -> {
            for (int i = 0; i < servicesListResponseServiceVOArrs.size(); i++) {
              if (servicesListResponseServiceVOArrs.get(i).getServiceName().equals(selected)) {
                serviceSelectedCode = servicesListResponseServiceVOArrs.get(i).getServiceCode();
              }
            }
          });
    }
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

    if (countrySelectedCode == null) {
      final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setTitle(R.string.required_fields_dialog_title);
      alertDialog.setMessage(getString(R.string.required_fields_for_search));
      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
          (dialog, which) -> dialog.dismiss());
      alertDialog.show();
    } else {
      Intent intent = new Intent();
      intent.putStringArrayListExtra("WORKING_DAYS", finalWorkingDays);
      intent.putExtra("Country_code", countrySelectedCode);
      intent.putExtra("Service_code", serviceSelectedCode);
      intent.putExtra("City_code", citySelectedCode);
      intent.putExtra("Network", networkSelected);
      intent.putExtra("Status", statusSelected);
      intent.putExtra("Type", typeSelected);
      setResult(SEARCH_CRITERIA_SELECTED, intent);
      finish();
    }
  }

  private void setupDefaultCountryAndCities(List<CountriesListResponseDatum> countriesResponse) {
    for (int i = 0; i < countriesResponse.size(); i++) {
      if (countriesResponse.get(i)
          .getIsoCode()
          .equals(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""))) {
        countryPicker.setSelectedItem(countriesResponse.get(i).getLabel());
        presenter.getCitiesList(countriesResponse.get(i).getIsoCode());
      }
    }
  }

  private ArrayList<FilterChoice> getWorkingDayChoices() {
    ArrayList<FilterChoice> choicesToReturn = new ArrayList<>();
    choicesToReturn.add(new FilterChoice("Any", false));
    choicesToReturn.add(new FilterChoice("Always Open (24/7)", false));

    return choicesToReturn;
  }

  private void setupWorkingDaysRecycler(ArrayList<FilterChoice> workingDays) {
    FilterChoiceRecyclerAdapter filterChoiceRecyclerAdapter =
        new FilterChoiceRecyclerAdapter(workingDays);
    workingDaysRecycler.setLayoutManager(new LinearLayoutManager(this));
    workingDaysRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
    workingDaysRecycler.setAdapter(filterChoiceRecyclerAdapter);
  }

  private void setupTypePicker() {
    typePicker.setUpDropDown(getString(R.string.type_dropdown_title), getString(R.string.type_dropdown_message),
        new String[] { getString(R.string.type_dropdown_hospital_item), getString(R.string.type_dropdown_doctor_item), getString(
                    R.string.type_dropdown_clinic_item) },
        (index, selected) -> typeSelected = selected);
  }

  private void setupStatusPicker() {
    statusPicker.setUpDropDown(getString(R.string.status_dropdown_title), getString(R.string.status_dropdown_message), new String[] { getString(
            R.string.status_dropdown_online_item), getString(R.string.status_dropdown_offline_item) },
        (index, selected) -> statusSelected = selected);
  }

  private void setupNetworkPicker() {
    networkPicker.setUpDropDown(getString(R.string.network_dropdown_title), getString(R.string.network_dropdown_message),
        new String[] { getString(R.string.network_dropdown_all_item), getString(R.string.network_dropdown_within_network_item) }, (index, selected) -> networkSelected = selected);
  }
}
