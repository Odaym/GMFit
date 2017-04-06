package com.mcsaatchi.gmfit.insurance.activities.approval_request;

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
import java.util.List;

public class ApprovalRequestsTrackActivity extends BaseActivity
    implements ApprovalRequestsTrackActivityPresenter.ApprovalRequestsTrackActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_requests_status_list);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Approval Requests Status", true);

    ApprovalRequestsTrackActivityPresenter presenter =
        new ApprovalRequestsTrackActivityPresenter(this, dataAccessHandler);
    presenter.getClaimsList(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "2");
  }

  @Override public void populateClaimsList(List<ClaimsListResponseDatum> claimsList) {
    StatusAdapter statusAdapter = new StatusAdapter(ApprovalRequestsTrackActivity.this, claimsList,
        (reimbursementModel, index) -> {
          Intent intent =
              new Intent(ApprovalRequestsTrackActivity.this, ApprovalRequestDetailsActivity.class);
          intent.putExtra(ApprovalRequestDetailsActivity.APPROVAL_REQUEST_CLAIM_ID,
              reimbursementModel.getId());
          startActivity(intent);
        });

    recyclerView.setLayoutManager(new LinearLayoutManager(ApprovalRequestsTrackActivity.this));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(statusAdapter);
  }
}
