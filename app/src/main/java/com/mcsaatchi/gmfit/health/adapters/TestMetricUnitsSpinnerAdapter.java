package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponseDatum;
import java.util.ArrayList;

public class TestMetricUnitsSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

  private final Context activity;
  private ArrayList<MedicalTestMetricsResponseDatum> listItems;
  private int unitsSize;

  public TestMetricUnitsSpinnerAdapter(Context context,
      ArrayList<MedicalTestMetricsResponseDatum> listItems, int unitsSize) {
    this.listItems = listItems;
    this.unitsSize = unitsSize;
    activity = context;
  }

  public int getCount() {
    return unitsSize;
  }

  public Object getItem(int i) {
    return listItems.get(i);
  }

  public long getItemId(int i) {
    return (long) i;
  }

  @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
    TextView txt = new TextView(activity);
    txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16,
        16);
    txt.setGravity(Gravity.CENTER_VERTICAL);
    txt.setText(listItems.get(position).getUnit());
    txt.setTextColor(activity.getResources().getColor(R.color.health_green));
    return txt;
  }

  public View getView(int i, View view, ViewGroup viewgroup) {
    TextView txt = new TextView(activity);
    Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_arrow_down_green);
    drawable.setBounds(0, 0, 50, 50);
    txt.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
    txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16,
        16);
    txt.setCompoundDrawables(null, null, drawable, null);
    txt.setText(listItems.get(i).getUnit());
    txt.setTextColor(activity.getResources().getColor(R.color.health_green));
    return txt;
  }
}
