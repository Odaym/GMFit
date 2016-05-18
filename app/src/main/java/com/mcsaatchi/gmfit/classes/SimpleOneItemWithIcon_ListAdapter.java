package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.models.MealItem;

import java.util.List;

public class SimpleOneItemWithIcon_ListAdapter extends BaseAdapter {

    private Context context;
    private List<MealItem> listItems;
    private int drawableResId;

    public SimpleOneItemWithIcon_ListAdapter(Context context, List<MealItem> listItems, int drawableResId) {
        super();
        this.context = context;
        this.listItems = listItems;
        this.drawableResId = drawableResId;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public MealItem getItem(int index) {
        return listItems.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_simple_one_with_icon, parent,
                    false);

            holder = new ViewHolder();

            holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);
            holder.itemIconRightIMG = (ImageView) convertView.findViewById(R.id.itemIconRightIMG);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemNameTV.setText(listItems.get(position).getName());
        holder.itemIconRightIMG.setImageResource(drawableResId);

        return convertView;
    }

    class ViewHolder {
        TextView itemNameTV;
        ImageView itemIconRightIMG;
    }
}
