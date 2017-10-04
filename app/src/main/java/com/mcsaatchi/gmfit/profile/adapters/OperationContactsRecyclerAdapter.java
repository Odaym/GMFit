package com.mcsaatchi.gmfit.profile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseBody;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.profile.activities.OperationContactDetailsActivity;
import java.util.List;

public class OperationContactsRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<OperationContactsResponseBody> operationContactsResponseBody;

  public OperationContactsRecyclerAdapter(Context context,
      List<OperationContactsResponseBody> operationContactsResponseBody) {
    this.context = context;
    this.operationContactsResponseBody = operationContactsResponseBody;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_operation_contacts, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.operationContactNameTV.setText(operationContactsResponseBody.get(position).getName());
  }

  @Override public int getItemCount() {
    return operationContactsResponseBody.size();
  }

  public OperationContactsResponseBody getItem(int position) {
    return operationContactsResponseBody.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView operationContactNameTV;
    private ImageView indicatorArrowIV;

    public ViewHolder(View itemView) {
      super(itemView);

      operationContactNameTV = itemView.findViewById(R.id.operationContactNameTV);
      indicatorArrowIV = itemView.findViewById(R.id.indicatorArrowIV);

      if (Helpers.isLanguageArabic()) {
        indicatorArrowIV.setScaleX(-1);
      }

      itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, OperationContactDetailsActivity.class);
        intent.putExtra("OPERATION_CONTACT",
            operationContactsResponseBody.get(getAdapterPosition()));
        context.startActivity(intent);
      });
    }
  }
}
