package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.models.ReimbursementModel;
import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
  private List<ReimbursementModel> reimbursements = new ArrayList<>();
  private StatusAdapter.OnClickListener onClickListener;
  public StatusAdapter(List<ReimbursementModel> reimbursements,
      StatusAdapter.OnClickListener onClickListener) {
    this.reimbursements = reimbursements;
    this.onClickListener = onClickListener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    v = inflater.inflate(R.layout.reimbursement_status_item, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    ReimbursementModel reimbursement = reimbursements.get(position);
    holder.populate(reimbursement);
    holder.addListener(reimbursement, position, onClickListener);
  }

  @Override public int getItemCount() {
    return reimbursements.size();
  }

  public interface OnClickListener {
    void onClick(ReimbursementModel reimbursementModel, int index);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView idTv, categoryTv, subCategoryTv, typeTv, serviceDateTv, amountTv, statusTv;
    View container;

    public ViewHolder(View v) {
      super(v);
      idTv = (TextView) v.findViewById(R.id.reimbursementId);
      categoryTv = (TextView) v.findViewById(R.id.category);
      subCategoryTv = (TextView) v.findViewById(R.id.subCategory);
      typeTv = (TextView) v.findViewById(R.id.type);
      serviceDateTv = (TextView) v.findViewById(R.id.dateTime);
      amountTv = (TextView) v.findViewById(R.id.amount);
      statusTv = (TextView) v.findViewById(R.id.status);
      container = v.findViewById(R.id.container);
    }

    void populate(ReimbursementModel reimbursement) {
      idTv.setText("Reimbursement #" + reimbursement.getId());
      categoryTv.setText(reimbursement.getCategory());
      subCategoryTv.setText(reimbursement.getSubCategory());
      typeTv.setText(reimbursement.getType());
      serviceDateTv.setText(reimbursement.getServiceDate());
      amountTv.setText(reimbursement.getAmount());
      statusTv.setText(reimbursement.getStatus());
    }

    void addListener(final ReimbursementModel reimbursement, final int position,
        final OnClickListener onClickListener) {
      container.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onClickListener.onClick(reimbursement, position);
        }
      });
    }
  }
}