package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.adapters.InquiryStatusAdapter;
import java.util.List;

public class InquiryTrackActivity extends BaseActivity
    implements InquiryTrackActivityPresenter.InquiryTrackActivityView {
  @Bind(R.id.inquiriesRecyclerView) RecyclerView inquiriesRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.inquiry_complaint_activity_title), true);

    InquiryTrackActivityPresenter presenter =
        new InquiryTrackActivityPresenter(this, dataAccessHandler);

    presenter.getInquiriesList(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, ""));
  }

  @Override public void displayInquiriesList(List<InquiriesListResponseInnerData> inquiriesList) {
    InquiryStatusAdapter statusAdapter =
        new InquiryStatusAdapter(InquiryTrackActivity.this, inquiriesList);

    inquiriesRecyclerView.setLayoutManager(new LinearLayoutManager(InquiryTrackActivity.this));
    inquiriesRecyclerView.setHasFixedSize(true);
    inquiriesRecyclerView.setAdapter(statusAdapter);

    //Intent intent = new Intent(getActivity(), PDFViewerActivity.class);
    //intent.putExtra("TITLE", "Card Details");
    //intent.putExtra("PDF",
    //    response.body().getData().getBody().getData().replace("\\", ""));
    //
    //startActivity(intent);
  }
}
