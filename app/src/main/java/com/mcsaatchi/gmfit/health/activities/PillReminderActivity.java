package com.mcsaatchi.gmfit.health.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.MedicationsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.ArrayList;
import java.util.List;

public class PillReminderActivity extends BaseActivity {

  @Bind(R.id.numberOfMedicationsTV) TextView numberOfMedicationsTV;
  @Bind(R.id.medicationsRecyclerView) RecyclerView medicationsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private List<Medication> medicationsList = new ArrayList<>();

  private RuntimeExceptionDao<Medication, Integer> medicationDAO;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_pill_reminder);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.pill_reminder_activity_title), true);

    //medicationDAO = dbHelper.getMedicationDAO();
    //
    //setupMedicationsList(medicationDAO.queryForAll());

    medicationsList.add(
        new Medication("Augmentin", "475MG SYRP Syrp 475MG 70 ml", "10 ml", "Everyday", 3));
    medicationsList.add(
        new Medication("Vestibulum Elit", "100MG SYRP Syrp 100MG 9 ml", "900 ml", "2 days/week",
            3));
    medicationsList.add(
        new Medication("Sed Interdum", "512MG SYRP Syrp 519MG 90 ml", "100 ml", "Everyday", 1));
    medicationsList.add(
        new Medication("Vulputate Ultrices", "123MG SYRP Syrp 658MG 190 ml", "50 ml", "4 days/week",
            5));

    if (medicationsList.size() == 1) {
      numberOfMedicationsTV.setText(medicationsList.size() + " medication");
    } else {
      numberOfMedicationsTV.setText(medicationsList.size() + " medications");
    }

    setupMedicationsList(medicationsList);
  }

  private void setupMedicationsList(List<Medication> medicationList) {
    medicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    MedicationsRecyclerAdapter medicationsRecyclerAdapter =
        new MedicationsRecyclerAdapter(this, medicationList, medicationDAO);
    medicationsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    medicationsRecyclerView.setAdapter(medicationsRecyclerAdapter);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.pill_reminder_medications, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.addMedicationItem:
        break;
    }

    return super.onOptionsItemSelected(item);
  }
}
