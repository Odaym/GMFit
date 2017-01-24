package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.text.WordUtils;

public class ChronicInactiveStatusAdapter extends RecyclerView.Adapter {
  private List<TreatmentsModel> treatmentsList;
  private Context context;

  public ChronicInactiveStatusAdapter(Context context, List<TreatmentsModel> treatmentsList) {
    this.context = context;
    this.treatmentsList = treatmentsList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_chronic_treatment_inactive, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.bind(treatmentsList.get(position));
  }

  @Override public int getItemCount() {
    return treatmentsList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private RelativeLayout parentLayout;
    private TextView treatmentNameTV, treatmentDescriptionTV, treatmentStatusTV;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
      treatmentNameTV = (TextView) itemView.findViewById(R.id.treatmentNameTV);
      treatmentDescriptionTV = (TextView) itemView.findViewById(R.id.treatmentDescriptionTV);
      treatmentStatusTV = (TextView) itemView.findViewById(R.id.treatmentStatusTV);
    }

    public void bind(TreatmentsModel treatment) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

      treatmentNameTV.setText(WordUtils.capitalizeFully(treatment.getName()));
      treatmentStatusTV.setText(treatment.getStatus());

      switch (treatment.getStatus()) {
        case "Partially Approved":
        case "Approved":
          treatmentStatusTV.setTextColor(Color.GREEN);
          break;
        case "Processing":
          treatmentStatusTV.setTextColor(context.getColor(R.color.nutrition_meal_bars_orange));
          break;
      }

      if (treatment.getFromDate() != null && treatment.getToDate() != null) {
        treatmentDescriptionTV.setText(
            dateFormat.format(treatment.getFromDate().getTime()) + " - " + dateFormat.format(
                treatment.getToDate().getTime()));
      } else {
        treatmentDescriptionTV.setText("Start and end dates not available yet");
      }

      parentLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          //Intent intent = new Intent(context, AddExistingMedicationActivity.class);
          //intent.putExtra(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, true);
          //intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM, medicationItem);
          //context.startActivity(intent);
        }
      });
    }
  }
}
