package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

public class DraggableOneItem_ListAdapter extends BaseAdapter {

    private Context context;
    private SparseArray<String[]> listItems;

    public DraggableOneItem_ListAdapter(Context context, SparseArray<String[]> listItems) {
        super();
        this.context = context;
        this.listItems = listItems;
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public String[] getItem(int index) {
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
            convertView = inflater.inflate(R.layout.list_item_simple_draggable, parent,
                    false);

            holder = new ViewHolder();

            holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemNameTV.setText(listItems.get(position)[0]);

        return convertView;
    }

    class ViewHolder {
        TextView itemNameTV;
    }
}
