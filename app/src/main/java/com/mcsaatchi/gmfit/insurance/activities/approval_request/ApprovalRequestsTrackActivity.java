package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementSearchFilterActivity;
import com.mcsaatchi.gmfit.insurance.adapters.StatusAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApprovalRequestsTrackActivity extends BaseActivity
    implements ApprovalRequestsTrackActivityPresenter.ApprovalRequestsTrackActivityView {

  public static final int SEARCH_CRITERIA_SELECTED = 824;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  @Bind(R.id.searchBarET) EditText searchBarET;
  @Bind(R.id.searchImage) ImageView searchImage;
  @Bind(R.id.searchResultsLayout) RelativeLayout searchResultsLayout;
  @Bind(R.id.clearFilterTV) TextView clearFilterTV;
  private List<ClaimsListResponseDatum> originalClaimsList = null;
  private boolean alreadyReversed = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_requests_status_list);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Approval Requests Status", true);

    ApprovalRequestsTrackActivityPresenter presenter =
        new ApprovalRequestsTrackActivityPresenter(this, dataAccessHandler);
    presenter.getClaimsList(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "2");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case SEARCH_CRITERIA_SELECTED:
        if (data != null) {
          searchResultsLayout.setVisibility(View.VISIBLE);
          clearFilterTV.setOnClickListener(view -> searchBarET.setText(""));

          List<ClaimsListResponseDatum> filteredClaimList = new ArrayList<>();

          ArrayList<String> statusesCriteria =
              data.getExtras().getStringArrayList("STATUSES_SEARCH_CRITERIA");
          ArrayList<String> yearsCriteria =
              data.getExtras().getStringArrayList("YEAR_SEARCH_CRITERIA");

          if (statusesCriteria != null) {
            for (int i = 0; i < statusesCriteria.size(); i++) {
              for (int j = 0; j < originalClaimsList.size(); j++) {
                if (statusesCriteria.get(i).equals("Submitted")) {
                  statusesCriteria.set(i, "Submited");
                }

                if (originalClaimsList.get(j).getStatus().equals(statusesCriteria.get(i))) {
                  filteredClaimList.add(originalClaimsList.get(j));
                }
              }
            }
          }

          if (yearsCriteria != null) {
            for (int i = 0; i < yearsCriteria.size(); i++) {
              for (int j = 0; j < originalClaimsList.size(); j++) {
                if (originalClaimsList.get(j).getDate().contains(yearsCriteria.get(i))
                    && !filteredClaimList.contains(originalClaimsList.get(j))) {
                  filteredClaimList.add(originalClaimsList.get(j));
                }
              }
            }
          }

          populateClaimsList(filteredClaimList);

          break;
        }
    }
  }

  @Override public void populateClaimsList(List<ClaimsListResponseDatum> claimsList) {
    if (!alreadyReversed) {
      Collections.reverse(claimsList);
      alreadyReversed = true;
    }

    if (originalClaimsList == null) originalClaimsList = claimsList;

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

  @Override public void hookupSearchBar(List<ClaimsListResponseDatum> claimsList) {
    searchBarET.addTextChangedListener(new TextWatcher() {

      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        List<ClaimsListResponseDatum> searchResults = new ArrayList<>();

        if (charSequence.toString().length() > 2) {
          searchImage.setImageResource(R.drawable.ic_clear_search);
          searchImage.setOnClickListener(view -> searchBarET.setText(""));

          for (int j = 0; j < claimsList.size(); j++) {
            if (String.valueOf(claimsList.get(j).getId()).contains(charSequence.toString())) {
              searchResults.add(claimsList.get(j));
            }
          }

          populateClaimsList(searchResults);
        } else if (charSequence.toString().isEmpty()) {
          searchImage.setImageResource(R.drawable.ic_search_holo_light);
          searchResultsLayout.setVisibility(View.GONE);
          populateClaimsList(originalClaimsList);
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  @OnClick(R.id.filtersLayout) public void handleFiltersLayoutClicked() {
    searchBarET.setText("");

    Intent intent =
        new Intent(ApprovalRequestsTrackActivity.this, ReimbursementSearchFilterActivity.class);
    startActivityForResult(intent, SEARCH_CRITERIA_SELECTED);
  }
}
