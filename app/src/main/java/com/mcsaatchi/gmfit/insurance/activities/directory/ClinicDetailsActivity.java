package com.mcsaatchi.gmfit.insurance.activities.directory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponseDatum;
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

        phoneTV.setText(clinicObject.getPhone());
        mobileTV.setText(clinicObject.getMobile());
        emailAddressTV.setText(clinicObject.getName().split(" ")[0] + "_clinic@gmail.com");

        if (clinicObject.getOnline() != null && clinicObject.getOnline()) {
          onlineNowLayout.setVisibility(View.VISIBLE);
        }

        if (clinicObject.getPartOfNetwork() != null && clinicObject.getPartOfNetwork()) {
          withinNetworkLayout.setVisibility(View.VISIBLE);
        }

        if (clinicObject.getTwentyfourseven() != null && clinicObject.getTwentyfourseven()) {
          open247Layout.setVisibility(View.VISIBLE);
        }

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

  @OnClick(R.id.locationDetailsLayout) public void handleLocationDetailsClicked() {
    double latitude = Double.parseDouble(clinicObject.getLatitude());
    double longitude = Double.parseDouble(clinicObject.getLongitude());
    String label = clinicObject.getName();
    String uriBegin = "geo:" + latitude + "," + longitude;
    String query = latitude + "," + longitude + "(" + label + ")";
    String encodedQuery = Uri.encode(query);
    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
    Uri uri = Uri.parse(uriString);
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  @OnClick(R.id.mobileTV) public void handleMobileClicked() {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + mobileTV.getText().toString()));
    startActivity(intent);
  }

  @OnClick(R.id.phoneTV) public void handlePhoneNumberClicked() {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + phoneTV.getText().toString()));
    startActivity(intent);
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
