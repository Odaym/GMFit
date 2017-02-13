package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ClinicAddressesRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.Clinic;
import com.mcsaatchi.gmfit.insurance.models.ClinicOpeningHours;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDirectoryFragment extends Fragment implements OnMapReadyCallback {
  @Bind(R.id.clinicAddressesRecyclerView) RecyclerView clinicAddressRecycler;
  @Bind(R.id.searchBoxET) EditText searchBoxET;

  private boolean listingVisible = false;

  private WorkaroundMapFragment mapFragment;
  private ImageView switchMapViewBTN;
  private GoogleMap map;
  private ViewGroup parentFragmentView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_directory, container, false);

    ButterKnife.bind(this, fragmentView);

    parentFragmentView = ((ViewGroup) getParentFragment().getView());

    final List<Clinic> listOfClinics = new ArrayList<>();

    listOfClinics.add(new Clinic("Clinic 1", "Beirut, Hamra",
        new ClinicOpeningHours("9:00 AM - 6:00 PM", "9:00 AM - 6:00 PM", "9:00 AM - 6:00 PM",
            "9:00 AM - 6:00 PM", "9:00 AM - 6:00 PM", "9:00 AM - 2:30 PM", "closed"), false, true,
        false));

    listOfClinics.add(new Clinic("Hotel Dieu de France", "Beirut, Achrafieh",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), false, false, false));

    listOfClinics.add(new Clinic("Hospital 422", "Beirut, Koraytem",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), true, false, true));

    listOfClinics.add(new Clinic("Sample Hospital 210", "Aley",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), false, false, true));

    listOfClinics.add(new Clinic("AUBMC", "Beirut, Sanayeh",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), true, true, false));

    listOfClinics.add(new Clinic("Zahraa Hospital", "Beirut, Verdun",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), false, true, false));

    listOfClinics.add(new Clinic("Traad Hospital", "Beirut, Mkalles",
        new ClinicOpeningHours("9:00 AM - 12:00 AM", "9:00 AM - 12:00 AM", "9:00 AM - 12:00 AM",
            "9:00 AM - 12:00 AM", "9:00 AM - 12:00 AM", "9:00 AM - 2:00 PM", "closed"), true, true,
        true));

    listOfClinics.add(new Clinic("Clinic 233", "Beirut, Ras El Nabaa",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), false, false, true));

    listOfClinics.add(new Clinic("Medicinal House Building", "Beirut, Ras El Nabaa",
        new ClinicOpeningHours("9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM",
            "9:00 AM - 5:00 PM", "9:00 AM - 5:00 PM", "closed", "closed"), false, false, false));

    mapFragment = ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
    mapFragment.getMapAsync(this);
    mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
      @Override public void onTouch() {
        NestedScrollView myScrollingContent =
            ((NestedScrollView) getActivity().findViewById(R.id.myScrollingContent));
        myScrollingContent.requestDisallowInterceptTouchEvent(true);
      }
    });

    setupSwitchMapViewButton();

    ClinicAddressesRecyclerAdapter clinicAddressesRecyclerAdapter =
        new ClinicAddressesRecyclerAdapter(getActivity(), listOfClinics);

    clinicAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    clinicAddressRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    clinicAddressRecycler.setHasFixedSize(true);
    clinicAddressRecycler.setAdapter(clinicAddressesRecyclerAdapter);

    searchBoxET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        List<Clinic> searchResults = new ArrayList<Clinic>();

        for (int j = 0; j < listOfClinics.size(); j++) {
          if (listOfClinics.get(j)
              .getName()
              .toLowerCase()
              .contains(editable.toString().toLowerCase())) {
            searchResults.add(listOfClinics.get(j));
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

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    map.addMarker(
        new MarkerOptions().position(new LatLng(44.968046, -94.420307)).title("Marker in 1"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(49.33328, -89.132008)).title("Marker in 2"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(33.755787, -116.359998)).title("Marker in 2"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(33.844843, -116.54911)).title("Marker in 3"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(46.92057, -93.44786)).title("Marker in 4"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(42.240309, -91.493619)).title("Marker in 5"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(41.968041, -94.419696)).title("Marker in 6"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(44.333304, -89.132027)).title("Marker in 7"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(33.755783, -116.360066)).title("Marker in 8"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(33.844847, -116.549069)).title("Marker in 9"));
    map.addMarker(
        new MarkerOptions().position(new LatLng(31.920474, .447851)).title("Marker in 10"));

    map.addMarker(
        new MarkerOptions().position(new LatLng(33.8938, 35.5018)).title("Marker in Beirut"));

    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.8938, 35.5018)));
  }

  private void setupSwitchMapViewButton() {
    if (parentFragmentView != null) {
      switchMapViewBTN = (ImageView) parentFragmentView.findViewById(R.id.switchMapViewBTN);

      switchMapViewBTN.setVisibility(View.VISIBLE);

      switchMapViewBTN.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (!listingVisible) {
            switchMapViewBTN.setImageResource(R.drawable.ic_show_directory_as_map);
            clinicAddressRecycler.setVisibility(View.VISIBLE);
          } else {
            switchMapViewBTN.setImageResource(R.drawable.ic_show_directory_as_listing);
            clinicAddressRecycler.setVisibility(View.INVISIBLE);
          }

          listingVisible = !listingVisible;
        }
      });
    }
  }
}
