package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddHealthTestDetails_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseBody;
import java.util.ArrayList;
import org.joda.time.LocalDate;

public class AvailableTestsRecycler_Adapter
    extends RecyclerView.Adapter<AvailableTestsRecycler_Adapter.MyViewHolder> {

  private ArrayList<MedicalTestsResponseBody> availableTests;

  private Context context;

  public AvailableTestsRecycler_Adapter(ArrayList<MedicalTestsResponseBody> availableTests, Context context) {
    this.availableTests = availableTests;
    this.context = context;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_available_tests, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {

    MedicalTestsResponseBody testResponseBody = availableTests.get(position);

    holder.itemNameTV.setText(testResponseBody.getName());
  }

  @Override public int getItemCount() {
    return availableTests.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView itemNameTV;

    MyViewHolder(View view) {
      super(view);
      view.setOnClickListener(this);
      itemNameTV = (TextView) view.findViewById(R.id.itemNameTV);
    }

    @Override public void onClick(View view) {
      Intent intent = new Intent(context, AddHealthTestDetails_Activity.class);
      intent.putExtra(Constants.EXTRAS_TEST_TITLE,
          availableTests.get(getAdapterPosition()).getName());
      intent.putExtra(Constants.EXTRAS_TEST_SLUG,
          availableTests.get(getAdapterPosition()).getSlug());
      intent.putExtra(Constants.EXTRAS_TEST_DATE_TAKEN, new LocalDate().toString());
      intent.putParcelableArrayListExtra(Constants.EXTRAS_TEST_OBJECT_DETAILS,
          (ArrayList<? extends Parcelable>) availableTests.get(getAdapterPosition()).getMetrics());
      context.startActivity(intent);
    }
  }
}
