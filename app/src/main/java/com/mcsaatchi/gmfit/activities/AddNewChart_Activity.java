package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.SimpleTwoItem_ListAdapter;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewChart_Activity extends Base_Activity {
    @Bind(R.id.chartsList)
    ListView chartsList;

    private SparseArray<String[]> itemsMap = new SparseArray<String[]>() {{
        put(0, new String[]{"Number of Steps", "Steps"});
        put(1, new String[]{"Walking and Running Distance", "KM"});
        put(2, new String[]{"Cycling Distance", "KM"});
        put(3, new String[]{"Total Distance Traveled", "KM"});
        put(4, new String[]{"Flights Climbed", "Steps"});
        put(5, new String[]{"Active Calories", "kcal"});
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.add_new_chart_activity_title, true));

        setContentView(R.layout.activity_add_new_chart);

        ButterKnife.bind(this);

        chartsList.setAdapter(new SimpleTwoItem_ListAdapter(this, itemsMap));

        chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRAS_CHART_TYPE_SELECTED, itemsMap.get(position)[0]);
                setResult(Fitness_Fragment.ADD_NEW_FITNESS_CHART_REQUEST_CODE, intent);
                finish();
            }
        });
    }
}
