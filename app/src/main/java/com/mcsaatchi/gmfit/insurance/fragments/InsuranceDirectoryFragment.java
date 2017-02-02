package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ClinicAddressesRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.Clinic;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDirectoryFragment extends Fragment implements OnMapReadyCallback {
  @Bind(R.id.clinicAddressesRecyclerView) RecyclerView clinicAddressRecycler;

  private GoogleMap map;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_directory, container, false);

    ButterKnife.bind(this, fragmentView);

    List<Clinic> listOfClinics = new ArrayList<>();

    listOfClinics.add(new Clinic("Clinic 1", "Beirut, Achrafieh"));
    listOfClinics.add(new Clinic("Clinic 2", "Beirut, Ras Beirut"));
    listOfClinics.add(new Clinic("Clinic 4", "Beirut, Sanayeh"));
    listOfClinics.add(new Clinic("Clinic 1", "Beirut, Fassouh"));
    listOfClinics.add(new Clinic("Clinic 52", "Beirut, Ras el Nabaa"));

    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    ClinicAddressesRecyclerAdapter clinicAddressesRecyclerAdapter =
        new ClinicAddressesRecyclerAdapter(getActivity(), listOfClinics);

    clinicAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    clinicAddressRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    clinicAddressRecycler.setHasFixedSize(true);
    clinicAddressRecycler.setAdapter(clinicAddressesRecyclerAdapter);

    return fragmentView;
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    // Add a marker in Sydney, Australia, and move the camera.
    LatLng sydney = new LatLng(-34, 151);
    map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
  }
}
