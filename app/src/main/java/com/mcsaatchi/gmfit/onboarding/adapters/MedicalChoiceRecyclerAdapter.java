package com.mcsaatchi.gmfit.onboarding.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.onboarding.models.MedicalCondition;
import java.util.ArrayList;

public class MedicalChoiceRecyclerAdapter
    extends RecyclerView.Adapter<MedicalChoiceRecyclerAdapter.RecyclerViewHolder> {
  private RecyclerView RV;
  private ArrayList<MedicalCondition> conditions;

  public MedicalChoiceRecyclerAdapter(RecyclerView RV, ArrayList<MedicalCondition> conditions) {
    this.RV = RV;
    this.conditions = conditions;
  }

  @Override public int getItemCount() {
    return conditions.size();
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_medical_condition_choice, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    holder.medicalCheckbox.setText(conditions.get(position).getMedicalCondition());

    if (conditions.get(position).isSelected()) {
      holder.medicalCheckbox.setChecked(true);
    }

    holder.medicalCheckbox.setOnCheckedChangeListener((compoundButton, selected) -> {
      if (compoundButton.getText().equals("None")) {
        for (int i = 0; i < conditions.size(); i++) {
          conditions.get(i).setSelected(false);

          if (conditions.get(i).getMedicalCondition().equals("None")) {
            conditions.get(i).setSelected(true);
          }
        }


        //new Handler().post(this::notifyDataSetChanged);
      } else {
        conditions.get(position).setSelected(selected);
      }
    });
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder {
    CheckBox medicalCheckbox;

    RecyclerViewHolder(View itemView) {
      super(itemView);

      medicalCheckbox = (CheckBox) itemView.findViewById(R.id.medicalCheckbox);
    }
  }
}