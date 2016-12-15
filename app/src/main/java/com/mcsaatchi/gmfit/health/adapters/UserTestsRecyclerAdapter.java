package com.mcsaatchi.gmfit.health.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.activities.AddNewHealthTestActivity;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import timber.log.Timber;

public class UserTestsRecyclerAdapter
    extends RecyclerView.Adapter<UserTestsRecyclerAdapter.MyViewHolder> {

  private List<TakenMedicalTestsResponseBody> userTests;
  private Context context;

  public UserTestsRecyclerAdapter(Context context, List<TakenMedicalTestsResponseBody> userTests) {
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

    try {
      DateTime entryDate = new DateTime(userTest.getDateTaken());

      holder.itemSubtitleTV.setText(entryDate.dayOfWeek().getAsText()
          + " "
          + entryDate.getDayOfMonth()
          + " "
          + entryDate.monthOfYear().getAsText()
          + ", "
          + entryDate.getYear());
    } catch (IllegalFieldValueException e) {
      Timber.d("Date taken for this test was returned as 0000-00-00");
    }
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
      Intent intent = new Intent(context, AddNewHealthTestActivity.class);
      intent.putExtra(Constants.EXTRAS_TEST_OBJECT_DETAILS, userTests.get(getAdapterPosition()));
      for (int i = 0; i < userTests.get(getAdapterPosition()).getImages().size(); i++) {
        Timber.d("Test images are : %s",
            userTests.get(getAdapterPosition()).getImages().get(i).getImage());
      }
      context.startActivity(intent);
    }
  }
}