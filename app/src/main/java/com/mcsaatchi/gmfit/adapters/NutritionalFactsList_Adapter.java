package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.models.NutritionalFact;

import java.util.List;

public class NutritionalFactsList_Adapter extends BaseAdapter {

  private Context context;
  private List<NutritionalFact> nutritionalFacts;

  public NutritionalFactsList_Adapter(Context context, List<NutritionalFact> nutritionalFacts) {
    super();
    this.context = context;
    this.nutritionalFacts = nutritionalFacts;
  }

  @Override public int getCount() {
    return nutritionalFacts.size();
  }

  @Override public NutritionalFact getItem(int index) {
    return nutritionalFacts.get(index);
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

    holder.itemNameTV.setText(nutritionalFacts.get(position).getName());

    holder.itemHintTV.setText(nutritionalFacts.get(position).getUnit());

    return convertView;
  }

  class ViewHolder {
    TextView itemNameTV;
    TextView itemHintTV;
  }
}
