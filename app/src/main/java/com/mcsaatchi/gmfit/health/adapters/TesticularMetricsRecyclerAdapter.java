package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponseDatum;
import java.util.ArrayList;

public class TesticularMetricsRecyclerAdapter
    extends RecyclerView.Adapter<TesticularMetricsRecyclerAdapter.MyViewHolder> {

  private ArrayList<MedicalTestMetricsResponseBody> testMetrics;
  private Context context;

  public TesticularMetricsRecyclerAdapter(Context context,
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

  @Override public void onBindViewHolder(final MyViewHolder holder, final int position) {

    holder.metricNameTV.setText(testMetrics.get(position).getName());

    TestMetricUnitsSpinnerAdapter adapter = new TestMetricUnitsSpinnerAdapter(context,
        (ArrayList<MedicalTestMetricsResponseDatum>) testMetrics.get(position).getUnits(),
        testMetrics.get(position).getUnits().size());
    holder.metricUnitsSpinner.setAdapter(adapter);

    holder.metricUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        testMetrics.get(position).getUnits().get(i).setSelected(true);
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    holder.metricValueET.setTextColor(context.getResources().getColor(R.color.health_green));

    holder.metricValueET.setText(testMetrics.get(position).getValue());

    holder.metricValueET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        testMetrics.get(position).setValue(editable.toString());
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
