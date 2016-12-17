package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import java.util.ArrayList;

public class HealthWidgetsGridAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<HealthWidget> widgetsMap;
  private int layoutResourceId;

  public HealthWidgetsGridAdapter(Context context, ArrayList<HealthWidget> widgetsMap,
      int layoutResourceId) {
    this.context = context;
    this.widgetsMap = widgetsMap;
    this.layoutResourceId = layoutResourceId;
  }

  public int getCount() {
    return widgetsMap.size();
  }

  public HealthWidget getItem(int position) {
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
      convertView = inflater.inflate(layoutResourceId, parent, false);

      holder = new ViewHolder();

      holder.metricTV = (FontTextView) convertView.findViewById(R.id.metricTV);
      holder.measurementUnitTV = (TextView) convertView.findViewById(R.id.measurementUnitTV);
      holder.metricTitleTV = (FontTextView) convertView.findViewById(R.id.metricTitleTV);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.metricTitleTV.setText((widgetsMap.get(position)).getTitle());
    holder.metricTV.setText(String.valueOf((int) widgetsMap.get(position).getValue()));
    holder.measurementUnitTV.setText((widgetsMap.get(position)).getMeasurementUnit());

    return convertView;
  }

  class ViewHolder {
    FontTextView metricTV;
    TextView measurementUnitTV;
    FontTextView metricTitleTV;
  }
}