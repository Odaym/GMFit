package com.mcsaatchi.gmfit.insurance.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.NonSwipeableViewPager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InsuranceFragment extends Fragment {
  @Bind(R.id.pager) NonSwipeableViewPager pager;
  @Bind(R.id.tabs) TabLayout tabs;
  @Bind(R.id.fragment_container) FrameLayout fragment_container;
  @Bind(R.id.parentLayoutToCustomize) LinearLayout parentLayoutToCustomize;
  @Bind(R.id.contractChooserBTN) ImageView contractChooserBTN;
  @Bind(R.id.switchMapViewBTN) ImageView switchMapViewBTN;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(R.string.insurance_section_title);
    }

    String userName = prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, "");
    String userPassword = prefs.getString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, "");

    setupViewPager(pager);
    tabs.setupWithViewPager(pager);

    if (!userName.isEmpty() && !userPassword.isEmpty()) {
      insuranceUserLogin(userName, userPassword);
    }

    return fragmentView;
  }

  private void setupViewPager(ViewPager viewPager) {
    InsuranceEntryPagerAdapter adapter = new InsuranceEntryPagerAdapter(getChildFragmentManager());
    adapter.addFragmentTitle("Insurance");
    adapter.addFragmentTitle("Directory");
    viewPager.setAdapter(adapter);
  }

  private void insuranceUserLogin(final String username, String password) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();

      if (waitingDialog.isShowing()) waitingDialog.dismiss();
    });

    dataAccessHandler.insuranceUserLogin(username,
        prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""), "2", password,
        new Callback<InsuranceLoginResponse>() {
          @Override public void onResponse(Call<InsuranceLoginResponse> call,
              Response<InsuranceLoginResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_INSURANCE_USER_OBJECT,
                    response.body().getData().getBody().getData());
                bundle.putString("CARD_NUMBER", username);
                InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
                insuranceHomeFragment.setArguments(bundle);

                getChildFragmentManager().beginTransaction()
                    .replace(R.id.root_frame, insuranceHomeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commitAllowingStateLoss();

                break;
            }
          }

          @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private class InsuranceEntryPagerAdapter extends FragmentPagerAdapter {
    private final List<String> fragmentTitleList = new ArrayList<>();

    InsuranceEntryPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        switchMapViewBTN.setVisibility(View.GONE);
        return new InsuranceRootFragment();
      } else {
        switchMapViewBTN.setVisibility(View.VISIBLE);
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
