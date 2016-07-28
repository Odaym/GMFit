package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.support.design.internal.ParcelableSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.classes.ParcelableFitnessString;
import com.mcsaatchi.gmfit.classes.ParcelableNutritionString;

import java.text.NumberFormat;

public class Widgets_GridAdapter extends BaseAdapter {
    private Context context;
    private ParcelableSparseArray widgetsMap;
    private int layoutResourceId;

    public Widgets_GridAdapter(Context context, ParcelableSparseArray widgetsMap, int layoutResourceId) {
        this.context = context;
        this.widgetsMap = widgetsMap;
        this.layoutResourceId = layoutResourceId;
    }

    public int getCount() {
        return widgetsMap.size();
    }

    public ParcelableFitnessString getItem(int position) {
        return (ParcelableFitnessString) widgetsMap.valueAt(position);
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

            switch (layoutResourceId) {
                case R.layout.grid_item_fitness_widgets:
                    holder.metricTV = (FontTextView) convertView.findViewById(R.id.metricTV);
                    holder.metricIcon = (ImageView) convertView.findViewById(R.id.metricIMG);
                    holder.measurementUnitTV = (TextView) convertView.findViewById(R.id.measurementUnitTV);
                    break;
                case R.layout.grid_item_nutrition_widgets:
                    holder.metricTV = (FontTextView) convertView.findViewById(R.id.metricTV);
                    holder.metricPercentageTV = (TextView) convertView.findViewById(R.id.metricPercentageTV);
                    holder.measurementUnitTV = (TextView) convertView.findViewById(R.id.measurementUnitTV);
                    holder.metricTitleTV = (FontTextView) convertView.findViewById(R.id.metricTitleTV);
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch(layoutResourceId){
            case R.layout.grid_item_fitness_widgets:
//                holder.metricTV.setText(NumberFormat.getInstance().format(Double.parseDouble(String.valueOf(((ParcelableFitnessString) widgetsMap.valueAt(position))
//                        .getValue()))));

                holder.metricTV.setText((String.valueOf(((ParcelableFitnessString) widgetsMap.valueAt(position))
                        .getValue())));

                holder.metricIcon.setImageResource(((ParcelableFitnessString) widgetsMap.valueAt(position)).getDrawableResId());
                holder.measurementUnitTV.setText(((ParcelableFitnessString) widgetsMap.valueAt(position)).getMeasurementUnit());
                break;
            case R.layout.grid_item_nutrition_widgets:
                holder.metricTV.setText(NumberFormat.getInstance().format(Double.parseDouble(String.valueOf(((ParcelableNutritionString) widgetsMap.valueAt(position))
                        .getValue()))));

                holder.metricTitleTV.setText(((ParcelableNutritionString) widgetsMap.valueAt(position)).getTitle());
                holder.metricPercentageTV.setText("(" + ((ParcelableNutritionString) widgetsMap.valueAt(position)).getPercentage() + "%)");
                holder.measurementUnitTV.setText(((ParcelableNutritionString) widgetsMap.valueAt(position)).getMeasurementUnit());

                break;
        }

        return convertView;
    }

    class ViewHolder {
        ImageView metricIcon;
        FontTextView metricTV;
        TextView measurementUnitTV;
        TextView metricPercentageTV;
        FontTextView metricTitleTV;
    }
}