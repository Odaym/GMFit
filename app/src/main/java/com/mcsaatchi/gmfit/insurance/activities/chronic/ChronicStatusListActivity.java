package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicInactiveStatusAdapter;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicStatusAdapter;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.util.ArrayList;
import java.util.Calendar;

public class ChronicStatusListActivity extends BaseActivity {
  @Bind(R.id.activeTreatmentsRecyclerView) RecyclerView activeTreatmentsRecyclerView;
  @Bind(R.id.inactiveTreatmentsRecyclerView) RecyclerView inactiveTreatmentsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ChronicStatusAdapter chronicStatusAdapter;
  private ChronicInactiveStatusAdapter chronicInactiveStatusAdapter;

  private Calendar fromDate = Calendar.getInstance();
  private Calendar toDate = Calendar.getInstance();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.chronic_status_list_activity_title), true);

    setupActiveTreatmentsList();

    setupInactiveTreatmentsList();
  }

  private void setupActiveTreatmentsList() {
    ArrayList<TreatmentsModel> activeTreatments = new ArrayList<>();

    fromDate.set(Calendar.YEAR, 2016);
    fromDate.set(Calendar.MONTH, 5);
    fromDate.set(Calendar.DAY_OF_MONTH, 26);

    toDate.set(Calendar.YEAR, 2016);
    toDate.set(Calendar.MONTH, 6);
    toDate.set(Calendar.DAY_OF_MONTH, 28);

    activeTreatments.add(new TreatmentsModel("Chronic Treatment 1", fromDate, toDate, "Approved"));

    fromDate.set(Calendar.YEAR, 2017);
    fromDate.set(Calendar.MONTH, 1);
    fromDate.set(Calendar.DAY_OF_MONTH, 22);
    toDate.set(Calendar.YEAR, 2018);
    toDate.set(Calendar.MONTH, 8);
    toDate.set(Calendar.DAY_OF_MONTH, 23);

    activeTreatments.add(
        new TreatmentsModel("Another Treatment 2", fromDate, toDate, "Partially Approved"));

    fromDate.set(Calendar.YEAR, 2019);
    fromDate.set(Calendar.MONTH, 1);
    fromDate.set(Calendar.DAY_OF_MONTH, 22);

    toDate.set(Calendar.YEAR, 2020);
    toDate.set(Calendar.MONTH, 5);
    toDate.set(Calendar.DAY_OF_MONTH, 2);

    activeTreatments.add(
        new TreatmentsModel("Chronic Treatment 4", fromDate, toDate, "Processing"));

    chronicStatusAdapter = new ChronicStatusAdapter(this, activeTreatments);
    activeTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    activeTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    activeTreatmentsRecyclerView.setHasFixedSize(true);
    activeTreatmentsRecyclerView.setAdapter(chronicStatusAdapter);
  }

  private void setupInactiveTreatmentsList() {
    ArrayList<TreatmentsModel> inactiveTreatmentsList = new ArrayList<>();

    fromDate.set(Calendar.YEAR, 2015);
    fromDate.set(Calendar.MONTH, 2);
    fromDate.set(Calendar.DAY_OF_MONTH, 16);

    toDate.set(Calendar.YEAR, 2020);
    toDate.set(Calendar.MONTH, 4);
    toDate.set(Calendar.DAY_OF_MONTH, 7);

    inactiveTreatmentsList.add(
        new TreatmentsModel("Chronic Treatment 4", fromDate, toDate, "Rejected"));

    fromDate.set(Calendar.YEAR, 2017);
    fromDate.set(Calendar.MONTH, 1);
    fromDate.set(Calendar.DAY_OF_MONTH, 22);
    toDate.set(Calendar.YEAR, 2018);
    toDate.set(Calendar.MONTH, 8);
    toDate.set(Calendar.DAY_OF_MONTH, 23);

    inactiveTreatmentsList.add(
        new TreatmentsModel("Another Treatment 5", fromDate, toDate, "Pending for Deletion"));

    fromDate.set(Calendar.YEAR, 2016);
    fromDate.set(Calendar.MONTH, 5);
    fromDate.set(Calendar.DAY_OF_MONTH, 3);

    toDate.set(Calendar.YEAR, 2017);
    toDate.set(Calendar.MONTH, 0);
    toDate.set(Calendar.DAY_OF_MONTH, 1);

    inactiveTreatmentsList.add(
        new TreatmentsModel("Chronic Treatment 6", fromDate, toDate, "Terminated"));

    chronicInactiveStatusAdapter = new ChronicInactiveStatusAdapter(this, inactiveTreatmentsList);
    inactiveTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    inactiveTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    inactiveTreatmentsRecyclerView.setHasFixedSize(true);
    inactiveTreatmentsRecyclerView.setAdapter(chronicInactiveStatusAdapter);
  }
}
