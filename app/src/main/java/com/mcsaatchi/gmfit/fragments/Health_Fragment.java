package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewHealthTest_Activity;
import com.mcsaatchi.gmfit.adapters.HealthWidgets_GridAdapter;
import com.mcsaatchi.gmfit.adapters.UserTestsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.classes.UserTest;
import com.mcsaatchi.gmfit.models.HealthWidget;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Health_Fragment extends Fragment {

    @Bind(R.id.metricCounterTV)
    TextView metricCounterTV;
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.addEntryBTN_MEDICAL_TESTS)
    TextView addEntryBTN_MEDICAL_TESTS;
    @Bind(R.id.userTestsListView)
    RecyclerView userTestsListView;

    private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();

    private ArrayList<UserTest> userTests = new ArrayList<>();

    private SharedPreferences prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);

        metricCounterTV.setText(String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0.0f)));

        HealthWidget widget1 = new HealthWidget();
        widget1.setTitle("BMI");
        widget1.setMeasurementUnit("kg/m");
        widget1.setValue(26.1);

        HealthWidget widget2 = new HealthWidget();
        widget2.setTitle("Cholesterol");
        widget2.setMeasurementUnit("g");
        widget2.setValue(82);

        HealthWidget widget3 = new HealthWidget();
        widget3.setTitle("Glucose");
        widget3.setMeasurementUnit("mmol/L");
        widget3.setValue(13.7);

        HealthWidget widget4 = new HealthWidget();
        widget4.setTitle("Heart Rate");
        widget4.setMeasurementUnit("BPM");
        widget4.setValue(94);

        healthWidgetsMap.add(widget1);
        healthWidgetsMap.add(widget2);
        healthWidgetsMap.add(widget3);
        healthWidgetsMap.add(widget4);

        HealthWidgets_GridAdapter healthWidgetsGridAdapter = new HealthWidgets_GridAdapter(getActivity(), healthWidgetsMap, R.layout.grid_item_health_widgets);

        widgetsGridView.setAdapter(healthWidgetsGridAdapter);

        UserTest userTest1 = new UserTest();
        userTest1.setName("Blood Sugar Tests");
        userTest1.setDateTaken("Sat Apr 2, 2016");

        UserTest userTest2 = new UserTest();
        userTest2.setName("Cholesterol Tests");
        userTest2.setDateTaken("Fri Mar 18, 2016");

        UserTest userTest3 = new UserTest();
        userTest3.setName("Liver Function Test");
        userTest3.setDateTaken("Sat Nov 7, 2015");

        UserTest userTest4 = new UserTest();
        userTest4.setName("Ullamcorper Fringilla");
        userTest4.setDateTaken("Fri Oct 18, 2015");

        userTests.add(userTest1);
        userTests.add(userTest2);
        userTests.add(userTest3);
        userTests.add(userTest4);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        UserTestsRecycler_Adapter userTestsRecyclerAdapter = new UserTestsRecycler_Adapter(getActivity(), userTests);

        userTestsListView.setLayoutManager(mLayoutManager);
        userTestsListView.setAdapter(userTestsRecyclerAdapter);
        userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        addEntryBTN_MEDICAL_TESTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewHealthTest_Activity.class);
                startActivity(intent);
            }
        });

        return fragmentView;
    }
}
