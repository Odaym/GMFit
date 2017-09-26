package com.mcsaatchi.gmfit.nutrition.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import java.util.ArrayList;

public class NutritionWidgetsRecyclerAdapter
    extends RecyclerView.Adapter<NutritionWidgetsRecyclerAdapter.RecyclerViewHolder> {
  private Context context;
  private ArrayList<NutritionWidget> widgetsMap;
  private int layoutResourceId;

  public NutritionWidgetsRecyclerAdapter(Context context, ArrayList<NutritionWidget> widgetsMap,
      int layoutResourceId) {
    this.context = context;
    this.widgetsMap = widgetsMap;
    this.layoutResourceId = layoutResourceId;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    holder.metricTitleTV.setText((widgetsMap.get(position)).getTitle());

    if (widgetsMap.get(position).getValue() == 0.0) {
      holder.metricTV.setText(Helpers.getFormattedString(0));
    } else {
      holder.metricTV.setText(Helpers.getFormattedString(widgetsMap.get(position).getValue()));
    }

    if ((widgetsMap.get(position)).getPercentage() == 0.0) {
      holder.metricPercentageTV.setText("(" + Helpers.getFormattedString(0) + "%)");
    } else {
      holder.metricPercentageTV.setText(
          "(" + Helpers.getFormattedString((widgetsMap.get(position)).getPercentage()) + "%)");
    }

    holder.measurementUnitTV.setText((widgetsMap.get(position)).getMeasurementUnit());
  }

  @Override public int getItemCount() {
    return widgetsMap.size();
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder {

    FontTextView metricTV;
    TextView measurementUnitTV;
    TextView metricPercentageTV;
    FontTextView metricTitleTV;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      metricTV = itemView.findViewById(R.id.metricTV);
      metricPercentageTV = itemView.findViewById(R.id.metricPercentageTV);
      measurementUnitTV = itemView.findViewById(R.id.measurementUnitTV);
      metricTitleTV = itemView.findViewById(R.id.metricTitleTV);
    }
  }
}