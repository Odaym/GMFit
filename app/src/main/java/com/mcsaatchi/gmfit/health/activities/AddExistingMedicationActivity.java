package com.mcsaatchi.gmfit.health.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.health.models.Medication;
import timber.log.Timber;

public class AddExistingMedicationActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicineNameET) EditText medicineNameET;
  @Bind(R.id.posologyMeasurementTV) TextView posologyMeasurementTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_existing_medication);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.add_medication), true);

    if (getIntent().getExtras() != null) {
      Medication medicationItem =
          (Medication) getIntent().getExtras().get(Constants.EXTRAS_MEDICATION_ITEM);

      if (medicationItem != null) {
        medicineNameET.setText(medicationItem.getName());
        posologyMeasurementTV.setText(medicationItem.getUnitForm());
      }

      Timber.d("Medication name is : " + medicationItem.getName());
    }
  }
}
