package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponseYearly;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlugBreakdown_Fragment_Yearly extends Fragment {
    @Bind(R.id.slugBreakdownListView)
    ListView slugBreakdownListView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_slug_breakdown_yearly, null);

        ButterKnife.bind(this, fragmentView);

        Bundle fragmentBundle = getArguments();

        if (fragmentBundle != null) {
            ArrayList<Parcelable> slugBreakdownData = fragmentBundle.getParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY);

            hookupListWithItems(slugBreakdownData);
        }


        return fragmentView;
    }

    private void hookupListWithItems(ArrayList<Parcelable> items) {
        SlugBreakdown_ListAdapter slugBreakdownListAdapter = new SlugBreakdown_ListAdapter(getActivity(), items);
        slugBreakdownListView.setAdapter(slugBreakdownListAdapter);
    }

    public class SlugBreakdown_ListAdapter extends BaseAdapter {

        private ArrayList<Parcelable> slugBreakdownData;
        private Context context;

        public SlugBreakdown_ListAdapter(Context context, ArrayList<Parcelable> slugBreakdownData) {
            super();
            this.context = context;
            this.slugBreakdownData = slugBreakdownData;
        }

        @Override
        public int getCount() {
            return slugBreakdownData.size();
        }

        @Override
        public SlugBreakdownResponseYearly getItem(int index) {
            return (SlugBreakdownResponseYearly) slugBreakdownData.get(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_slug_yearly_breakdown, parent,
                        false);

                holder = new ViewHolder();

                holder.slugDateTV = (TextView) convertView.findViewById(R.id.slugDateTV);
                holder.slugTotalTV = (TextView) convertView.findViewById(R.id.slugTotalTV);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.slugDateTV.setText(getItem(position).getDate());
            holder.slugTotalTV.setText(getItem(position).getTotal());

            return convertView;
        }

        class ViewHolder {
            TextView slugDateTV;
            TextView slugTotalTV;
        }
    }
}