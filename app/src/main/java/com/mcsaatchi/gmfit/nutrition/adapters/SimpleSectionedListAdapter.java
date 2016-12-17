package com.mcsaatchi.gmfit.nutrition.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import de.halfbit.pinnedsection.PinnedSectionListView;
import java.util.List;

public class SimpleSectionedListAdapter extends BaseAdapter
    implements PinnedSectionListView.PinnedSectionListAdapter {

  private static final int SECTION_VIEWTYPE = 1;
  private static final int ITEM_VIEWTYPE = 2;
  private Context context;
  private List<MealItem> mealItems;

  public SimpleSectionedListAdapter(Context context, List<MealItem> mealItems) {
    this.context = context;
    this.mealItems = mealItems;
  }

  @Override public int getCount() {
    return mealItems.size();
  }

  @Override public int getViewTypeCount() {
    return 3;
  }

  @Override public MealItem getItem(int index) {
    return mealItems.get(index);
  }

  @Override public int getItemViewType(int position) {
    return getItem(position).getSectionType();
  }

  @Override public long getItemId(int i) {
    return 0;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      holder = new ViewHolder();

      if (mealItems.get(position).getSectionType() == SECTION_VIEWTYPE) {
        convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        holder.itemNameTV = (TextView) convertView.findViewById(android.R.id.text1);
      } else {
        convertView = inflater.inflate(R.layout.list_item_one_with_icon, parent, false);

        holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);
        holder.itemIconRightIMG = (ImageView) convertView.findViewById(R.id.itemIconRightIMG);
      }

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.itemNameTV.setText(mealItems.get(position).getName());

    if (mealItems.get(position).getSectionType() == ITEM_VIEWTYPE) {
      holder.itemNameTV.setTextColor(context.getResources().getColor(android.R.color.black));
      holder.itemIconRightIMG.setImageResource(R.drawable.ic_arrow_right_pink);
    } else {
      holder.itemNameTV.setTextColor(context.getResources().getColor(android.R.color.white));
      holder.itemNameTV.setBackgroundColor(context.getResources().getColor(R.color.nutrition_red));
    }

    return convertView;
  }

  @Override public boolean isItemViewTypePinned(int viewType) {
    return viewType == SECTION_VIEWTYPE;
  }

  class ViewHolder {
    TextView itemNameTV;
    ImageView itemIconRightIMG;
  }
}
