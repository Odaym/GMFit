package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.health.activities.AddExistingMedicationActivity;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.List;

public class SearchMedicationsRecyclerAdapter extends RecyclerView.Adapter {
  private List<Medication> medicationsList;
  private Context context;

  public SearchMedicationsRecyclerAdapter(Context context, List<Medication> medicationsList) {
    this.context = context;
    this.medicationsList = medicationsList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_search_medication, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    if (medicationsList != null && 0 <= position && position < medicationsList.size()) {
      final Medication medicationItem = medicationsList.get(position);

      holder.bind(medicationItem);
    }
  }

  @Override public int getItemCount() {
    return medicationsList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView medicineNameTV, medicineDescriptionTV;

    public ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, AddExistingMedicationActivity.class);
        intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM,
            medicationsList.get(getAdapterPosition()));
        context.startActivity(intent);
      });

      medicineNameTV = itemView.findViewById(R.id.medicineNameTV);
      medicineDescriptionTV = itemView.findViewById(R.id.medicineDescriptionTV);
    }

    public void bind(Medication medicationItem) {
      medicineNameTV.setText(medicationItem.getName());
      medicineDescriptionTV.setText(medicationItem.getDescription());
    }
  }
}
