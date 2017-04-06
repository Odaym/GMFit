package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentDetailsResponseMedicationItem;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import java.util.List;

public class ChronicDetailsActivity extends BaseActivity
    implements ChronicDetailsActivityPresenter.ChronicDetailsActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicalRemindersRecyclerView) RecyclerView medicalRemindersRecyclerView;
  @Bind(R.id.startDateTV) TextView startDateTV;
  @Bind(R.id.endDateTV) TextView endDateTV;
  @Bind(R.id.statusValueTV) TextView statusValueTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status);

    ButterKnife.bind(this);

    ChronicDetailsActivityPresenter presenter =
        new ChronicDetailsActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      ChronicTreatmentListInnerData chronicTreatment =
          (ChronicTreatmentListInnerData) getIntent().getExtras().get("CHRONIC_OBJECT");
      setupToolbar(getClass().getSimpleName(), toolbar, chronicTreatment.getName(), true);

      statusValueTV.setTextColor(
          getResources().getColor(Helpers.determineStatusColor(chronicTreatment.getStatus())));
      statusValueTV.setText(chronicTreatment.getStatus());

      if (chronicTreatment.getStartDate() != null && chronicTreatment.getEndDate() != null) {
        startDateTV.setText(chronicTreatment.getStartDate().split("T")[0]);
        endDateTV.setText(chronicTreatment.getEndDate().split("T")[0]);
      }

      presenter.getChronicTreatmentDetails(
          prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
          chronicTreatment.getRequestNbr());
    } else {
      setupToolbar(getClass().getSimpleName(), toolbar,
          getResources().getString(R.string.treatment_status_section_title), true);
    }
  }

  @Override public void displayChronicTreatmentDetails(
      List<ChronicTreatmentDetailsResponseMedicationItem> treatmentItems) {
    MedicalInformationAdapter adapter = new MedicalInformationAdapter(treatmentItems,
        (medicalInformationModel, index) -> Toast.makeText(ChronicDetailsActivity.this,
            "Medical Information list item!", Toast.LENGTH_SHORT).show());
    medicalRemindersRecyclerView.setLayoutManager(
        new LinearLayoutManager(ChronicDetailsActivity.this));
    medicalRemindersRecyclerView.setNestedScrollingEnabled(false);
    medicalRemindersRecyclerView.addItemDecoration(
        new SimpleDividerItemDecoration(ChronicDetailsActivity.this));
    medicalRemindersRecyclerView.setAdapter(adapter);
  }
}
