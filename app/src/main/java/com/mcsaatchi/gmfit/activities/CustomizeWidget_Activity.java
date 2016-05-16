package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.SimpleOneItemWithIcon_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeWidget_Activity extends Base_Activity {
    @Bind(R.id.widgetsListView)
    DragSortListView widgetsListView;

    private SimpleOneItemWithIcon_Sparse_ListAdapter customizeWidgetsAdapter;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private SparseArray<String[]> itemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Walking and Running Distance"});
        put(1, new String[]{"Cycling Distance"});
        put(2, new String[]{"Distance Traveled"});
        put(3, new String[]{"Flights Climbed"});
        put(4, new String[]{"Active Calories"});
        put(5, new String[]{"Resting Calories"});
    }};

    private SparseArray<String[]> orderedItemsMap = new SparseArray<>();

    private List<Integer> itemIndeces = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.customize_widgets_activity_title, true));

        setContentView(R.layout.activity_customize_widget);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Constants.EXTRAS_PREFS, Context.MODE_PRIVATE);

        if (prefs.getString(Constants.EXTRAS_WIDGETS_ORDER_ARRAY, null) != null) {
            String savedString = prefs.getString(Constants.EXTRAS_WIDGETS_ORDER_ARRAY, null);

            StringTokenizer st = new StringTokenizer(savedString, ",");
            for (int i = 0; i < itemsMap.size(); i++) {
                itemIndeces.add(Integer.parseInt(st.nextToken()));
                Log.d(Constants.TAG, "onCreate: items order " + itemIndeces.get(i));
                orderedItemsMap.put(i, itemsMap.get(itemIndeces.get(i)));
            }
            itemsMap = orderedItemsMap;
        } else {
            for (int i = 0; i < itemsMap.size(); i++) {
                itemIndeces.add(itemsMap.keyAt(i));
            }
        }

        hookupListWithItems(itemsMap);
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Constants.EXTRAS_WIDGETS_ORDER_ARRAY_CHANGED, itemsMap));

                    customizeWidgetsAdapter.notifyData();

                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < itemsMap.size(); i++) {
                        str.append(itemIndeces.get(i)).append(",");
                    }

                    prefsEditor = prefs.edit();
                    prefsEditor.putString(Constants.EXTRAS_WIDGETS_ORDER_ARRAY, str.toString());
                    prefsEditor.apply();
                }
            };

    private DragSortListView.DragListener onDrag = new DragSortListView.DragListener() {
        @Override
        public void drag(int from, int to) {
            if (to < itemsMap.size() && from < itemsMap.size()) {
                Collections.swap(itemIndeces, from, to);

                String[] tempItem = itemsMap.get(from);
                itemsMap.setValueAt(from, itemsMap.get(to));
                itemsMap.setValueAt(to, tempItem);
            }
        }
    };

    private void hookupListWithItems(SparseArray<String[]> items) {
        widgetsListView.setDragListener(onDrag);
        widgetsListView.setDropListener(onDrop);

        customizeWidgetsAdapter = new SimpleOneItemWithIcon_Sparse_ListAdapter(this, items, /* */R.drawable.ic_menu_black_24dp);
        widgetsListView.setAdapter(customizeWidgetsAdapter);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.list_header_customize_widgets, widgetsListView, false);
        widgetsListView.addHeaderView(header, null, false);
    }
}