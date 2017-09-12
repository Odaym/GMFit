package com.mcsaatchi.gmfit.insurance.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.insurance.activities.directory.DirectorySearchFilterActivity;
import com.mcsaatchi.gmfit.insurance.adapters.ClinicAddressesRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.adapters.CustomInfoWindowAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementTrackActivity.SEARCH_CRITERIA_SELECTED;

public class InsuranceDirectoryFragment extends BaseFragment
    implements InsuranceDirectoryFragmentPresenter.InsuranceDirectoryFragmentView,
    OnMapReadyCallback{

  private static final int PERMISSION_LOCATION_REQUEST_CODE = 375;

  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Bind(R.id.clinicAddressesRecyclerView) RecyclerView clinicAddressRecycler;
  @Bind(R.id.loadingMapProgress) ProgressBar loadingMapProgress;
  @Bind(R.id.mapKeyLayout) LinearLayout mapKeyLayout;
  @Bind(R.id.searchBarET) EditText searchBarET;
  @Bind(R.id.searchImage) ImageView searchImage;
  @Bind(R.id.searchResultsLayout) RelativeLayout searchResultsLayout;
  @Bind(R.id.clearFilterTV) TextView clearFilterTV;

  private boolean searchBarClicked = false;

  private InsuranceDirectoryFragmentPresenter presenter;
  private List<GetNearbyClinicsResponseDatum> clinicsWithLocation = new ArrayList<>();
  private double[] userLatLong = new double[2];
  private boolean listingVisible = false;
  private LocationManager lm;

  private GoogleMap map;
  private ImageView switchMapViewBTN;
  private ViewGroup parentFragmentView;
  private ImageView contractChooserBTN;
  private WorkaroundMapFragment mapFragment;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_directory, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    presenter = new InsuranceDirectoryFragmentPresenter(this, dataAccessHandler);

    parentFragmentView = ((ViewGroup) getParentFragment().getView());

    contractChooserBTN = parentFragmentView.findViewById(R.id.contractChooserBTN);

    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    mapFragment = ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
    mapFragment.setListener(() -> {

    });

    if (prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
      mapKeyLayout.setVisibility(View.INVISIBLE);
    }

    setupSwitchMapViewButton();

    searchBarET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        List<GetNearbyClinicsResponseDatum> searchResults = new ArrayList<>();

        if (editable.toString().length() > 2) {
          searchImage.setImageResource(R.drawable.ic_clear_search);
          searchImage.setOnClickListener(view -> searchBarET.setText(""));

          for (int i = 0; i < clinicsWithLocation.size(); i++) {
            if (clinicsWithLocation.get(i)
                .getName()
                .toLowerCase()
                .contains(editable.toString().toLowerCase())) {
              searchResults.add(clinicsWithLocation.get(i));
            }
          }

          if (getActivity() != null) {
            displayClinicAddressesRecycler(searchResults);
          }
        } else if (editable.toString().isEmpty()) {
          searchImage.setImageResource(R.drawable.ic_search_holo_light);
          searchResultsLayout.setVisibility(View.GONE);

          displayClinicAddressesRecycler(clinicsWithLocation);
        }
      }
    });

    searchBarET.setOnTouchListener((view, motionEvent) -> {
      if (!searchBarClicked) switchMapViewBTN.performClick();
      searchBarClicked = !searchBarClicked;
      return false;
    });

    return fragmentView;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case PERMISSION_LOCATION_REQUEST_CODE:
        if (ActivityCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

          Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
          userLatLong[0] = location.getLatitude();
          userLatLong[1] = location.getLongitude();

          getNearbyClinics(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "H",
              Integer.parseInt(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, "422")),
              userLatLong[1], userLatLong[0], 0);
        }

        break;
      case SEARCH_CRITERIA_SELECTED:
        String countrySelectedCode, citySelectedCode, serviceSelectedCode, typeSelected,
            networkSelected, statusSelected;

        if (data != null) {
          //Unused so far
          ArrayList<String> workingDaysCriteria =
              data.getExtras().getStringArrayList("WORKING_DAYS");

          countrySelectedCode = data.getExtras().getString("Country_code");
          citySelectedCode = data.getExtras().getString("City_code");
          serviceSelectedCode = data.getExtras().getString("Service_code");
          typeSelected = data.getExtras().getString("Type");
          networkSelected = data.getExtras().getString("Status");
          statusSelected = data.getExtras().getString("Network");

          String finalProviderType = "D";

          if (typeSelected != null) {
            switch (typeSelected) {
              case "Hospital":
                finalProviderType = "H";
                break;
              case "Doctor":
                finalProviderType = "D";
                break;
              case "Clinic":
                finalProviderType = "C";
                break;
            }
          }

          if (citySelectedCode == null) {
            citySelectedCode = "0";
          }

          applySearchFilters(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
              Integer.parseInt(countrySelectedCode), Integer.parseInt(citySelectedCode),
              finalProviderType, 0);
        }

        break;
    }
  }

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);

    if (isVisibleToUser) {
      contractChooserBTN.setVisibility(View.INVISIBLE);
      switchMapViewBTN.setVisibility(View.VISIBLE);
      getUserLocation();
    } else {
      if (contractChooserBTN != null && switchMapViewBTN != null) {
        if (!prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
          contractChooserBTN.setVisibility(View.VISIBLE);
        }
        switchMapViewBTN.setVisibility(View.INVISIBLE);
      }
    }
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    filterClinicsAndAddMarkers();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
      case PERMISSION_LOCATION_REQUEST_CODE: {
        if (ActivityCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

          Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
          userLatLong[0] = location.getLatitude();
          userLatLong[1] = location.getLongitude();

          getNearbyClinics(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "H",
              Integer.parseInt(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, "422")),
              userLatLong[1], userLatLong[0], 0);
        }
      }
    }
  }

  @Override public void displayNearbyClinics(List<GetNearbyClinicsResponseDatum> clinicsList) {
    for (int i = 0; i < clinicsList.size(); i++) {
      if (clinicsList.get(i).getLatitude() != null && clinicsList.get(i).getLongitude() != null) {
        clinicsWithLocation.add(clinicsList.get(i));
      }
    }

    if (getActivity() != null) {
      displayClinicAddressesRecycler(clinicsWithLocation);

      mapFragment.getMapAsync(InsuranceDirectoryFragment.this);
      mapFragment.setListener(() -> {
        NestedScrollView myScrollingContent = getActivity().findViewById(R.id.myScrollingContent);
        myScrollingContent.requestDisallowInterceptTouchEvent(true);
      });
    }
  }

  @Override public void displaySearchResults(List<GetNearbyClinicsResponseDatum> searchResults) {
    displayClinicAddressesRecycler(searchResults);
    addMarkersToMap(searchResults, true);
  }

  @OnClick(R.id.myLocationLayout) public void handleOnMyLocationPressed() {
    zoomAnimateCamera(new MarkerOptions().position(new LatLng(userLatLong[0], userLatLong[1])));
  }

  @OnClick(R.id.filtersLayout) public void handleFiltersLayoutClicked() {
    searchBarET.setText("");

    Intent intent = new Intent(getActivity(), DirectorySearchFilterActivity.class);
    startActivityForResult(intent, SEARCH_CRITERIA_SELECTED);
  }

  private void displayClinicAddressesRecycler(List<GetNearbyClinicsResponseDatum> clinicsList) {
    ClinicAddressesRecyclerAdapter clinicAddressesRecyclerAdapter =
        new ClinicAddressesRecyclerAdapter((GMFitApplication) getActivity().getApplication(),
            getActivity(), clinicsList);

    clinicAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    clinicAddressRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    clinicAddressRecycler.setHasFixedSize(true);
    clinicAddressRecycler.setAdapter(clinicAddressesRecyclerAdapter);
  }

  private void getNearbyClinics(String contractNo, String providerTypesCode, int searchCtry,
      double longitude, double latitude, int fetchClosest) {
    loadingMapProgress.setVisibility(View.VISIBLE);

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.get_nearby_clinics_message);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());

    presenter.getNearbyClinics(contractNo, providerTypesCode, searchCtry, longitude, latitude,
        fetchClosest);
  }

  private void applySearchFilters(String contractNo, int searchCtry, int searchCity,
      String providerTypesCode, int fetchClosest) {
    loadingMapProgress.setVisibility(View.VISIBLE);

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.applying_search_filters);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());

    presenter.applySearchFilters(contractNo, searchCtry, searchCity, providerTypesCode,
        fetchClosest);
  }

  private void setupSwitchMapViewButton() {
    if (parentFragmentView != null) {
      switchMapViewBTN = parentFragmentView.findViewById(R.id.switchMapViewBTN);

      switchMapViewBTN.setOnClickListener(view -> {
        if (!listingVisible) {
          switchMapViewBTN.setImageResource(R.drawable.ic_show_directory_as_map);
          clinicAddressRecycler.setVisibility(View.VISIBLE);
        } else {
          switchMapViewBTN.setImageResource(R.drawable.ic_show_directory_as_listing);
          clinicAddressRecycler.setVisibility(View.INVISIBLE);
        }

        listingVisible = !listingVisible;
      });
    }
  }

  private void filterClinicsAndAddMarkers() {
    List<GetNearbyClinicsResponseDatum> filteredClinics =
        filterValidClinics(userLatLong, clinicsWithLocation);

    addMarkersToMap(filteredClinics, false);
  }

  private List<GetNearbyClinicsResponseDatum> filterValidClinics(double[] userLatLong,
      List<GetNearbyClinicsResponseDatum> clinicsToWorkWith) {
    Location userLocation = new Location("You");
    userLocation.setLatitude(userLatLong[0]);
    userLocation.setLongitude(userLatLong[1]);

    List<GetNearbyClinicsResponseDatum> filteredClinics = new ArrayList<>();

    for (int i = 0; i < clinicsToWorkWith.size(); i++) {
      Location targetLocation = new Location(clinicsToWorkWith.get(i).getName());
      targetLocation.setLongitude(Double.parseDouble(clinicsToWorkWith.get(i).getLongitude()));
      targetLocation.setLatitude(Double.parseDouble(clinicsToWorkWith.get(i).getLatitude()));

      if (userLocation.distanceTo(targetLocation) < 10000) {
        filteredClinics.add(clinicsToWorkWith.get(i));
      }
    }

    return filteredClinics;
  }

  private void addMarkersToMap(List<GetNearbyClinicsResponseDatum> validClinics,
      boolean fromSearch) {
    map.getUiSettings().setMyLocationButtonEnabled(true);
    map.clear();

    MarkerOptions locationMarker = null;

    MarkerOptions youMarker =
        new MarkerOptions().position(new LatLng(userLatLong[0], userLatLong[1]))
            .title("You")
            .snippet("")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_you_custom_map_marker));

    map.addMarker(youMarker);

    for (int i = 0; i < validClinics.size(); i++) {
      StringBuilder snippet = new StringBuilder();

      if (validClinics.get(i).getPartOfNetwork() != null && validClinics.get(i)
          .getPartOfNetwork()) {
        snippet.append("N");
      }

      if (validClinics.get(i).getOnline() != null && validClinics.get(i).getOnline()) {
        snippet.append("O");
      }

      if (validClinics.get(i).getTwentyfourseven() != null && validClinics.get(i)
          .getTwentyfourseven()) {
        snippet.append("247");
      }

      try {
        if (validClinics.get(i).getLatitude() != null
            && validClinics.get(i).getLongitude() != null) {
          locationMarker = new MarkerOptions().position(
              new LatLng(Double.parseDouble(validClinics.get(i).getLatitude()),
                  Double.parseDouble(validClinics.get(i).getLongitude())))
              .title(validClinics.get(i).getName())
              .snippet(snippet.toString())
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_map_marker));

          map.addMarker(locationMarker);

          map.setInfoWindowAdapter(
              new CustomInfoWindowAdapter((GMFitApplication) getActivity().getApplication(),
                  getActivity(), validClinics.get(i)));

          GetNearbyClinicsResponseDatum clinic = validClinics.get(i);

          //map.setOnInfoWindowClickListener(marker -> {
          //  Intent intent = new Intent(getActivity(), ClinicDetailsActivity.class);
          //  intent.putExtra("CLINIC_OBJECT", clinic);
          //  getActivity().startActivity(intent);
          //});
        }
      } catch (NullPointerException ignored) {
      }
    }

    if (fromSearch) {
      zoomAnimateCamera(locationMarker);
    } else {
      zoomAnimateCamera(youMarker);
    }
  }

  private void getUserLocation() {
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] {
          Manifest.permission.ACCESS_FINE_LOCATION
      }, PERMISSION_LOCATION_REQUEST_CODE);
    } else {
      Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      if (location != null) {
        userLatLong[0] = location.getLatitude();
        userLatLong[1] = location.getLongitude();

        getNearbyClinics(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "H",
            Integer.parseInt(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, "422")),
            userLatLong[1], userLatLong[0], 0);
      }
    }
  }

  private void zoomAnimateCamera(MarkerOptions marker) {
    if (marker != null) {
      CameraUpdate center = CameraUpdateFactory.newLatLng(
          new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
      CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

      if (map != null) {
        map.moveCamera(center);
        map.animateCamera(zoom, 400, null);

        loadingMapProgress.setVisibility(View.GONE);
      }
    }
  }
}
