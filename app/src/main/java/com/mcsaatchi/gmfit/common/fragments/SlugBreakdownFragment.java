package com.mcsaatchi.gmfit.common.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SlugBreakdownResponseInnerData;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SlugBreakdownResponsePeriod;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealOnDateActivity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import org.joda.time.DateTime;

public class SlugBreakdownFragment extends Fragment {
  @Bind(R.id.slugBreakdownListView) ListView slugBreakdownListView;
  @Bind(R.id.allTimeValueTV) TextView allTimeValueTV;

  private String typeOfFragmentToCustomizeFor;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_slug_breakdown_daily, null);

    ButterKnife.bind(this, fragmentView);

    Bundle fragmentBundle = getArguments();

    if (fragmentBundle != null) {
      SlugBreakdownResponseInnerData slugBreakdownData =
          fragmentBundle.getParcelable(Constants.BUNDLE_SLUG_BREAKDOWN_DATA);

      String slugBreakDownDataType =
          fragmentBundle.getString(Constants.BUNDLE_SLUG_BREAKDOWN_DATA_TYPE);

      String measurementUnitForMetric =
          fragmentBundle.getString(Constants.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT, "");
      typeOfFragmentToCustomizeFor = fragmentBundle.getString(Constants.EXTRAS_FRAGMENT_TYPE, "");

      float slugBreakdownYearlyTotal =
          fragmentBundle.getFloat(Constants.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL, 0);
      allTimeValueTV.setText(NumberFormat.getNumberInstance(Locale.US)
          .format((int) Double.parseDouble(String.valueOf(slugBreakdownYearlyTotal)))
          + " "
          + measurementUnitForMetric);

      switch (slugBreakDownDataType) {
        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY:
          hookupListWithItems((ArrayList<SlugBreakdownResponsePeriod>) slugBreakdownData.getDaily(),
              measurementUnitForMetric, Constants.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY);
          break;

        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY:
          hookupListWithItems(
              (ArrayList<SlugBreakdownResponsePeriod>) slugBreakdownData.getMonthly(),
              measurementUnitForMetric, Constants.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY);
          break;
        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY:
          hookupListWithItems(
              (ArrayList<SlugBreakdownResponsePeriod>) slugBreakdownData.getYearly(),
              measurementUnitForMetric, Constants.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY);
          break;
      }
    }

    return fragmentView;
  }

  private void hookupListWithItems(ArrayList<SlugBreakdownResponsePeriod> items,
      String measurementUnitForMetric, String breakdownType) {
    final SlugBreakdown_ListAdapter slugBreakdownListAdapter =
        new SlugBreakdown_ListAdapter(getActivity(), items, measurementUnitForMetric,
            breakdownType);
    slugBreakdownListView.setAdapter(slugBreakdownListAdapter);

    if (typeOfFragmentToCustomizeFor.equals(Constants.EXTRAS_NUTRITION_FRAGMENT)) {
      slugBreakdownListView.setOnItemClickListener((adapterView, view, i, l) -> {
        Intent intent = new Intent(getActivity(), AddNewMealOnDateActivity.class);
        intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON,
            slugBreakdownListAdapter.getItem(i).getDate());
        startActivity(intent);
      });
    }
  }

  private class SlugBreakdown_ListAdapter extends BaseAdapter {

    private ArrayList<SlugBreakdownResponsePeriod> slugBreakdownData;
    private Context context;
    private String measurementUnit;
    private String breakdownType;

    SlugBreakdown_ListAdapter(Context context,
        ArrayList<SlugBreakdownResponsePeriod> slugBreakdownData, String measurementUnit,
        String breakdownType) {
      super();
      this.context = context;
      this.slugBreakdownData = slugBreakdownData;
      this.measurementUnit = measurementUnit;
      this.breakdownType = breakdownType;
    }

    @Override public int getCount() {
      return slugBreakdownData.size();
    }

    @Override public SlugBreakdownResponsePeriod getItem(int index) {
      return slugBreakdownData.get(index);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_slug_daily_breakdown, parent, false);

        holder = new ViewHolder();

        holder.slugDateTV = convertView.findViewById(R.id.slugDateTV);
        holder.slugTotalTV = convertView.findViewById(R.id.slugTotalTV);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      DateTime entryDate = new DateTime(getItem(position).getDate());

      switch (breakdownType) {
        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY:
          holder.slugDateTV.setText(entryDate.getDayOfMonth()
              + " "
              + entryDate.monthOfYear().getAsText()
              + ", "
              + entryDate.getYear());
          break;
        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY:
          holder.slugDateTV.setText(
              entryDate.monthOfYear().getAsText() + ", " + entryDate.getYear());
          break;
        case Constants.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY:
          holder.slugDateTV.setText(String.valueOf(entryDate.getYear()));
          break;
      }

      holder.slugTotalTV.setText(NumberFormat.getNumberInstance(Locale.US)
          .format((int) Double.parseDouble(getItem(position).getTotal())) + " " + measurementUnit);

      return convertView;
    }

    class ViewHolder {
      TextView slugDateTV;
      TextView slugTotalTV;
    }
  }
}