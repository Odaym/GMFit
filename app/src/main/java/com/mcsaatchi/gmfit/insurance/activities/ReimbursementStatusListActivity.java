package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.insurance.adapters.StatusAdapter;
import com.mcsaatchi.gmfit.insurance.models.ReimbursementModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReimbursementStatusListActivity extends Base_Activity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    StatusAdapter statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_status);
        ButterKnife.bind(this);
        setupToolbar(toolbar, "Reimbursement Status", true);

        List<ReimbursementModel> mock = new ArrayList<>();
        mock.add(new ReimbursementModel("Reimbursement #232323", "OUT", "Dental", "17 Aug 2016", "LBP 550,000", "Rejected", "Reimbursement"));
        mock.add(new ReimbursementModel("Reimbursement #232323", "OUT", "Dental", "17 Aug 2016", "LBP 550,000", "Rejected", "Reimbursement"));
        mock.add(new ReimbursementModel("Reimbursement #232323", "OUT", "Dental", "17 Aug 2016", "LBP 550,000", "Rejected", "Reimbursement"));
        mock.add(new ReimbursementModel("Reimbursement #232323", "OUT", "Dental", "17 Aug 2016", "LBP 550,000", "Rejected", "Reimbursement"));

        statusAdapter = new StatusAdapter(mock, new StatusAdapter.OnClickListener() {
            @Override
            public void onClick(ReimbursementModel reimbursementModel, int index) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(statusAdapter);
    }
}