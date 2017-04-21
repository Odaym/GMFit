package com.mcsaatchi.gmfit.insurance.fragments;

import android.Manifest;
import android.content.Context;
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
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.insurance.adapters.ClinicAddressesRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.adapters.CustomInfoWindowAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class InsuranceDirectoryFragment extends BaseFragment
    implements InsuranceDirectoryFragmentPresenter.InsuranceDirectoryFragmentView,
    OnMapReadyCallback {
  private static final int PERMISSION_LOCATION_REQUEST_CODE = 375;
  @Bind(R.id.clinicAddressesRecyclerView) RecyclerView clinicAddressRecycler;
  @Bind(R.id.searchBoxET) EditText searchBoxET;
  @Bind(R.id.loadingMapProgress) ProgressBar loadingMapProgress;
  @Bind(R.id.mapKeyLayout) LinearLayout mapKeyLayout;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private InsuranceDirectoryFragmentPresenter presenter;

  private boolean listingVisible = false;
  private List<GetNearbyClinicsResponseDatum> clinicsWithLocation = new ArrayList<>();
  private LocationManager lm;
  private double[] userLatLong = new double[2];

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

    contractChooserBTN = (ImageView) parentFragmentView.findViewById(R.id.contractChooserBTN);

    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    mapFragment = ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
    mapFragment.setListener(() -> {

    });

    if (prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
      Timber.d("contract is empty!");
      mapKeyLayout.setVisibility(View.GONE);
    }

    setupSwitchMapViewButton();

    searchBoxET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        List<GetNearbyClinicsResponseDatum> searchResults = new ArrayList<>();

        for (int i = 0; i < clinicsWithLocation.size(); i++) {
          if (clinicsWithLocation.get(i)
              .getName()
              .toLowerCase()
              .contains(editable.toString().toLowerCase())) {
            searchResults.add(clinicsWithLocation.get(i));
          }
        }

        ClinicAddressesRecyclerAdapter clinicAddressesRecyclerAdapter =
            new ClinicAddressesRecyclerAdapter(getActivity(), searchResults);

        clinicAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        clinicAddressRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        clinicAddressRecycler.setHasFixedSize(true);
        clinicAddressRecycler.setAdapter(clinicAddressesRecyclerAdapter);
      }
    });

    return fragmentView;
  }

  @OnClick(R.id.myLocationLayout) public void handleOnMyLocationPressed() {
    zoomAnimateCamera();
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

          getNearbyClinics();
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

    ClinicAddressesRecyclerAdapter clinicAddressesRecyclerAdapter =
        new ClinicAddressesRecyclerAdapter(getActivity(), clinicsWithLocation);

    clinicAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    clinicAddressRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    clinicAddressRecycler.setHasFixedSize(true);
    clinicAddressRecycler.setAdapter(clinicAddressesRecyclerAdapter);

    mapFragment.getMapAsync(InsuranceDirectoryFragment.this);
    mapFragment.setListener(() -> {
      NestedScrollView myScrollingContent =
          ((NestedScrollView) getActivity().findViewById(R.id.myScrollingContent));
      myScrollingContent.requestDisallowInterceptTouchEvent(true);
    });
  }

  private void getNearbyClinics() {
    loadingMapProgress.setVisibility(View.VISIBLE);

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.get_nearby_clinics_message);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());

    presenter.getNearbyClinics(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "H",
        22, userLatLong[1], userLatLong[0], 0);
  }

  private void setupSwitchMapViewButton() {
    if (parentFragmentView != null) {
      switchMapViewBTN = (ImageView) parentFragmentView.findViewById(R.id.switchMapViewBTN);

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

    addMarkersToMap(filteredClinics);
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

  private void addMarkersToMap(List<GetNearbyClinicsResponseDatum> validClinics) {
    map.getUiSettings().setMyLocationButtonEnabled(true);

    map.addMarker(new MarkerOptions().position(new LatLng(userLatLong[0], userLatLong[1]))
        .title("You")
        .snippet("")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_you_custom_map_marker)));

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

      map.addMarker(new MarkerOptions().position(
          new LatLng(Double.parseDouble(validClinics.get(i).getLatitude()),
              Double.parseDouble(validClinics.get(i).getLongitude())))
          .title(validClinics.get(i).getName())
          .snippet(snippet.toString())
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_map_marker)));
      map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));
    }

    zoomAnimateCamera();
  }

  private void getUserLocation() {
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      Timber.d("Do we have permission?");
      requestPermissions(new String[] {
          Manifest.permission.ACCESS_FINE_LOCATION
      }, PERMISSION_LOCATION_REQUEST_CODE);
    } else {
      Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      userLatLong[0] = location.getLatitude();
      userLatLong[1] = location.getLongitude();

      getNearbyClinics();
    }
  }

  private void zoomAnimateCamera() {
    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(userLatLong[0], userLatLong[1]));
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

    map.moveCamera(center);
    map.animateCamera(zoom, 400, null);

    loadingMapProgress.setVisibility(View.GONE);
  }
}
