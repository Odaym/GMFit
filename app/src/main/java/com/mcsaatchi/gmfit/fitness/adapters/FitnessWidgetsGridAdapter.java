package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FitnessWidgetsGridAdapter
    extends RecyclerView.Adapter<FitnessWidgetsGridAdapter.RecyclerViewHolder> {
  private Context context;
  private ArrayList<FitnessWidget> widgetsMap;
  private int layoutResourceId;

  public FitnessWidgetsGridAdapter(Context context, ArrayList<FitnessWidget> widgets,
      int layoutResourceId) {
    this.context = context;
    this.widgetsMap = widgets;
    this.layoutResourceId = layoutResourceId;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    switch (widgetsMap.get(position).getTitle()) {
      case "Active Calories":
        holder.metricIcon.setImageResource(R.drawable.ic_calories_spent);
        holder.metricTV.setText(String.valueOf(this.widgetsMap.get(position).getValue()));
        holder.measurementUnitTV.setText(this.widgetsMap.get(position).getMeasurementUnit());
        break;
      case "Distance Traveled":
        holder.metricIcon.setImageResource(R.drawable.ic_distance_traveled);
        holder.metricTV.setText(String.valueOf(
            new DecimalFormat("##.###").format(this.widgetsMap.get(position).getValue())));
        holder.measurementUnitTV.setText(this.widgetsMap.get(position).getMeasurementUnit());
        break;
    }
  }

  @Override public int getItemCount() {
    return widgetsMap.size();
  }

  public FitnessWidget getItem(int position) {
    return widgetsMap.get(position);
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView metricIcon;
    FontTextView metricTV;
    TextView measurementUnitTV;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      metricTV = (FontTextView) itemView.findViewById(R.id.metricTV);
      metricIcon = (ImageView) itemView.findViewById(R.id.metricIMG);
      measurementUnitTV = (TextView) itemView.findViewById(R.id.measurementUnitTV);
    }
  }
}