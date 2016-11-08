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
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponseBody;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class UserTestsRecycler_Adapter
    extends RecyclerView.Adapter<UserTestsRecycler_Adapter.MyViewHolder> {

  private List<TakenMedicalTestsResponseBody> userTests;
  private Context context;

  public UserTestsRecycler_Adapter(Context context, List<TakenMedicalTestsResponseBody> userTests) {
    this.userTests = userTests;
    this.context = context;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_one_with_icon_and_text, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {

    TakenMedicalTestsResponseBody userTest = userTests.get(position);

    holder.itemNameTV.setText(userTest.getName());

    DateTime entryDate = new DateTime(userTest.getDateTaken());

    holder.itemSubtitleTV.setText(entryDate.dayOfWeek().getAsText()
        + " "
        + entryDate.getDayOfMonth()
        + " "
        + entryDate.monthOfYear().getAsText()
        + ", "
        + entryDate.getYear());
  }

  @Override public int getItemCount() {
    return userTests.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView itemNameTV, itemSubtitleTV;

    MyViewHolder(View view) {
      super(view);
      view.setOnClickListener(this);
      itemNameTV = (TextView) view.findViewById(R.id.itemNameTV);
      itemSubtitleTV = (TextView) view.findViewById(R.id.itemSubtitleTV);
    }

    @Override public void onClick(View view) {
      Intent intent = new Intent(context, AddHealthTestDetails_Activity.class);
      intent.putExtra(Constants.EXTRAS_TEST_INSTANCE_ID,
          userTests.get(getAdapterPosition()).getInstanceId());
      intent.putParcelableArrayListExtra(Constants.EXTRAS_TEST_METRICS,
          (ArrayList<? extends Parcelable>) userTests.get(getAdapterPosition()).getMetrics());
      intent.putParcelableArrayListExtra(Constants.EXTRAS_TEST_IMAGES,
          (ArrayList<? extends Parcelable>) userTests.get(getAdapterPosition()).getImages());
      intent.putExtra(Constants.EXTRAS_TEST_ITEM_PURPOSE_EDITING, true);
      context.startActivity(intent);
    }
  }
}
