package com.mcsaatchi.gmfit.health.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.mcsaatchi.gmfit.common.classes.AlarmReceiver;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.MedicationRemindersRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.DayChoice;
import com.mcsaatchi.gmfit.health.models.Medication;
import com.mcsaatchi.gmfit.health.models.MedicationReminder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class AddExistingMedicationActivity extends BaseActivity {

  public static final int REMINDER_DAYS_CHOSEN = 231;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicineNameET) EditText medicineNameET;
  @Bind(R.id.unitMeasurementTV) TextView unitMeasurementTV;
  @Bind(R.id.daysOfWeekLayout) LinearLayout daysOfWeekLayout;
  @Bind(R.id.daysOfWeekTV) TextView daysOfWeekTV;
  @Bind(R.id.frequencyET) EditText frequencyET;
  @Bind(R.id.treatmentDurationET) EditText treatmentDurationET;
  @Bind(R.id.remindersRecyclerView) RecyclerView remindersRecyclerView;
  @Bind(R.id.yourNotesET) EditText yourNotesET;
  @Bind(R.id.unitsET) EditText unitsET;
  @Bind(R.id.addMedicationBTN) Button addMedicationBTN;
  @Bind(R.id.enableRemindersSwitch) Switch enableRemindersSwitch;

  private RuntimeExceptionDao<Medication, Integer> medicationDAO;
  private ArrayList<MedicationReminder> medicationReminders;
  private Medication medicationItem;
  private boolean editPurpose = false;
  private boolean areRemindersEnabled = false;
  private ArrayList<DayChoice> daysSelected = null;

  private int[] daysOfWeekArray;

  private Map<String, Integer> daysOfweekMap = new HashMap<String, Integer>() {{
    put("Monday", 1);
    put("Tuesday", 2);
    put("Wednesday", 3);
    put("Thursday", 4);
    put("Friday", 5);
    put("Saturday", 6);
    put("Sunday", 7);
  }};

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_existing_medication);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.add_medication), true);

    medicationDAO = dbHelper.getMedicationDAO();

    if (getIntent().getExtras() != null) {
      medicationItem =
          (Medication) getIntent().getExtras().get(Constants.EXTRAS_MEDICATION_REMINDER_ITEM);

      editPurpose = getIntent().getExtras()
          .getBoolean(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, false);

      if (medicationItem != null) {
        daysSelected = medicationItem.getWhen();

        daysOfWeekArray = new int[daysSelected.size()];
        for (int i = 0; i < daysSelected.size(); i++) {
          if (daysSelected.get(i).isDaySelected()) {
            daysOfWeekArray[i] = daysOfweekMap.get(daysSelected.get(i).getDayName());
          }
        }

        //
        //for (Medication med : dbHelper.getMedicationDAO().queryForAll()) {
        //  Timber.d("MedicationReminders SIZE : " + med.getMedicationReminders().size());
        //
        //  Iterator<MedicationReminder> itr = med.getMedicationReminders().iterator();
        //
        //  while (itr.hasNext()) {
        //    MedicationReminder RM = itr.next();
        //    Timber.d("Reminder #"
        //        + RM.getId()
        //        + " inside "
        //        + med.getName()
        //        + " has time : "
        //        + RM.getHour()
        //        + ":"
        //        + RM.getMinute()
        //        + " "
        //        + RM.getAM_PM());
        //  }
        //}

        medicineNameET.setText(medicationItem.getName());
        medicineNameET.setSelection(medicationItem.getName().length());

        unitsET.setText(String.valueOf(medicationItem.getUnits()));
        unitMeasurementTV.setText(medicationItem.getUnitForm());
        frequencyET.setText(String.valueOf(medicationItem.getFrequency()));
        daysOfWeekTV.setText(medicationItem.getWhenString());
        treatmentDurationET.setText(String.valueOf(medicationItem.getTreatmentDuration()));
        yourNotesET.setText(medicationItem.getRemarks());
      }

      if (editPurpose) {
        addMedicationBTN.setText(getString(R.string.edit_medication_button));
      }
    }

    enableRemindersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
          prepareRemindersRecyclerView(Integer.parseInt(frequencyET.getText().toString()));
          remindersRecyclerView.setVisibility(View.VISIBLE);
        } else {
          remindersRecyclerView.setVisibility(View.GONE);
        }

        areRemindersEnabled = !areRemindersEnabled;
      }
    });

    frequencyET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        if (!editable.toString().isEmpty() && areRemindersEnabled) {
          prepareRemindersRecyclerView(Integer.parseInt(editable.toString()));
        }
      }
    });
  }

  private void prepareRemindersRecyclerView(int frequencyNumber) {
    medicationReminders = new ArrayList<>(frequencyNumber);

    for (int ind = 0; ind < frequencyNumber; ind++) {
      medicationReminders.add(new MedicationReminder(9, 30, 0, "AM"));
    }

    setupRemindersRecyclerView(medicationReminders);
  }

  private void setupRemindersRecyclerView(ArrayList<MedicationReminder> medicationReminderTimes) {
    MedicationRemindersRecyclerAdapter medicationRemindersRecyclerAdapter =
        new MedicationRemindersRecyclerAdapter(AddExistingMedicationActivity.this,
            medicationReminderTimes);
    remindersRecyclerView.setLayoutManager(
        new LinearLayoutManager(AddExistingMedicationActivity.this));
    remindersRecyclerView.addItemDecoration(
        new SimpleDividerItemDecoration(AddExistingMedicationActivity.this));
    remindersRecyclerView.setAdapter(medicationRemindersRecyclerAdapter);
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
      if (!editPurpose) {
        Medication medication = new Medication();
        medication.setName(medicineNameET.getText().toString());
        medication.setRemarks(yourNotesET.getText().toString());
        medication.setUnits(Integer.parseInt(unitsET.getText().toString()));
        medication.setUnitForm(unitMeasurementTV.getText().toString());
        medication.setWhen(daysSelected);
        medication.setWhenString(daysOfWeekTV.getText().toString());
        medication.setDosage("0.5 " + medication.getUnitForm());
        medication.setDescription(
            medication.getUnits() + " " + medication.getUnitForm() + " " + medication.getUnitForm()
                .toUpperCase() + " " + medication.getUnits() + " " + medication.getUnitForm());

        medicationDAO.create(medication);

        if (areRemindersEnabled && medicationReminders != null) {
          assignForeignCollectionToParentObject(medication);
        }

        medicationDAO.update(medication);
      } else {
        medicationItem.setName(medicineNameET.getText().toString());
        medicationItem.setRemarks(yourNotesET.getText().toString());
        medicationItem.setUnits(Integer.parseInt(unitsET.getText().toString()));
        medicationItem.setUnitForm(unitMeasurementTV.getText().toString());
        medicationItem.setWhen(daysSelected);
        medicationItem.setWhenString(daysOfWeekTV.getText().toString());
        medicationItem.setDosage("0.5 " + medicationItem.getUnitForm());
        medicationItem.setRemindersEnabled(areRemindersEnabled);
        medicationItem.setDescription(medicationItem.getUnits()
            + " "
            + medicationItem.getUnitForm()
            + " "
            + medicationItem.getUnitForm().toUpperCase()
            + " "
            + medicationItem.getUnits()
            + " "
            + medicationItem.getUnitForm());

        medicationDAO.update(medicationItem);

        if (areRemindersEnabled && medicationReminders != null) {
          assignForeignCollectionToParentObject(medicationItem);
        }
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
        daysOfWeekArray = new int[dayChoices.size()];

        for (int i = 0; i < dayChoices.size(); i++) {
          if (dayChoices.get(i).isDaySelected()) {
            daysOfWeekArray[i] = daysOfweekMap.get(dayChoices.get(i).getDayName());
            daysOfWeekResult += dayChoices.get(i).getDayName().substring(0, 3) + ", ";
          }
        }
      }

      daysOfWeekTV.setText(daysOfWeekResult.replaceAll(", $", ""));
    }
  }

  public void setupMedicationReminders(MedicationReminder medReminder) {
    Timber.d(medReminder.toString());
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());

    for (Integer day : daysOfWeekArray) {
      if (day != 0) {
        calendar.set(Calendar.DAY_OF_WEEK, day);
      }
    }
    calendar.set(Calendar.HOUR_OF_DAY, medReminder.getHour());
    calendar.set(Calendar.MINUTE, medReminder.getMinute());
    calendar.set(Calendar.SECOND, medReminder.getSecond());
    calendar.set(Calendar.MILLISECOND, 0);

    Intent intent = new Intent(AddExistingMedicationActivity.this, AlarmReceiver.class);
    intent.putExtra(Constants.EXTRAS_ALARM_TYPE, "medications");
    intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM, (Parcelable) medReminder);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, medReminder.getId(), intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }

  private void assignForeignCollectionToParentObject(Medication medicationObject) {

    medicationDAO.assignEmptyForeignCollection(medicationObject, "medicationReminders");

    MedicationRemindersRecyclerAdapter adapter =
        (MedicationRemindersRecyclerAdapter) remindersRecyclerView.getAdapter();

    for (int i = 0; i < adapter.getItemCount(); i++) {
      MedicationReminder medReminder = adapter.getItem(i);
      medReminder.setMedication(medicationObject);
      medReminder.setDays_of_week(daysOfWeekArray);
      medicationObject.getMedicationReminders().add(medReminder);

      setupMedicationReminders(medReminder);
    }
  }
}
