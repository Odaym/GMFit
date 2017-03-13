package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.InquiriesListResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InquiryStatusListActivity extends BaseActivity {
  @Bind(R.id.inquiriesRecyclerView) RecyclerView inquiriesRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.inquiry_complaint_activity_title), true);

    getInquiriesList();
  }

  public void getInquiriesList() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getInquiriesList(null, new Callback<InquiriesListResponse>() {
      @Override public void onResponse(Call<InquiriesListResponse> call,
          Response<InquiriesListResponse> response) {

        switch (response.code()) {
          case 200:
            //Intent intent = new Intent(getActivity(), PDFViewerActivity.class);
            //intent.putExtra("TITLE", "Card Details");
            //intent.putExtra("PDF",
            //    response.body().getData().getBody().getData().replace("\\", ""));
            //
            //startActivity(intent);
            break;
          case 449:
            alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
            alertDialog.show();
            break;
        }

        waitingDialog.dismiss();
      }

      @Override public void onFailure(Call<InquiriesListResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }
}
