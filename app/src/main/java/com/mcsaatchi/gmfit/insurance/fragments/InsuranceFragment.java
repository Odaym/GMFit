package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import java.util.ArrayList;
import java.util.List;

public class InsuranceFragment extends Fragment {
  @Bind(R.id.pager) ViewPager pager;
  @Bind(R.id.tabs) TabLayout tabs;
  @Bind(R.id.fragment_container) FrameLayout fragment_container;
  @Bind(R.id.parentLayoutToCustomize) LinearLayout parentLayoutToCustomize;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance, container, false);

    ButterKnife.bind(this, fragmentView);

    setupViewPager(pager);
    tabs.setupWithViewPager(pager);

    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(R.string.insurance_section_title);
    }

    return fragmentView;
  }

  private void setupViewPager(ViewPager viewPager) {
    InsuranceEntryPagerAdapter adapter = new InsuranceEntryPagerAdapter(getChildFragmentManager());
    adapter.addFragment(new InsuranceLoginFragment(), "Insurance");
    adapter.addFragment(new InsuranceLoginFragment(), "Directory");
    viewPager.setAdapter(adapter);
  }

  class InsuranceEntryPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    InsuranceEntryPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        return new InsuranceRootFragment();
      } else {
        return new InsuranceDirectoryFragment();
      }
    }

    @Override public int getCount() {
      return fragmentList.size();
    }

    void addFragment(Fragment fragment, String title) {
      fragmentList.add(fragment);
      fragmentTitleList.add(title);
    }

    @Override public CharSequence getPageTitle(int position) {
      return fragmentTitleList.get(position);
    }
  }
}
