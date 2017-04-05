package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.adapters.StatusAdapter;
import com.mcsaatchi.gmfit.insurance.presenters.ReimbursementTrackActivityPresenter;
import java.util.List;

public class ReimbursementTrackActivity extends BaseActivity
    implements ReimbursementTrackActivityPresenter.ReimbursementTrackActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reimbursement_status_list);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Reimbursement Status", true);

    ReimbursementTrackActivityPresenter presenter =
        new ReimbursementTrackActivityPresenter(this, dataAccessHandler);

    presenter.getReimbursementClaims(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
  }

  @Override public void populateClaimsList(List<ClaimsListResponseDatum> claimsList) {
    StatusAdapter statusAdapter = new StatusAdapter(ReimbursementTrackActivity.this, claimsList,
        (reimbursementModel, index) -> {
          Intent intent =
              new Intent(ReimbursementTrackActivity.this, ReimbursementDetailsActivity.class);
          intent.putExtra(ReimbursementDetailsActivity.REIMBURSEMENT_REQUEST_ID,
              reimbursementModel.getId());
          startActivity(intent);
        });

    recyclerView.setLayoutManager(new LinearLayoutManager(ReimbursementTrackActivity.this));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(statusAdapter);
  }
}