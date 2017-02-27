package com.mcsaatchi.gmfit.insurance.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.CoverageDescriptionResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.ApprovalRequestsStatusListActivity;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.SubmitApprovalRequestsActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicStatusListActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.SubmitChronicPrescriptionActivity;
import com.mcsaatchi.gmfit.insurance.activities.home.CoverageDescriptionActivity;
import com.mcsaatchi.gmfit.insurance.activities.inquiry.InquiryEmptyActivity;
import com.mcsaatchi.gmfit.insurance.activities.inquiry.SubmitInquiryActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementStatusListActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.SubmitReimbursementActivity;
import com.mcsaatchi.gmfit.insurance.activities.snapshot.SnapshotActivity;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;
import javax.inject.Inject;
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
  private static final int REIMBURSEMENT_ITEM = 0;
  private static final int APPROVAL_REQUEST_ITEM = 1;
  private static final int CHRONIC_ITEM = 2;
  private static final int INQUIRY_REQUESTS_ITEM = 3;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private ArrayList<InsuranceOperationWidget> widgetsMap;
  private FragmentActivity fragmentActivity;
  private Context context;
  private String[] dialogItems =
      new String[] { "Reimbursement", "Approval Request", "Chronic", "Requests" };

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

  private void getCoverageDescription() {
    final ProgressDialog waitingDialog = new ProgressDialog(fragmentActivity);
    waitingDialog.setTitle(context.getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(context.getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getCoverageDescription(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""),
        new Callback<CoverageDescriptionResponse>() {

          @Override public void onResponse(Call<CoverageDescriptionResponse> call,
              Response<CoverageDescriptionResponse> response) {

            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Intent intent = new Intent(context, CoverageDescriptionActivity.class);
                intent.putExtra("PDF",
                    response.body().getData().getBody().getData().replace("\\", ""));

                fragmentActivity.startActivity(intent);
            }
          }

          @Override public void onFailure(Call<CoverageDescriptionResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                context.getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
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
          break;
        case COVERAGE_ITEM:
          getCoverageDescription();
          break;
        case SNAPSHOT_ITEM:
          intent = new Intent(fragmentActivity, SnapshotActivity.class);
          fragmentActivity.startActivity(intent);
          break;
      }
    }

    void setDropDownItems(final int request_purpose) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
      builder.setTitle("What would you like to submit?")
          .setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int position) {

              Intent intent;

              switch (position) {
                case REIMBURSEMENT_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent = new Intent(context, SubmitReimbursementActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent = new Intent(fragmentActivity, ReimbursementStatusListActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                  }
                  break;
                case APPROVAL_REQUEST_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent = new Intent(fragmentActivity, SubmitApprovalRequestsActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent =
                          new Intent(fragmentActivity, ApprovalRequestsStatusListActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                  }
                  break;
                case CHRONIC_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent =
                          new Intent(fragmentActivity, SubmitChronicPrescriptionActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent = new Intent(fragmentActivity, ChronicStatusListActivity.class);
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
                      intent = new Intent(fragmentActivity, InquiryEmptyActivity.class);
                      fragmentActivity.startActivity(intent);
                      break;
                  }
                  break;
              }
            }
          });
      builder.create().show();
    }
  }
}