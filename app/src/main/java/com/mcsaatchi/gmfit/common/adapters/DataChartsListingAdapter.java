package com.mcsaatchi.gmfit.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.List;

public class DataChartsListingAdapter extends BaseAdapter {

  private List<DataChart> chartListItems;

  private Context context;

  public DataChartsListingAdapter(List<DataChart> chartListItems, Context context) {
    super();
    this.chartListItems = chartListItems;
    this.context = context;
  }

  @Override public int getCount() {
    return chartListItems.size();
  }

  @Override public DataChart getItem(int index) {
    return chartListItems.get(index);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.list_item_two_items, parent, false);

      holder = new ViewHolder();

      holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);
      holder.itemHintTV = (TextView) convertView.findViewById(R.id.itemHintTV);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.itemNameTV.setText(chartListItems.get(position).getName());

    holder.itemHintTV.setText(chartListItems.get(position).getMeasurementUnit());

    return convertView;
  }

  class ViewHolder {
    TextView itemNameTV;
    TextView itemHintTV;
  }
}
