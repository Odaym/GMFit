package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentDetailsResponseMedicationItem;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.List;

public class MedicalInformationAdapter
    extends RecyclerView.Adapter<MedicalInformationAdapter.ViewHolder> {
  private List<ChronicTreatmentDetailsResponseMedicationItem> medicines = new ArrayList<>();
  private MedicalInformationAdapter.OnClickListener onClickListener;

  public MedicalInformationAdapter(List<ChronicTreatmentDetailsResponseMedicationItem> medicines,
      MedicalInformationAdapter.OnClickListener onClickListener) {
    this.medicines = medicines;
    this.onClickListener = onClickListener;
  }

  @Override
  public MedicalInformationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    v = inflater.inflate(R.layout.list_item_medical_information_status, parent, false);
    return new MedicalInformationAdapter.ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(MedicalInformationAdapter.ViewHolder holder, int position) {
    ChronicTreatmentDetailsResponseMedicationItem medicine = medicines.get(position);
    holder.populate(medicine);
    holder.addListener(medicine, position, onClickListener);
  }

  @Override public int getItemCount() {
    return medicines.size();
  }

  public interface OnClickListener {
    void onClick(ChronicTreatmentDetailsResponseMedicationItem medicalInformationModel, int index);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView medicineNameTv, tabletCountTv, statusTv, frequencyTv, durationTv;
    View container;

    public ViewHolder(View v) {
      super(v);
      medicineNameTv = v.findViewById(R.id.medicineName);
      tabletCountTv = v.findViewById(R.id.tabletCount);
      statusTv = v.findViewById(R.id.status);
      frequencyTv = v.findViewById(R.id.frequency);
      durationTv = v.findViewById(R.id.duration);
      container = v.findViewById(R.id.container);
    }

    void populate(ChronicTreatmentDetailsResponseMedicationItem medicine) {
      medicineNameTv.setText(medicine.getItemDesc());
      tabletCountTv.setText(medicine.getItemDosage());
      statusTv.setTextColor(Helpers.determineStatusColor(medicine.getStatus()));
      statusTv.setText(medicine.getStatus());
      frequencyTv.setText(medicine.getPosology());
    }

    void addListener(final ChronicTreatmentDetailsResponseMedicationItem medicine,
        final int position, final MedicalInformationAdapter.OnClickListener onClickListener) {
      container.setOnClickListener(view -> onClickListener.onClick(medicine, position));
    }
  }
}
