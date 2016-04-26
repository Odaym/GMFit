package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeWidget_Activity extends Base_Activity {
    @Bind(R.id.widgetsListView)
    DragSortListView widgetsListView;

    ArrayList<String> listItems = new ArrayList<String>() {{
        add("Item 1");
        add("Item 2");
        add("Item 3");
        add("Item 4");
        add("Item 5");
        add("Item 6");
        add("Item 6");
        add("Item 7");
        add("Item 8");
        add("Item 9");
        add("Item 10");
        add("Item 11");
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.customize_widgets_activity_title, true));

        setContentView(R.layout.activity_customize_widget);

        ButterKnife.bind(this);

        widgetsListView.setAdapter(new CustomizeWidget_Adapter(this, listItems));
    }

    class CustomizeWidget_Adapter extends BaseAdapter {

        private Context context;
        private List<String> listItems;

        public CustomizeWidget_Adapter(Context context, List<String> listItems) {
            super();
            this.context = context;
            this.listItems = listItems;
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
                convertView = inflater.inflate(R.layout.list_item_customize_widget, parent,
                        false);

                holder = new ViewHolder();

                holder.itemNameTV = (TextView) convertView.findViewById(R.id.itemNameTV);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.itemNameTV.setText(listItems.get(position));

            return convertView;
        }

        class ViewHolder {
            TextView itemNameTV;
        }
    }
}