package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import javax.inject.Inject;

public class CustomInfoWindowAdapter
    implements GoogleMap.InfoWindowAdapter {

  @Bind(R.id.open247Layout) LinearLayout open247Layout;
  @Bind(R.id.withinNetworkLayout) LinearLayout withinNetworkLayout;
  @Bind(R.id.onlineNowLayout) LinearLayout onlineNowLayout;
  @Bind(R.id.markerTitleTV) TextView markerTitleTV;
  @Bind(R.id.linearLayout) LinearLayout linearLayout;
  @Inject SharedPreferences prefs;
  private Context context;
  private GetNearbyClinicsResponseDatum clinic;

  public CustomInfoWindowAdapter(GMFitApplication application, Context context,
      GetNearbyClinicsResponseDatum clinic) {
    this.context = context;
    this.clinic = clinic;

    application.getAppComponent().inject(this);
  }

  @Override public View getInfoWindow(Marker marker) {
    return prepareInfoView(marker);
  }

  @Override public View getInfoContents(Marker marker) {
    return prepareInfoView(marker);
  }

  private View prepareInfoView(Marker marker) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View infoView = inflater.inflate(R.layout.map_infowindow_dialog, null, false);

    ButterKnife.bind(this, infoView);

    markerTitleTV.setText(marker.getTitle());

    if (!prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
      linearLayout.setVisibility(View.VISIBLE);

      switch (marker.getSnippet()) {
        case "":
          return null;
        case "N":
          open247Layout.setVisibility(View.INVISIBLE);
          onlineNowLayout.setVisibility(View.INVISIBLE);
          break;
        case "O":
          open247Layout.setVisibility(View.INVISIBLE);
          withinNetworkLayout.setVisibility(View.INVISIBLE);
          break;
        case "247":
          onlineNowLayout.setVisibility(View.INVISIBLE);
          withinNetworkLayout.setVisibility(View.INVISIBLE);
          break;
        case "NO":
          open247Layout.setVisibility(View.INVISIBLE);
          break;
        case "O247":
          withinNetworkLayout.setVisibility(View.INVISIBLE);
          break;
        case "N247":
          onlineNowLayout.setVisibility(View.INVISIBLE);
          break;
      }
    } else {
      linearLayout.setVisibility(View.INVISIBLE);
    }

    return infoView;
  }
}
