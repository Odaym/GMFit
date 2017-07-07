package com.mcsaatchi.gmfit.insurance.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.ApprovalRequestsTrackActivity;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.SubmitApprovalRequestActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicTrackActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.SubmitChronicActivity;
import com.mcsaatchi.gmfit.insurance.activities.home.SnapshotActivity;
import com.mcsaatchi.gmfit.insurance.activities.inquiry.InquiryTrackActivity;
import com.mcsaatchi.gmfit.insurance.activities.inquiry.SubmitInquiryActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementTrackActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.SubmitReimbursementActivity;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.io.File;
import java.util.ArrayList;
import javax.inject.Inject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InsuranceOperationWidgetsGridAdapter
    extends RecyclerView.Adapter<InsuranceOperationWidgetsGridAdapter.RecyclerViewHolder> {
  private static final int SUBMIT_ITEM = 0;
  private static final int TRACK_ITEM = 1;
  private static final int MEMBER_GUIDE_ITEM = 2;
  private static final int COVERAGE_ITEM = 3;
  private static final int SNAPSHOT_ITEM = 4;
  private static final int POLICY_LIMITATION_ITEM = 5;
  private static final int REIMBURSEMENT_ITEM = 0;
  private static final int APPROVAL_REQUEST_ITEM = 1;
  private static final int CHRONIC_ITEM = 2;
  private static final int INQUIRY_REQUESTS_ITEM = 3;

  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;

  private ArrayList<InsuranceOperationWidget> widgetsMap;
  private FragmentActivity fragmentActivity;
  private Context context;
  private String[] dialogItems =
      new String[] { "Reimbursement", "Approval Request", "Chronic", "Complaint" };

  public InsuranceOperationWidgetsGridAdapter(FragmentActivity fragmentActivity, Context context,
      ArrayList<InsuranceOperationWidget> widgets) {
    this.widgetsMap = widgets;
    this.context = context;
    this.fragmentActivity = fragmentActivity;

    ((GMFitApplication) context).getAppComponent().inject(this);
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.grid_item_insurance_operation_widget, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    holder.widgetIcon.setImageResource(widgetsMap.get(position).getWidgetResourceID());
    holder.widgetName.setText(widgetsMap.get(position).getWidgetName());
  }

  @Override public int getItemCount() {
    return widgetsMap.size();
  }

  private void getPolicyLimitation() {
    final ProgressDialog waitingDialog = new ProgressDialog(fragmentActivity);
    waitingDialog.setTitle(context.getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(context.getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        context.getResources().getString(R.string.ok), (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getCoverageDescription(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "0",
        new Callback<ResponseBody>() {

          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            switch (response.code()) {
              case 200:
                String PDFname = "PolicyLimitation.pdf";

                saveAndOpenPDF(response.body(), PDFname);

                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(context.getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void getCoverageDescription() {
    final ProgressDialog waitingDialog = new ProgressDialog(fragmentActivity);
    waitingDialog.setTitle(context.getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(context.getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        context.getResources().getString(R.string.ok), (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getCoverageDescription(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "1",
        new Callback<ResponseBody>() {

          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            switch (response.code()) {
              case 200:
                String PDFname = "CoverageDescription.pdf";

                saveAndOpenPDF(response.body(), PDFname);

                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(context.getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void getMembersGuide() {
    final ProgressDialog waitingDialog = new ProgressDialog(fragmentActivity);
    waitingDialog.setTitle(context.getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(context.getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        context.getResources().getString(R.string.ok), (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getMembersGuide(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "0",
        new Callback<ResponseBody>() {

          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            switch (response.code()) {
              case 200:
                String PDFname = "MembersGuide.pdf";

                saveAndOpenPDF(response.body(), PDFname);

                break;
              case 449:
                //view.displayRequestErrorDialog(Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(context.getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void saveAndOpenPDF(ResponseBody responseBody, String PDFname) {
    Helpers.writeResponseBodyToDisk(responseBody, PDFname);

    File file = new File(Environment.getExternalStorageDirectory()
        + File.separator
        + "GMFit"
        + File.separator
        + PDFname);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    fragmentActivity.startActivity(intent);
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView widgetIcon;
    TextView widgetName;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);

      widgetIcon = (ImageView) itemView.findViewById(R.id.widgetIconIV);
      widgetName = (TextView) itemView.findViewById(R.id.widgetNameTV);
    }

    @Override public void onClick(View view) {
      int positionOfOperation = getAdapterPosition();
      Intent intent;

      switch (positionOfOperation) {
        case SUBMIT_ITEM:
          setDropDownItems(SUBMIT_ITEM);
          break;
        case TRACK_ITEM:
          setDropDownItems(TRACK_ITEM);
          break;
        case MEMBER_GUIDE_ITEM:
          getMembersGuide();
          break;
        case COVERAGE_ITEM:
          getCoverageDescription();
          break;
        case SNAPSHOT_ITEM:
          intent = new Intent(fragmentActivity, SnapshotActivity.class);
          fragmentActivity.startActivity(intent);
          break;
        case POLICY_LIMITATION_ITEM:
          getPolicyLimitation();
          break;
      }
    }

    void setDropDownItems(final int request_purpose) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
      builder.setTitle("Please select an item")
          .setItems(dialogItems, (dialogInterface, position) -> {

            Intent intent;

            switch (position) {
              case REIMBURSEMENT_ITEM:
                switch (request_purpose) {
                  case SUBMIT_ITEM:
                    intent = new Intent(context, SubmitReimbursementActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                  case TRACK_ITEM:
                    intent = new Intent(fragmentActivity, ReimbursementTrackActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                }
                break;
              case APPROVAL_REQUEST_ITEM:
                switch (request_purpose) {
                  case SUBMIT_ITEM:
                    intent = new Intent(fragmentActivity, SubmitApprovalRequestActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                  case TRACK_ITEM:
                    intent = new Intent(fragmentActivity, ApprovalRequestsTrackActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                }
                break;
              case CHRONIC_ITEM:
                switch (request_purpose) {
                  case SUBMIT_ITEM:
                    intent = new Intent(fragmentActivity, SubmitChronicActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                  case TRACK_ITEM:
                    intent = new Intent(fragmentActivity, ChronicTrackActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                }
                break;
              case INQUIRY_REQUESTS_ITEM:
                switch (request_purpose) {
                  case SUBMIT_ITEM:
                    intent = new Intent(fragmentActivity, SubmitInquiryActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                  case TRACK_ITEM:
                    intent = new Intent(fragmentActivity, InquiryTrackActivity.class);
                    fragmentActivity.startActivity(intent);
                    break;
                }
                break;
            }
          });
      builder.create().show();
    }
  }
}