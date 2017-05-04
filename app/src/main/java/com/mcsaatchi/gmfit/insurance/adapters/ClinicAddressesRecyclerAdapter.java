package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.insurance.activities.directory.ClinicDetailsActivity;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang3.text.WordUtils;

public class ClinicAddressesRecyclerAdapter extends RecyclerView.Adapter {
  @Inject SharedPreferences prefs;
  private List<GetNearbyClinicsResponseDatum> clinicsList;
  private Context context;

  public ClinicAddressesRecyclerAdapter(GMFitApplication application, Context context,
      List<GetNearbyClinicsResponseDatum> clinicsList) {
    this.context = context;
    this.clinicsList = clinicsList;

    application.getAppComponent().inject(this);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_clinic_addresses, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.bind(clinicsList.get(position));
  }

  @Override public int getItemCount() {
    return clinicsList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView clinicNameTV, clinicAddressTV;
    private LinearLayout parentLayout;
    private ImageView withinNetworkIV, open247IV, onlineNowIV;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
      clinicNameTV = (TextView) itemView.findViewById(R.id.clinicNameTV);
      clinicAddressTV = (TextView) itemView.findViewById(R.id.clinicAddressTV);
      withinNetworkIV = (ImageView) itemView.findViewById(R.id.withinNetworkIV);
      open247IV = (ImageView) itemView.findViewById(R.id.open247IV);
      onlineNowIV = (ImageView) itemView.findViewById(R.id.onlineNowIV);
    }

    public void bind(GetNearbyClinicsResponseDatum clinic) {
      clinicNameTV.setText(WordUtils.capitalizeFully(clinic.getName()));
      clinicAddressTV.setText(clinic.getAddress());

      if (!prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
        if (clinic.getOnline() != null) {
          if (clinic.getOnline()) {
            onlineNowIV.setVisibility(View.VISIBLE);
          }
        }

        if (clinic.getPartOfNetwork() != null) {
          if (clinic.getPartOfNetwork()) {
            withinNetworkIV.setVisibility(View.VISIBLE);
          }
        }

        if (clinic.getTwentyfourseven() != null) {
          if (clinic.getTwentyfourseven()) {
            open247IV.setVisibility(View.VISIBLE);
          }
        }
      }

      parentLayout.setOnClickListener(view -> {
        Intent intent = new Intent(context, ClinicDetailsActivity.class);
        intent.putExtra("CLINIC_OBJECT", clinicsList.get(getAdapterPosition()));
        context.startActivity(intent);
      });
    }
  }
}
