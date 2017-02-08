package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import com.mcsaatchi.gmfit.insurance.models.MedicalInformationModel;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChornicPrescriptionStatusDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicalRemindersRecyclerView) RecyclerView medicalRemindersRecyclerView;
  @Bind(R.id.startDateTV) TextView startDateTV;
  @Bind(R.id.endDateTV) TextView endDateTV;
  @Bind(R.id.statusValueTV) TextView statusValueTV;

  private MedicalInformationAdapter adapter;
  private TreatmentsModel treatmentsModel;

  private Calendar calendar = Calendar.getInstance();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status);

    ButterKnife.bind(this);

    updateLabel(startDateTV);
    updateLabel(endDateTV);

    startDateTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        new DatePickerDialog(ChornicPrescriptionStatusDetailsActivity.this,
            new DatePickerDialog.OnDateSetListener() {
              @Override public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(startDateTV);
              }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show();
      }
    });

    endDateTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        new DatePickerDialog(ChornicPrescriptionStatusDetailsActivity.this,
            new DatePickerDialog.OnDateSetListener() {
              @Override public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(endDateTV);
              }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show();
      }
    });

    if (getIntent().getExtras() != null) {
      treatmentsModel = (TreatmentsModel) getIntent().getExtras().get("CHRONIC_OBJECT");
      setupToolbar(getClass().getSimpleName(), toolbar, treatmentsModel.getName(), true);
      statusValueTV.setText(treatmentsModel.getStatus());
    } else {
      setupToolbar(getClass().getSimpleName(), toolbar,
          getResources().getString(R.string.treatment_status_section_title), true);
    }

    List<MedicalInformationModel> medicines = new ArrayList<>();
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));
    medicines.add(new MedicalInformationModel("Panadol Extra Tab 500mg", "Approved", "2 tablets",
        "3 times daily", "15 days"));

    adapter = new MedicalInformationAdapter(medicines, null);
    medicalRemindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    medicalRemindersRecyclerView.setNestedScrollingEnabled(false);
    medicalRemindersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    medicalRemindersRecyclerView.setAdapter(adapter);
  }

  private void updateLabel(TextView tv) {
    String myFormat = "dd MMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    tv.setText(sdf.format(calendar.getTime()));
  }
}
