package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.FontTextView;
import com.mcsaatchi.gmfit.models.FitnessWidget;

import java.util.ArrayList;

public class FitnessWidgets_GridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FitnessWidget> widgetsMap;
    private int layoutResourceId;

    public FitnessWidgets_GridAdapter(Context context, ArrayList<FitnessWidget> widgetsMap, int layoutResourceId) {
        this.context = context;
        this.widgetsMap = widgetsMap;
        this.layoutResourceId = layoutResourceId;
    }

    public int getCount() {
        return widgetsMap.size();
    }

    public FitnessWidget getItem(int position) {
        return widgetsMap.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent,
                    false);

            holder = new ViewHolder();

            holder.metricTV = (FontTextView) convertView.findViewById(R.id.metricTV);
            holder.metricIcon = (ImageView) convertView.findViewById(R.id.metricIMG);
            holder.measurementUnitTV = (TextView) convertView.findViewById(R.id.measurementUnitTV);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d("TAG", "getView: widgetsMap.get(position).getValue() " + widgetsMap.get(position).getValue());
        Log.d("TAG", "getView: widgetsMap.get(position).getValue() " + widgetsMap.get(position).getValue());

        holder.metricTV.setText(String.valueOf(widgetsMap.get(position).getValue()));
        holder.measurementUnitTV.setText(widgetsMap.get(position).getMeasurementUnit());

        switch (widgetsMap.get(position).getTitle()) {
            case "Distance":
                holder.metricIcon.setImageResource(R.drawable.ic_distance_traveled);
                break;
            case "Calories":
                holder.metricIcon.setImageResource(R.drawable.ic_calories_spent);
                break;
        }

        return convertView;
    }

    class ViewHolder {
        ImageView metricIcon;
        FontTextView metricTV;
        TextView measurementUnitTV;
    }
}