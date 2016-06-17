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
import com.mcsaatchi.gmfit.classes.ParcelableString;

public class FitnessWidgets_GridAdapter extends BaseAdapter {
    private Context context;
    private ParcelableSparseArray widgetsMap;

    public FitnessWidgets_GridAdapter(Context context, ParcelableSparseArray widgetsMap) {
        this.context = context;
        this.widgetsMap = widgetsMap;
    }

    public int getCount() {
        return widgetsMap.size();
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

        holder.metricTV.setText(((ParcelableString) widgetsMap.valueAt(position)).getValue());

        holder.metricIcon.setImageResource(((ParcelableString) widgetsMap.valueAt(position)).getKey());

        return convertView;
    }

    class ViewHolder {
        ImageView metricIcon;
        TextView metricTV;
    }
}