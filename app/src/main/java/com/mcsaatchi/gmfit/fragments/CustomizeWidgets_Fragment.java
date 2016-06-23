package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.OneItemWithIcon_Sparse_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeWidgets_Fragment extends Fragment {
    @Bind(R.id.widgetsListView)
    DragSortListView widgetsListView;
    private OneItemWithIcon_Sparse_ListAdapter customizeWidgetsAdapter;

    private SparseArray<String[]> nutritionItemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Calories", "125", "kcal", "102%"});
        put(1, new String[]{"Biotin", "321", "mcg", "120%"});
        put(2, new String[]{"Caffeine", "913", "mg", "39%"});
        put(3, new String[]{"Calcium", "1092", "mg", "40%"});
        put(4, new String[]{"Carbohydrates", "129", "g", "92%"});
        put(5, new String[]{"Chloride", "923", "mg", "41%"});
        put(6, new String[]{"Chromium", "12,903", "mcg", "59%"});
        put(7, new String[]{"Copper", "301", "mg", "103%"});
        put(8, new String[]{"Dietary Cholesterol", "11", "mg", "2%"});
    }};

    private String PREFS_WIDGETS_ORDER_ARRAY_IDENTIFIER;
    private String WIDGETS_ORDER_ARRAY_CHANGED_EVENT;
    private Activity parentActivity;

    private ParcelableSparseArray itemsMap = new ParcelableSparseArray();

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    EventBus_Singleton.getInstance().post(new EventBus_Poster(WIDGETS_ORDER_ARRAY_CHANGED_EVENT, itemsMap));

                    customizeWidgetsAdapter.notifyData();
                }
            };

    private DragSortListView.DragListener onDrag = new DragSortListView.DragListener() {
        @Override
        public void drag(int from, int to) {
            if (to < itemsMap.size() && from < itemsMap.size()) {
                Parcelable tempItem = itemsMap.valueAt(from);
                itemsMap.setValueAt(from, itemsMap.valueAt(to));
                itemsMap.setValueAt(to, tempItem);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_customize_widgets, null);

        ButterKnife.bind(this, fragmentView);

        Bundle fragmentBundle = getArguments();

        if (fragmentBundle != null) {
            String typeOfFragmentToCustomizeFor = fragmentBundle.getString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE);

            if (typeOfFragmentToCustomizeFor != null) {
                switch (typeOfFragmentToCustomizeFor) {
                    case Cons.EXTRAS_FITNESS_FRAGMENT:
                        itemsMap = fragmentBundle.getParcelable(Cons.BUNDLE_FITNESS_WIDGETS_MAP);

                        PREFS_WIDGETS_ORDER_ARRAY_IDENTIFIER = Cons.EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY;
                        WIDGETS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_FITNESS_WIDGETS_ORDER_ARRAY_CHANGED;

                        break;
                    case Cons.EXTRAS_NUTRITION_FRAGMENT:
//                        itemsMap = nutritionItemsMap;
                        PREFS_WIDGETS_ORDER_ARRAY_IDENTIFIER = Cons.EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY;
                        WIDGETS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY_CHANGED;
                        break;
                    case Cons.EXTRAS_HEALTH_FRAGMENT:
                        //TODO
//                        itemsMap = healthItemsMap;
                        PREFS_WIDGETS_ORDER_ARRAY_IDENTIFIER = Cons.EXTRAS_HEALTH_WIDGETS_ORDER_ARRAY;
                        WIDGETS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_HEALTH_WIDGETS_ORDER_ARRAY_CHANGED;
                        break;
                }
            }
        }

        hookupListWithItems(itemsMap);

        return fragmentView;
    }

    private void hookupListWithItems(ParcelableSparseArray items) {
        widgetsListView.setDragListener(onDrag);
        widgetsListView.setDropListener(onDrop);

        customizeWidgetsAdapter = new OneItemWithIcon_Sparse_ListAdapter(parentActivity, items, R.drawable.ic_menu_black_24dp);
        widgetsListView.setAdapter(customizeWidgetsAdapter);
    }
}