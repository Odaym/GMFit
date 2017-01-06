package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicationItemCreatedEvent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.health.activities.AddExistingMedicationActivity;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

public class MedicationsRecyclerAdapter extends RecyclerView.Adapter {
  private List<Medication> medicationsList;
  private Context context;
  private RuntimeExceptionDao<Medication, Integer> medicationDAO;

  public MedicationsRecyclerAdapter(Context context, List<Medication> medicationsList,
      RuntimeExceptionDao<Medication, Integer> medicationDAO) {
    this.context = context;
    this.medicationsList = medicationsList;
    this.medicationDAO = medicationDAO;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_medication, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    if (medicationsList != null && 0 <= position && position < medicationsList.size()) {
      final Medication medicationItem = medicationsList.get(position);

      // Bind your data here
      holder.bind(medicationItem);
    }
  }

  @Override public int getItemCount() {
    return medicationsList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private View deleteLayout, deactivate_layout;
    private RelativeLayout parentLayout;
    private TextView medicineNameTV, medicineDescriptionTV, medicineIntakeDetailsTV;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
      deleteLayout = itemView.findViewById(R.id.delete_layout);
      deactivate_layout = itemView.findViewById(R.id.deactivate_layout);
      medicineNameTV = (TextView) itemView.findViewById(R.id.medicineNameTV);
      medicineDescriptionTV = (TextView) itemView.findViewById(R.id.medicineDescriptionTV);
      medicineIntakeDetailsTV = (TextView) itemView.findViewById(R.id.medicineIntakeDetailsTV);
    }

    public void bind(final Medication medicationItem) {
      deleteLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          medicationDAO.delete(medicationsList.get(getAdapterPosition()));
          medicationsList.remove(getAdapterPosition());
          notifyItemRemoved(getAdapterPosition());

          EventBusSingleton.getInstance().post(new MedicationItemCreatedEvent());
        }
      });

      //deactivate_layout.setOnClickListener(new View.OnClickListener() {
      //  @Override public void onClick(View view) {
      //    Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();
      //  }
      //});

      medicineNameTV.setText(WordUtils.capitalizeFully(medicationItem.getName()));
      medicineDescriptionTV.setText(medicationItem.getDescription());
      medicineIntakeDetailsTV.setText(medicationItem.getDosage()
          + " - "
          + medicationItem.getWhen()
          + " - "
          + medicationItem.getFrequency());

      parentLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          Intent intent = new Intent(context, AddExistingMedicationActivity.class);
          intent.putExtra(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, true);
          intent.putExtra(Constants.EXTRAS_MEDICATION_ITEM,
              medicationItem);
          context.startActivity(intent);
        }
      });
    }
  }
}
