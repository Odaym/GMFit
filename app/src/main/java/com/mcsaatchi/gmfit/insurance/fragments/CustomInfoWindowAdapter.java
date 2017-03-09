package com.mcsaatchi.gmfit.insurance.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.mcsaatchi.gmfit.R;
import timber.log.Timber;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

  @Bind(R.id.open247Layout) LinearLayout open247Layout;
  @Bind(R.id.withinNetworkLayout) LinearLayout withinNetworkLayout;
  @Bind(R.id.onlineNowLayout) LinearLayout onlineNowLayout;
  @Bind(R.id.markerTitleTV) TextView markerTitleTV;

  private Context context;

  public CustomInfoWindowAdapter(Context context) {
    this.context = context;
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

    Timber.d(marker.getTitle() + " has snippet : " + marker.getSnippet());

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

    return infoView;
  }
}
