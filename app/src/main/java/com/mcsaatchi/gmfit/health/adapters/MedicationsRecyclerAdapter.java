package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.List;

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
    private SwipeRevealLayout swipeLayout;
    private View deleteLayout, deactivate_layout;
    private TextView medicineNameTV, medicineDescriptionTV, medicineIntakeDetailsTV;

    public ViewHolder(View itemView) {
      super(itemView);
      swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
      deleteLayout = itemView.findViewById(R.id.delete_layout);
      //deactivate_layout = itemView.findViewById(R.id.deactivate_layout);
      medicineNameTV = (TextView) itemView.findViewById(R.id.medicineNameTV);
      medicineDescriptionTV = (TextView) itemView.findViewById(R.id.medicineDescriptionTV);
      medicineIntakeDetailsTV = (TextView) itemView.findViewById(R.id.medicineIntakeDetailsTV);
    }

    public void bind(Medication medicationItem) {
      deleteLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          medicationDAO.delete(medicationsList.get(getAdapterPosition()));
          medicationsList.remove(getAdapterPosition());
          notifyItemRemoved(getAdapterPosition());
        }
      });

      //deactivate_layout.setOnClickListener(new View.OnClickListener() {
      //  @Override public void onClick(View view) {
      //    Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();
      //  }
      //});

      medicineNameTV.setText(medicationItem.getName());
      medicineDescriptionTV.setText(medicationItem.getDescription());
      medicineIntakeDetailsTV.setText(medicationItem.getDosage()
          + " - "
          + medicationItem.getWhen()
          + " - "
          + medicationItem.getFrequency());
    }
  }
}
