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
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseMetricsDatum;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseUnit;
import java.util.ArrayList;
import java.util.List;

public class EditableTestMetricsRecycler_Adapter
    extends RecyclerView.Adapter<EditableTestMetricsRecycler_Adapter.MyViewHolder> {

  private List<TakenMedicalTestsResponseMetricsDatum> existingTestMetrics;
  private Context context;

  public EditableTestMetricsRecycler_Adapter(Context context,
      List<TakenMedicalTestsResponseMetricsDatum> existingTestMetrics) {
    this.existingTestMetrics = existingTestMetrics;
    this.context = context;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_add_health_test_details, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public int getItemCount() {
    return existingTestMetrics.size();
  }

  @Override public void onBindViewHolder(final MyViewHolder holder, final int position) {

    holder.metricNameTV.setText(existingTestMetrics.get(position).getName());

    EditableTestMetricUnitsSpinnerAdapter adapter =
        new EditableTestMetricUnitsSpinnerAdapter(context,
            (ArrayList<TakenMedicalTestsResponseUnit>) existingTestMetrics.get(position).getUnits(),
            existingTestMetrics.get(position).getUnits().size());

    holder.metricUnitsSpinner.setAdapter(adapter);

    holder.metricUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        existingTestMetrics.get(i).getUnits().get(i).setSelected(true);
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    holder.metricValueET.setTextColor(context.getResources().getColor(R.color.health_green));

    if (Integer.parseInt(existingTestMetrics.get(position).getValue()) != -1) {
      holder.metricValueET.setText(existingTestMetrics.get(position).getValue());
    }

    holder.metricValueET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void afterTextChanged(Editable editable) {
        existingTestMetrics.get(position).setValue(editable.toString());
      }
    });
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
