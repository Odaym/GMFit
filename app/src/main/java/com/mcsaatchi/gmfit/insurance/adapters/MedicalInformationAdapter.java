package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.models.MedicalInformationModel;
import java.util.ArrayList;
import java.util.List;

public class MedicalInformationAdapter
    extends RecyclerView.Adapter<MedicalInformationAdapter.ViewHolder> {
  private List<MedicalInformationModel> medicines = new ArrayList<>();
  private MedicalInformationAdapter.OnClickListener onClickListener;

  public MedicalInformationAdapter(List<MedicalInformationModel> medicines,
      MedicalInformationAdapter.OnClickListener onClickListener) {
    this.medicines = medicines;
    this.onClickListener = onClickListener;
  }

  @Override
  public MedicalInformationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    v = inflater.inflate(R.layout.medical_information_status_item, parent, false);
    return new MedicalInformationAdapter.ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(MedicalInformationAdapter.ViewHolder holder, int position) {
    MedicalInformationModel medicine = medicines.get(position);
    holder.populate(medicine);
    holder.addListener(medicine, position, onClickListener);
  }

  @Override public int getItemCount() {
    return medicines.size();
  }

  public interface OnClickListener {
    void onClick(MedicalInformationModel medicalInformationModel, int index);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView medicineNameTv, tabletCountTv, statusTv, frequencyTv, durationTv;
    View container;

    public ViewHolder(View v) {
      super(v);
      medicineNameTv = (TextView) v.findViewById(R.id.medicineName);
      tabletCountTv = (TextView) v.findViewById(R.id.tabletCount);
      statusTv = (TextView) v.findViewById(R.id.status);
      frequencyTv = (TextView) v.findViewById(R.id.frequency);
      durationTv = (TextView) v.findViewById(R.id.duration);
      container = v.findViewById(R.id.container);
    }

    void populate(MedicalInformationModel medicine) {
      medicineNameTv.setText(medicine.getMedicineName());
      tabletCountTv.setText(medicine.getTabletCount());
      statusTv.setText(medicine.getStatus());
      frequencyTv.setText(medicine.getFrequency());
      durationTv.setText(medicine.getDuration());
    }

    void addListener(final MedicalInformationModel medicine, final int position,
        final MedicalInformationAdapter.OnClickListener onClickListener) {
      container.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onClickListener.onClick(medicine, position);
        }
      });
    }
  }
}
