package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChornicPrescriptionStatusDetailsActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.RequestChronicDeletionActivity;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

public class ChronicStatusAdapter extends RecyclerView.Adapter {
  private List<TreatmentsModel> treatmentsList;
  private Context context;

  public ChronicStatusAdapter(Context context, List<TreatmentsModel> treatmentsList) {
    this.context = context;
    this.treatmentsList = treatmentsList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_chronic_treatment, parent, false);

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
    private View deleteLayout;
    private RelativeLayout parentLayout;
    private TextView treatmentNameTV, treatmentDescriptionTV, treatmentStatusTV;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
      deleteLayout = itemView.findViewById(R.id.delete_layout);
      treatmentNameTV = (TextView) itemView.findViewById(R.id.treatmentNameTV);
      treatmentDescriptionTV = (TextView) itemView.findViewById(R.id.treatmentDescriptionTV);
      treatmentStatusTV = (TextView) itemView.findViewById(R.id.treatmentStatusTV);
    }

    public void bind(TreatmentsModel treatment) {
      deleteLayout.setOnClickListener(v -> {
        Intent intent = new Intent(context, RequestChronicDeletionActivity.class);
        intent.putExtra("CHRONIC_OBJECT", treatmentsList.get(getAdapterPosition()));
        context.startActivity(intent);

        treatmentsList.remove(getAdapterPosition());
        notifyItemRemoved(getAdapterPosition());
      });

      treatmentNameTV.setText(WordUtils.capitalizeFully(treatment.getName()));
      treatmentStatusTV.setTextColor(Helpers.determineStatusColor(treatment.getStatus()));
      treatmentStatusTV.setText(treatment.getStatus());

      if (treatment.getFromDate() != null && treatment.getToDate() != null) {
        treatmentDescriptionTV.setText(
            treatment.getFromDate().split("T")[0] + " - " + treatment.getToDate().split("T")[0]);
      } else {
        treatmentDescriptionTV.setText("Start and end dates not available yet");
      }

      parentLayout.setOnClickListener(view -> {
        Intent intent = new Intent(context, ChornicPrescriptionStatusDetailsActivity.class);
        intent.putExtra("CHRONIC_OBJECT", treatmentsList.get(getAdapterPosition()));
        //intent.putExtra(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, true);
        //intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM, medicationItem);
        context.startActivity(intent);
      });
    }
  }
}
