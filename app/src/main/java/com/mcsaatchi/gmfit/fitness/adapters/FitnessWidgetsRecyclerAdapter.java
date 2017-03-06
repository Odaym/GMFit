package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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

public class FitnessWidgetsRecyclerAdapter
    extends RecyclerView.Adapter<FitnessWidgetsRecyclerAdapter.RecyclerViewHolder> {
  private Context context;
  private ArrayList<FitnessWidget> widgetsMap;
  private int layoutResourceId;

  public FitnessWidgetsRecyclerAdapter(Context context, ArrayList<FitnessWidget> widgets,
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
        holder.moreInfoIV.setVisibility(View.VISIBLE);
        holder.metricIcon.setImageResource(R.drawable.ic_calories_spent);
        holder.metricTV.setText(String.valueOf(this.widgetsMap.get(position).getValue()));
        holder.measurementUnitTV.setText(this.widgetsMap.get(position).getMeasurementUnit());

        holder.moreInfoIV.setOnClickListener(view -> {
          AlertDialog.Builder builder = new AlertDialog.Builder(context);
          builder.setMessage(R.string.active_calories_calculation_hint_message);
          builder.setCancelable(true);
          builder.setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.cancel());

          AlertDialog alert1 = builder.create();
          alert1.show();
        });
        break;
      case "Distance Traveled":
        holder.moreInfoIV.setVisibility(View.GONE);
        holder.metricIcon.setImageResource(R.drawable.ic_distance_traveled);
        holder.metricTV.setText(String.valueOf(
            new DecimalFormat("##.##").format(this.widgetsMap.get(position).getValue())));
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
    ImageView moreInfoIV;
    FontTextView metricTV;
    TextView measurementUnitTV;

    RecyclerViewHolder(View itemView) {
      super(itemView);
      metricTV = (FontTextView) itemView.findViewById(R.id.metricTV);
      metricIcon = (ImageView) itemView.findViewById(R.id.metricIMG);
      moreInfoIV = (ImageView) itemView.findViewById(R.id.moreInfoIV);
      measurementUnitTV = (TextView) itemView.findViewById(R.id.measurementUnitTV);
    }
  }
}