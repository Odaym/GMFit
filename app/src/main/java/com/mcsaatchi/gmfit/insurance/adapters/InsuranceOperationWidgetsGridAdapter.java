package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;

public class InsuranceOperationWidgetsGridAdapter
    extends RecyclerView.Adapter<InsuranceOperationWidgetsGridAdapter.RecyclerViewHolder> {
  private ArrayList<InsuranceOperationWidget> widgetsMap;

  public InsuranceOperationWidgetsGridAdapter(ArrayList<InsuranceOperationWidget> widgets) {
    this.widgetsMap = widgets;
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

  class RecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView widgetIcon;
    FontTextView widgetName;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      widgetIcon = (ImageView) itemView.findViewById(R.id.widgetIconIV);
      widgetName = (FontTextView) itemView.findViewById(R.id.widgetNameTV);
    }
  }
}