package com.mcsaatchi.gmfit.onboarding.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.UserFinalizedSetupProfileEvent;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.NonSwipeableViewPager;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile1Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile2Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile3Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile4Fragment;

public class SetupProfileActivity extends BaseActivity {
  @Bind(R.id.viewpager) NonSwipeableViewPager viewPager;
  @Bind(R.id.nextPageBTN) Button nextPageBTN;
  @Bind(R.id.toolbar) Toolbar toolbar;
  private SetupProfile_Adapter setupProfileAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_setup_profile);

    ButterKnife.bind(this);

    setupProfileAdapter = new SetupProfile_Adapter(getSupportFragmentManager());

    setupToolbar(toolbar, getResources().getString(R.string.setup_profile_step_1_title), true);

    setupViewPager(setupProfileAdapter);
  }

  private void setupViewPager(final SetupProfile_Adapter setupProfileAdapter) {
    viewPager.setAdapter(setupProfileAdapter);
    viewPager.setOffscreenPageLimit(3);

    viewPager.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
      }
    });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        switch (position) {
          case 0:
            toolbar.setTitle(getString(R.string.setup_profile_step_1_title));
            nextPageBTN.setText(getString(R.string.next_step));
            break;
          case 1:
            toolbar.setTitle(getString(R.string.setup_profile_step_2_title));
            nextPageBTN.setText(getString(R.string.next_step));
            break;
          case 2:
            toolbar.setTitle(getString(R.string.setup_profile_step_3_title));
            nextPageBTN.setText(getString(R.string.next_step));
            break;
          case 3:
            toolbar.setTitle(getString(R.string.setup_profile_step_4_title));
            nextPageBTN.setText(getString(R.string.finish_setup));
            break;
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });

    nextPageBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        switch (viewPager.getCurrentItem()) {
          case 1:
            if (setupProfileAdapter.getRegisteredFragment(viewPager.getCurrentItem()) != null) {
              /**
               * If no data was selected
               */
              if (!((SetupProfile2Fragment) setupProfileAdapter.getRegisteredFragment(
                  viewPager.getCurrentItem())).wasDataSelected()) {

                Toast.makeText(SetupProfileActivity.this, "Please make a choice to proceed",
                    Toast.LENGTH_SHORT).show();
              } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
              }
            }
            break;
          case 2:
            if (setupProfileAdapter.getRegisteredFragment(viewPager.getCurrentItem()) != null) {
              /**
               * If no data was selected
               */
              if (!((SetupProfile3Fragment) setupProfileAdapter.getRegisteredFragment(
                  viewPager.getCurrentItem())).wasDataSelected()) {

                Toast.makeText(SetupProfileActivity.this, "Please make a choice to proceed",
                    Toast.LENGTH_SHORT).show();
              } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
              }
            }
            break;
          case 3:
            if (setupProfileAdapter.getRegisteredFragment(viewPager.getCurrentItem()) != null) {
              /**
               * If no data was selected
               */
              if (((SetupProfile4Fragment) setupProfileAdapter.getRegisteredFragment(
                  viewPager.getCurrentItem())).getFinalHeight() == 0
                  || ((SetupProfile4Fragment) setupProfileAdapter.getRegisteredFragment(
                  viewPager.getCurrentItem())).getFinalWeight() == 0) {

                Toast.makeText(SetupProfileActivity.this,
                    "Please fill in your Height and Weight to proceed", Toast.LENGTH_SHORT).show();
              } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
              }
            }

            break;
          default:
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }

        /**
         * Time for submission
         */
        if (nextPageBTN.getText().toString().equals(getString(R.string.finish_setup))) {
          if (Helpers.isInternetAvailable(SetupProfileActivity.this)) {
            EventBusSingleton.getInstance()
                .post(new UserFinalizedSetupProfileEvent());
          } else {
            Helpers.showNoInternetDialog(SetupProfileActivity.this);
          }
        }
      }
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        break;
    }

    return false;
  }

  public class SetupProfile_Adapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SetupProfile_Adapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new SetupProfile1Fragment();
        case 1:
          return new SetupProfile2Fragment();
        case 2:
          return new SetupProfile3Fragment();
        case 3:
          return new SetupProfile4Fragment();
        default:
          return null;
      }
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
      Fragment fragment = (Fragment) super.instantiateItem(container, position);
      registeredFragments.put(position, fragment);
      return fragment;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      registeredFragments.remove(position);
      super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
      return registeredFragments.get(position);
    }

    @Override public int getCount() {
      return 4;
    }
  }
}