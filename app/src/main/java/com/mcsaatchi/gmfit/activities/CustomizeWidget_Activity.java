package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeWidget_Activity extends Base_Activity {
    @Bind(R.id.widgetsListView)
    DragSortListView widgetsListView;

    private String[] listItems = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.customize_widgets_activity_title, true));

        setContentView(R.layout.activity_customize_widget);

        ButterKnife.bind(this);

        widgetsListView.setAdapter(new ArrayAdapter(this,  android.R.layout.simple_list_item_1, listItems));
    }
}
