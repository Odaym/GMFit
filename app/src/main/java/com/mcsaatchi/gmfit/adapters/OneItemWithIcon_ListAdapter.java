package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

import java.util.List;

public class OneItemWithIcon_ListAdapter extends BaseAdapter {

    private Context context;
    private List<String> listItems;
    private int drawableResId;

    public OneItemWithIcon_ListAdapter(Context context, List<String> listItems, int drawableResId) {
        super();
        this.context = context;
        this.listItems = listItems;
        this.drawableResId = drawableResId;
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public String getItem(int index) {
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
            convertView = inflater.inflate(R.layout.list_item_one_with_icon, parent,
                    false);

            holder = new ViewHolder();

            holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);
            holder.itemIconRightIMG = (ImageView) convertView.findViewById(R.id.itemIconRightIMG);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemNameTV.setText(listItems.get(position));
        holder.itemIconRightIMG.setImageResource(drawableResId);

        return convertView;
    }

    class ViewHolder {
        TextView itemNameTV;
        ImageView itemIconRightIMG;
    }
}
