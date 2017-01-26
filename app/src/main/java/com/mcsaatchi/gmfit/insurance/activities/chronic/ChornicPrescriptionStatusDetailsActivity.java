package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import com.mcsaatchi.gmfit.insurance.models.MedicalInformationModel;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.util.ArrayList;
import java.util.List;

public class ChornicPrescriptionStatusDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicalRemindersRecyclerView) RecyclerView medicalRemindersRecyclerView;

  private MedicalInformationAdapter adapter;
  private TreatmentsModel treatmentsModel;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      treatmentsModel = (TreatmentsModel) getIntent().getExtras().get("CHRONIC_OBJECT");
      setupToolbar(toolbar, treatmentsModel.getName(), true);
    } else {
      setupToolbar(toolbar, getResources().getString(R.string.treatment_status_section_title),
          true);
    }

    List<MedicalInformationModel> medicines = new ArrayList<>();
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));

    adapter =
        new MedicalInformationAdapter(medicines, new MedicalInformationAdapter.OnClickListener() {
          @Override
          public void onClick(MedicalInformationModel medicalInformationModel, int index) {

          }
        });

    medicalRemindersRecyclerView.setNestedScrollingEnabled(false);
    medicalRemindersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    medicalRemindersRecyclerView.setAdapter(adapter);
  }
}
