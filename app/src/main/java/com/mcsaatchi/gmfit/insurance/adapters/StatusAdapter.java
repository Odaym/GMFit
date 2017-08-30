package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
  private Context context;
  private List<ClaimsListResponseDatum> reimbursements = new ArrayList<>();
  private StatusAdapter.OnClickListener onClickListener;

  public StatusAdapter(Context context, List<ClaimsListResponseDatum> reimbursements,
      StatusAdapter.OnClickListener onClickListener) {
    this.context = context;
    this.reimbursements = reimbursements;
    this.onClickListener = onClickListener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    v = inflater.inflate(R.layout.list_item_reimbursement_status, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    ClaimsListResponseDatum reimbursement = reimbursements.get(position);
    holder.populate(reimbursement);
    holder.addListener(reimbursement, position, onClickListener);
  }

  @Override public int getItemCount() {
    return reimbursements.size();
  }

  public interface OnClickListener {
    void onClick(ClaimsListResponseDatum reimbursementModel, int index);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView idTv, categoryTv, subCategoryTv, typeTv, serviceDateTv, amountTv, statusTv;
    View container;

    public ViewHolder(View v) {
      super(v);
      idTv = v.findViewById(R.id.reimbursementId);
      categoryTv = v.findViewById(R.id.category);
      subCategoryTv = v.findViewById(R.id.subCategory);
      typeTv = v.findViewById(R.id.type);
      serviceDateTv = v.findViewById(R.id.dateTime);
      amountTv = v.findViewById(R.id.amount);
      statusTv = v.findViewById(R.id.status);
      container = v.findViewById(R.id.container);
    }

    void populate(ClaimsListResponseDatum reimbursement) {
      idTv.setText("#" + reimbursement.getId());
      categoryTv.setText(reimbursement.getCategory());
      subCategoryTv.setText(reimbursement.getSubcategory());
      if (reimbursement.getDate() != null) {
        serviceDateTv.setText(
            Helpers.formatInsuranceDate(new LocalDate(reimbursement.getDate().split(" ")[0])));
      }
      amountTv.setText(String.valueOf(reimbursement.getAmount()));

      if (reimbursement.getStatus() != null) {
        statusTv.setTextColor(context.getResources()
            .getColor(Helpers.determineStatusColor(reimbursement.getStatus())));
        statusTv.setText(reimbursement.getStatus());
      }
    }

    void addListener(final ClaimsListResponseDatum reimbursement, final int position,
        final OnClickListener onClickListener) {
      container.setOnClickListener(view -> onClickListener.onClick(reimbursement, position));
    }
  }
}