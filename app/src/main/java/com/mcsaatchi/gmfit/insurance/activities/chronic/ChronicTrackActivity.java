package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicStatusAdapter;
import com.mcsaatchi.gmfit.insurance.presenters.ChronicTrackActivityPresenter;
import java.util.List;

public class ChronicTrackActivity extends BaseActivity
    implements ChronicTrackActivityPresenter.ChronicTrackActivityView {
  @Bind(R.id.activeTreatmentsRecyclerView) RecyclerView activeTreatmentsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.chronic_status_list_activity_title), true);

    ChronicTrackActivityPresenter presenter =
        new ChronicTrackActivityPresenter(this, dataAccessHandler);

    presenter.getChronicTreatmentsList(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
  }

  @Override
  public void setupActiveTreatmentsList(List<ChronicTreatmentListInnerData> activeTreatments) {
    ChronicStatusAdapter chronicStatusAdapter = new ChronicStatusAdapter(this, activeTreatments);
    activeTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    activeTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    activeTreatmentsRecyclerView.setHasFixedSize(true);
    activeTreatmentsRecyclerView.setAdapter(chronicStatusAdapter);
  }
}
