package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.rest.MedicalTestMetricsResponseBody;
import java.util.ArrayList;

public class TesticularMetricsRecycler_Adapter
    extends RecyclerView.Adapter<TesticularMetricsRecycler_Adapter.MyViewHolder> {

  private ArrayList<MedicalTestMetricsResponseBody> testMetrics;
  private Context context;

  public TesticularMetricsRecycler_Adapter(Context context,
      ArrayList<MedicalTestMetricsResponseBody> testMetrics) {
    this.testMetrics = testMetrics;
    this.context = context;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_add_health_test_details, parent, false);

    return new MyViewHolder(itemView);
  }

  public MedicalTestMetricsResponseBody getItem(int position) {
    return testMetrics.get(position);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, final int position) {

    holder.metricNameTV.setText(testMetrics.get(position).getName());

    ArrayList<String> metricUnits = new ArrayList<>();

    for (int i = 0; i < testMetrics.get(position).getUnits().size(); i++) {
      metricUnits.add(testMetrics.get(position).getUnits().get(i).getUnit());
    }

    if (metricUnits.isEmpty()) {
      metricUnits.add("unit 1");
    }

    TestMetricUnitsSpinnerAdapter adapter = new TestMetricUnitsSpinnerAdapter(context, metricUnits);
    holder.metricUnitsSpinner.setAdapter(adapter);

    holder.metricValueET.setTextColor(context.getResources().getColor(R.color.health_green));
    holder.metricValueET.setText(testMetrics.get(position).getValue());

    holder.metricValueET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        testMetrics.get(i).setValue(charSequence.toString());
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  @Override public int getItemCount() {
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
