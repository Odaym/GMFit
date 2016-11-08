package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponseDatum;

import java.util.ArrayList;

public class MedicalConditionsSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

  private final Context activity;
  private ArrayList<MedicalConditionsResponseDatum> listItems;

  public MedicalConditionsSpinnerAdapter(Context context,
      ArrayList<MedicalConditionsResponseDatum> listItems) {
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
    return Long.parseLong(listItems.get(i).getId());
  }

  @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
    TextView txt = new TextView(activity);
    txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16,
        16);
    txt.setGravity(Gravity.CENTER_VERTICAL);
    txt.setText(listItems.get(position).getName());
    txt.setTextColor(activity.getResources().getColor(android.R.color.black));
    return txt;
  }

  public View getView(int position, View view, ViewGroup viewgroup) {
    TextView txt = new TextView(activity);
    txt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
    txt.setPadding(activity.getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16,
        16);
    txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp,
        0);
    txt.setText(listItems.get(position).getName());
    txt.setTextColor(activity.getResources().getColor(android.R.color.white));
    return txt;
  }
}
