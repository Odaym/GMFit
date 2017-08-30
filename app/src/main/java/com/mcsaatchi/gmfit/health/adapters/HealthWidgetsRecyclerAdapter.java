package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import java.util.ArrayList;

public class HealthWidgetsRecyclerAdapter
    extends RecyclerView.Adapter<HealthWidgetsRecyclerAdapter.RecyclerViewHolder> {
  private Context context;
  private ArrayList<HealthWidget> widgetsMap;
  private int layoutResourceId;

  public HealthWidgetsRecyclerAdapter(Context context, ArrayList<HealthWidget> widgetsMap,
      int layoutResourceId) {
    this.context = context;
    this.widgetsMap = widgetsMap;
    this.layoutResourceId = layoutResourceId;
  }

  @Override public int getItemCount() {
    return widgetsMap.size();
  }

  public HealthWidget getItem(int position) {
    return widgetsMap.get(position);
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    holder.metricTitleTV.setText((widgetsMap.get(position)).getTitle());
    holder.metricTV.setText(String.valueOf((int) widgetsMap.get(position).getValue()));
    holder.measurementUnitTV.setText((widgetsMap.get(position)).getMeasurementUnit());
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder {

    FontTextView metricTV;
    TextView measurementUnitTV;
    FontTextView metricTitleTV;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      metricTV = itemView.findViewById(R.id.metricTV);
      measurementUnitTV = itemView.findViewById(R.id.measurementUnitTV);
      metricTitleTV = itemView.findViewById(R.id.metricTitleTV);
    }
  }
}