package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.ApprovalRequestsStatusEmptyActivity;
import com.mcsaatchi.gmfit.insurance.activities.approval_request.ApprovalRequestsStatusListActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicPrescriptionEmptyActivity;
import com.mcsaatchi.gmfit.insurance.activities.chronic.ChronicStatusListActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ClaimReimbursementActivity;
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementStatusListActivity;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;

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
  private static final int REQUESTS_ITEM = 3;

  private ArrayList<InsuranceOperationWidget> widgetsMap;
  private Context context;
  private String[] dialogItems =
      new String[] { "Reimbursement", "Approval Request", "Chronic", "Requests" };

  public InsuranceOperationWidgetsGridAdapter(Context context,
      ArrayList<InsuranceOperationWidget> widgets) {
    this.widgetsMap = widgets;
    this.context = context;
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
          break;
        case SNAPSHOT_ITEM:
          break;
      }
    }

    void setDropDownItems(final int request_purpose) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle("What would you like to submit?")
          .setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int position) {

              Intent intent;

              switch (position) {
                case REIMBURSEMENT_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent = new Intent(context, ClaimReimbursementActivity.class);
                      context.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent = new Intent(context, ReimbursementStatusListActivity.class);
                      context.startActivity(intent);
                      break;
                  }
                  break;
                case APPROVAL_REQUEST_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent = new Intent(context, ApprovalRequestsStatusEmptyActivity.class);
                      context.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent = new Intent(context, ApprovalRequestsStatusListActivity.class);
                      context.startActivity(intent);
                      break;
                  }
                  break;
                case CHRONIC_ITEM:
                  switch (request_purpose) {
                    case SUBMIT_ITEM:
                      intent = new Intent(context, ChronicPrescriptionEmptyActivity.class);
                      context.startActivity(intent);
                      break;
                    case TRACK_ITEM:
                      intent = new Intent(context, ChronicStatusListActivity.class);
                      context.startActivity(intent);
                      break;
                  }
                  break;
                case REQUESTS_ITEM:
                  break;
              }
            }
          });
      builder.create().show();
    }
  }
}