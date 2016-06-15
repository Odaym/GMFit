package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

public class FitnessWidgets_GridAdapter extends BaseAdapter {
    private Context context;
    private String[] widgetNames;
    private int[] widgetIcons;

    public FitnessWidgets_GridAdapter(Context context, String[] widgetNames, int[] widgetIcons) {
        this.context = context;
        this.widgetNames = widgetNames;
        this.widgetIcons = widgetIcons;
    }

    public int getCount() {
        return widgetIcons.length;
    }

    public Object getItem(int position) {
        return null;
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
            convertView = inflater.inflate(R.layout.grid_item_fitness_widgets, parent,
                    false);

            holder = new ViewHolder();

            holder.metricTV = (TextView) convertView.findViewById(R.id.metricTV);
            holder.metricIcon = (ImageView) convertView.findViewById(R.id.metricIMG);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.metricIcon.setImageResource(widgetIcons[position]);

        holder.metricTV.setText(widgetNames[position]);

        return convertView;
    }

    class ViewHolder {
        ImageView metricIcon;
        TextView metricTV;
    }
}