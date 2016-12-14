package com.mcsaatchi.gmfit.nutrition.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;

import java.util.ArrayList;

public class NutritionWidgetsGridAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<NutritionWidget> widgetsMap;

  public NutritionWidgetsGridAdapter(Context context, ArrayList<NutritionWidget> widgetsMap) {
    this.context = context;
    this.widgetsMap = widgetsMap;
  }

  public int getCount() {
    return widgetsMap.size();
  }

  public NutritionWidget getItem(int position) {
    return widgetsMap.get(position);
  }

  public long getItemId(int position) {
    return 0;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.grid_item_nutrition_widgets, parent, false);

      holder = new ViewHolder();

      holder.metricTV = (FontTextView) convertView.findViewById(R.id.metricTV);
      holder.metricPercentageTV = (TextView) convertView.findViewById(R.id.metricPercentageTV);
      holder.measurementUnitTV = (TextView) convertView.findViewById(R.id.measurementUnitTV);
      holder.metricTitleTV = (FontTextView) convertView.findViewById(R.id.metricTitleTV);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.metricTitleTV.setText((widgetsMap.get(position)).getTitle());
    holder.metricTV.setText(String.valueOf((int) widgetsMap.get(position).getValue()));
    holder.metricPercentageTV.setText("(" + (widgetsMap.get(position)).getPercentage() + "%)");
    holder.measurementUnitTV.setText((widgetsMap.get(position)).getMeasurementUnit());

    return convertView;
  }

  class ViewHolder {
    FontTextView metricTV;
    TextView measurementUnitTV;
    TextView metricPercentageTV;
    FontTextView metricTitleTV;
  }
}