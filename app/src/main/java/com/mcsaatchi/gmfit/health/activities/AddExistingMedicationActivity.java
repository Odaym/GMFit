package com.mcsaatchi.gmfit.health.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicationItemCreatedEvent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.health.models.DayChoice;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.ArrayList;

public class AddExistingMedicationActivity extends BaseActivity {

  public static final int REMINDER_DAYS_CHOSEN = 231;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicineNameET) EditText medicineNameET;
  @Bind(R.id.unitMeasurementTV) TextView unitMeasurementTV;
  @Bind(R.id.daysOfWeekLayout) LinearLayout daysOfWeekLayout;
  @Bind(R.id.daysOfWeekTV) TextView daysOfWeekTV;
  @Bind(R.id.frequencyET) EditText frequencyET;
  @Bind(R.id.treatmentDurationET) EditText treatmentDurationET;
  @Bind(R.id.yourNotesET) EditText yourNotesET;
  @Bind(R.id.unitsET) EditText unitsET;
  @Bind(R.id.addMedicationBTN) Button addMedicationBTN;
  private RuntimeExceptionDao<Medication, Integer> medicationDAO;
  private boolean editPurpose = false;
  private Medication medicationItem;
  private ArrayList<DayChoice> daysSelected = null;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_existing_medication);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.add_medication), true);

    medicationDAO = dbHelper.getMedicationDAO();

    if (getIntent().getExtras() != null) {
      medicationItem = (Medication) getIntent().getExtras().get(Constants.EXTRAS_MEDICATION_ITEM);

      editPurpose = getIntent().getExtras()
          .getBoolean(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, false);

      if (medicationItem != null) {
        medicineNameET.setText(medicationItem.getName());
        medicineNameET.setSelection(medicationItem.getName().length());

        unitsET.setText(String.valueOf(medicationItem.getUnits()));
        unitMeasurementTV.setText(medicationItem.getUnitForm());
        frequencyET.setText(String.valueOf(medicationItem.getFrequency()));
        daysOfWeekTV.setText(medicationItem.getWhen());
        treatmentDurationET.setText(String.valueOf(medicationItem.getTreatmentDuration()));
        yourNotesET.setText(medicationItem.getRemarks());
      }

      if (editPurpose) {
        addMedicationBTN.setText(getString(R.string.edit_medication_button));
      }
    }
  }

  @OnClick(R.id.daysOfWeekLayout) void openDaysChooser() {
    Intent intent = new Intent(AddExistingMedicationActivity.this, DaysChoiceListActivity.class);
    if (daysSelected != null) intent.putParcelableArrayListExtra("REMINDER_DAYS", daysSelected);
    startActivityForResult(intent, REMINDER_DAYS_CHOSEN);
  }

  @OnClick(R.id.addMedicationBTN) void addMedication() {
    if (medicineNameET.getText().toString().isEmpty() ||
        frequencyET.getText().toString().isEmpty() ||
        daysOfWeekTV.getText().toString().isEmpty() ||
        treatmentDurationET.getText().toString().isEmpty() ||
        unitMeasurementTV.getText().toString().isEmpty()) {
      Toast.makeText(this, R.string.fill_in_below_fields_hint, Toast.LENGTH_LONG).show();
    } else {
      Medication medication = new Medication();
      medication.setName(medicineNameET.getText().toString());
      medication.setFrequency(Integer.parseInt(frequencyET.getText().toString()));
      medication.setRemarks(yourNotesET.getText().toString());
      medication.setUnits(Integer.parseInt(unitsET.getText().toString()));
      medication.setUnitForm(unitMeasurementTV.getText().toString());
      medication.setWhen(daysOfWeekTV.getText().toString());
      medication.setDosage("0.5 " + medication.getUnitForm());
      medication.setTreatmentDuration(Integer.parseInt(treatmentDurationET.getText().toString()));
      medication.setDescription(
          medication.getUnits() + " " + medication.getUnitForm() + " " + medication.getUnitForm()
              .toUpperCase() + " " + medication.getUnits() + " " + medication.getUnitForm());

      if (editPurpose) {
        medicationDAO.update(medication);
      } else {
        medicationDAO.create(medication);
      }

      EventBusSingleton.getInstance().post(new MedicationItemCreatedEvent());

      finish();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REMINDER_DAYS_CHOSEN) {
      ArrayList<DayChoice> dayChoices = data.getExtras().getParcelableArrayList("REMINDER_DAYS");
      daysSelected = dayChoices;

      String daysOfWeekResult = "";

      if (dayChoices != null) {
        for (DayChoice day : dayChoices) {
          if (day.isDaySelected()) {
            daysOfWeekResult += day.getDayName().substring(0, 3) + ", ";
          }
        }
      }

      daysOfWeekTV.setText(daysOfWeekResult.replaceAll(", $", ""));
    }
  }
}
