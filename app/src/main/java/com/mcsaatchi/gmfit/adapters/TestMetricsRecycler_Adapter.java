package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseDatum;

import java.util.ArrayList;

public class TestMetricsRecycler_Adapter extends RecyclerView.Adapter<TestMetricsRecycler_Adapter.MyViewHolder> {

    private ArrayList<MedicalTestsResponseDatum> testMetrics;
    private Context context;

    public TestMetricsRecycler_Adapter(Context context, ArrayList<MedicalTestsResponseDatum> testMetrics) {
        this.testMetrics = testMetrics;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_add_health_test_details, parent, false);

        return new MyViewHolder(itemView);
    }

    public MedicalTestsResponseDatum getItem(int position){
        return testMetrics.get(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.metricNameTV.setText(testMetrics.get(position).getName());

        ArrayList<String> metricUnits = new ArrayList<>();

        for (int i = 0; i < testMetrics.get(position).getUnits().size(); i++) {
            metricUnits.add(testMetrics.get(position).getUnits().get(i).getUnit());
        }

        TestMetricUnitsSpinnerAdapter adapter = new TestMetricUnitsSpinnerAdapter(context, metricUnits);
        holder.metricUnitsSpinner.setAdapter(adapter);

        holder.metricValueET.setTextColor(context.getResources().getColor(R.color.health_green));
    }

    @Override
    public int getItemCount() {
        return testMetrics.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView metricNameTV;
        EditText metricValueET;
        Spinner metricUnitsSpinner;

        MyViewHolder(View view) {
            super(view);
            metricNameTV = (TextView) view.findViewById(R.id.metricNameTV);
            metricValueET = (EditText) view.findViewById(R.id.metricValueET);
            metricUnitsSpinner = (Spinner) view.findViewById(R.id.metricUnitsSpinner);
        }
    }
}
