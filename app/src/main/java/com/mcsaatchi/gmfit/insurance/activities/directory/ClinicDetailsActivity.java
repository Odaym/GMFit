package com.mcsaatchi.gmfit.insurance.activities.directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ClinicDetailsActivity extends BaseActivity implements OnMapReadyCallback {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Bind(R.id.clinicNameTV) TextView clinicNameTV;
  @Bind(R.id.clinicAddressTV) TextView clinicAddressTV;
  @Bind(R.id.phoneTV) TextView phoneTV;
  @Bind(R.id.mobileTV) TextView mobileTV;
  @Bind(R.id.emailAddressTV) TextView emailAddressTV;
  @Bind(R.id.clinicNameOnMapTV) TextView clinicNameOnMapTV;
  @Bind(R.id.clinicAddressOnMapTV) TextView clinicAddressOnMapTV;

  @Bind(R.id.withinNetworkLayout) LinearLayout withinNetworkLayout;
  @Bind(R.id.open247Layout) LinearLayout open247Layout;
  @Bind(R.id.onlineNowLayout) LinearLayout onlineNowLayout;

  private GetNearbyClinicsResponseDatum clinicObject;
  private GoogleMap map;
  private SupportMapFragment mapFragment;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_clinic_details);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.clinic_details_activity_title), true);

    if (getIntent().getExtras() != null) {
      clinicObject = getIntent().getExtras().getParcelable("CLINIC_OBJECT");

      if (clinicObject != null) {
        clinicNameTV.setText(clinicObject.getName());
        clinicAddressTV.setText(clinicObject.getAddress());

        phoneTV.setText("+961 1 884 001");
        mobileTV.setText("+961 70 770 441");
        emailAddressTV.setText(clinicObject.getName().split(" ")[0] + "_clinic@gmail.com");

        if (clinicObject.getOnline() != null) onlineNowLayout.setVisibility(View.VISIBLE);

        if (clinicObject.getPartOfNetwork()) withinNetworkLayout.setVisibility(View.VISIBLE);

        if (clinicObject.getTwentyfourseven()) open247Layout.setVisibility(View.VISIBLE);

        clinicNameOnMapTV.setText(clinicObject.getName());
        clinicAddressTV.setText(clinicObject.getAddress());

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
      }
    }
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    map.getUiSettings().setRotateGesturesEnabled(false);
    map.getUiSettings().setZoomControlsEnabled(false);
    map.getUiSettings().setZoomGesturesEnabled(false);
    map.getUiSettings().setTiltGesturesEnabled(false);

    zoomAnimateCamera();
  }

  private void zoomAnimateCamera() {
    map.addMarker(new MarkerOptions().position(
        new LatLng(Double.parseDouble(clinicObject.getLatitude()),
            Double.parseDouble(clinicObject.getLongitude()))).title(clinicObject.getName()));

    CameraUpdate center = CameraUpdateFactory.newLatLng(
        new LatLng(Double.parseDouble(clinicObject.getLatitude()),
            Double.parseDouble(clinicObject.getLongitude())));
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

    map.moveCamera(center);
    map.animateCamera(zoom, 400, null);
  }
}
