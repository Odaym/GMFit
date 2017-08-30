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
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicDeletionActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicDetailsActivity;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

public class ChronicStatusAdapter extends RecyclerView.Adapter {
  private List<ChronicTreatmentListInnerData> treatmentsList;
  private Context context;

  public ChronicStatusAdapter(Context context, List<ChronicTreatmentListInnerData> treatmentsList) {
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
      treatmentNameTV = itemView.findViewById(R.id.treatmentNameTV);
      treatmentDescriptionTV = itemView.findViewById(R.id.treatmentDescriptionTV);
      treatmentStatusTV = itemView.findViewById(R.id.treatmentStatusTV);
    }

    public void bind(ChronicTreatmentListInnerData chronicTreatment) {
      deleteLayout.setOnClickListener(v -> {
        Intent intent = new Intent(context, ChronicDeletionActivity.class);
        intent.putExtra("CHRONIC_OBJECT", treatmentsList.get(getAdapterPosition()));
        context.startActivity(intent);

        treatmentsList.remove(getAdapterPosition());
        notifyItemRemoved(getAdapterPosition());
      });

      treatmentNameTV.setText(WordUtils.capitalizeFully(chronicTreatment.getName()));
      treatmentStatusTV.setTextColor(context.getResources()
          .getColor(Helpers.determineStatusColor(chronicTreatment.getStatus())));
      treatmentStatusTV.setText(chronicTreatment.getStatus());

      if (chronicTreatment.getStartDate() != null && chronicTreatment.getEndDate() != null) {
        treatmentDescriptionTV.setText(
            chronicTreatment.getStartDate().split("T")[0] + " - " + chronicTreatment.getEndDate()
                .split("T")[0]);
      } else {
        treatmentDescriptionTV.setText("Start and end dates not available yet");
      }

      parentLayout.setOnClickListener(view -> {
        Intent intent = new Intent(context, ChronicDetailsActivity.class);
        intent.putExtra("CHRONIC_OBJECT", treatmentsList.get(getAdapterPosition()));
        //intent.putExtra(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, true);
        //intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM, medicationItem);
        context.startActivity(intent);
      });
    }
  }
}
