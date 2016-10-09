package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

import java.util.ArrayList;

public class TestMetricUnitsSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private ArrayList<String> listItems;

    public TestMetricUnitsSpinnerAdapter(Context context, ArrayList<String> listItems) {
        this.listItems = listItems;
        activity = context;
    }


    public int getCount() {
        return listItems.size();
    }

    public Object getItem(int i) {
        return listItems.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16, 16);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(listItems.get(position));
        txt.setTextColor(activity.getResources().getColor(R.color.health_green));
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16, 16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_green, 0);
        txt.setText(listItems.get(i));
        txt.setTextColor(activity.getResources().getColor(R.color.health_green));
        return txt;
    }

}
