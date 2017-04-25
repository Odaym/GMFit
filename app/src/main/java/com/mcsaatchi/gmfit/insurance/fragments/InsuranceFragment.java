package com.mcsaatchi.gmfit.insurance.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.NonSwipeableViewPager;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InsuranceFragment extends BaseFragment
    implements InsuranceFragmentPresenter.InsuranceFragmentView {
  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Bind(R.id.pager) NonSwipeableViewPager pager;
  @Bind(R.id.tabs) TabLayout tabs;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    InsuranceFragmentPresenter presenter = new InsuranceFragmentPresenter(this, dataAccessHandler);

    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(R.string.insurance_section_title);
    }

    String userName = prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, "");
    String userPassword = prefs.getString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, "");

    setupViewPager(pager);
    tabs.setupWithViewPager(pager);

    if (!userName.isEmpty() && !userPassword.isEmpty()) {
      presenter.login(userName, prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""),
          "2", userPassword);
    }

    return fragmentView;
  }

  @Override
  public void openHomeFragment(InsuranceLoginResponseInnerData userObject, String username) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(Constants.BUNDLE_INSURANCE_USER_OBJECT, userObject);
    bundle.putString("CARD_NUMBER", username);
    InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
    insuranceHomeFragment.setArguments(bundle);

    getChildFragmentManager().beginTransaction()
        .replace(R.id.root_frame, insuranceHomeFragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        .commitAllowingStateLoss();
  }

  private void setupViewPager(ViewPager viewPager) {
    InsuranceEntryPagerAdapter adapter = new InsuranceEntryPagerAdapter(getChildFragmentManager());
    adapter.addFragmentTitle("Insurance");
    adapter.addFragmentTitle("Directory");
    viewPager.setAdapter(adapter);
  }

  private class InsuranceEntryPagerAdapter extends FragmentPagerAdapter {
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
      return fragmentTitleList.size();
    }

    void addFragmentTitle(String title) {
      fragmentTitleList.add(title);
    }

    @Override public CharSequence getPageTitle(int position) {
      return fragmentTitleList.get(position);
    }
  }
}
