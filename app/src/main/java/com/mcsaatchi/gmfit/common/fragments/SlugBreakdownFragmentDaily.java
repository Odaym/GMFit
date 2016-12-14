package com.mcsaatchi.gmfit.common.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealOnDateActivity;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponseDaily;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlugBreakdownFragmentDaily extends Fragment {
  @Bind(R.id.slugBreakdownListView) ListView slugBreakdownListView;
  @Bind(R.id.allTimeValueTV) TextView allTimeValueTV;

  private String typeOfFragmentToCustomizeFor;
  private String measurementUnitForMetric;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_slug_breakdown_daily, null);

    ButterKnife.bind(this, fragmentView);

    Bundle fragmentBundle = getArguments();

    if (fragmentBundle != null) {
      ArrayList<Parcelable> slugBreakdownData =
          fragmentBundle.getParcelableArrayList(Constants.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY);

      measurementUnitForMetric =
          fragmentBundle.getString(Constants.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT, "");
      typeOfFragmentToCustomizeFor = fragmentBundle.getString(Constants.EXTRAS_FRAGMENT_TYPE, "");

      float slugBreakdownYearlyTotal =
          fragmentBundle.getFloat(Constants.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL, 0);
      allTimeValueTV.setText(NumberFormat.getNumberInstance(Locale.US)
          .format((int) Double.parseDouble(String.valueOf(slugBreakdownYearlyTotal)))
          + " "
          + measurementUnitForMetric);

      hookupListWithItems(slugBreakdownData, measurementUnitForMetric);
    }

    return fragmentView;
  }

  private void hookupListWithItems(ArrayList<Parcelable> items, String measurementUnitForMetric) {
    final SlugBreakdown_ListAdapter slugBreakdownListAdapter =
        new SlugBreakdown_ListAdapter(getActivity(), items, measurementUnitForMetric);
    slugBreakdownListView.setAdapter(slugBreakdownListAdapter);

    if (typeOfFragmentToCustomizeFor.equals(Constants.EXTRAS_NUTRITION_FRAGMENT)) {
      slugBreakdownListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          //                    Log.d("TAG", "onItemClick: Item selected date : " + ((TextView) view.findViewById(R.id.slugDateTV)).getText().toString());
          //
          //                    DateTime entryDate = new DateTime(slugBreakdownListAdapter.getItem(i).getDate());
          //
          //                    Log.d("TAG", "onItemClick: " + (entryDate.getDayOfMonth() + " " + entryDate.monthOfYear().getAsText() + ", " + entryDate.getYear()));

          Intent intent = new Intent(getActivity(), AddNewMealOnDateActivity.class);
          intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON,
              slugBreakdownListAdapter.getItem(i).getDate());
          startActivity(intent);
        }
      });
    }
  }

  public class SlugBreakdown_ListAdapter extends BaseAdapter {

    private ArrayList<Parcelable> slugBreakdownData;
    private Context context;
    private String measurementUnit;

    public SlugBreakdown_ListAdapter(Context context, ArrayList<Parcelable> slugBreakdownData,
        String measurementUnit) {
      super();
      this.context = context;
      this.slugBreakdownData = slugBreakdownData;
      this.measurementUnit = measurementUnit;
    }

    @Override public int getCount() {
      return slugBreakdownData.size();
    }

    @Override public SlugBreakdownResponseDaily getItem(int index) {
      return (SlugBreakdownResponseDaily) slugBreakdownData.get(index);
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

        holder.slugDateTV = (TextView) convertView.findViewById(R.id.slugDateTV);
        holder.slugTotalTV = (TextView) convertView.findViewById(R.id.slugTotalTV);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      DateTime entryDate = new DateTime(getItem(position).getDate());

      holder.slugDateTV.setText(entryDate.getDayOfMonth()
          + " "
          + entryDate.monthOfYear().getAsText()
          + ", "
          + entryDate.getYear());

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