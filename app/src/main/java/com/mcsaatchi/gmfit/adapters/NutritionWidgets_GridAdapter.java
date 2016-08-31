package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.models.NutritionWidget;

import java.util.ArrayList;

public class NutritionWidgets_GridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NutritionWidget> widgetsMap;
    private int layoutResourceId;

    public NutritionWidgets_GridAdapter(Context context, ArrayList<NutritionWidget> widgetsMap, int layoutResourceId) {
        this.context = context;
        this.widgetsMap = widgetsMap;
        this.layoutResourceId = layoutResourceId;
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

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent,
                    false);

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