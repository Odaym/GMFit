package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.classes.DefaultIndicatorController;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.fragments.IntroSliderFragment;
import com.squareup.otto.Subscribe;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity
    implements LoginActivityPresenter.LoginActivityView {

  @Bind(R.id.loginFacebookBTN) LoginButton loginFacebookBTN;
  @Bind(R.id.viewpager) ViewPager viewPager;

  private DefaultIndicatorController indicatorController;
  private LoginActivityPresenter presenter;
  private CallbackManager callbackManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(Helpers.createActivityBundleWithProperties(0, false));
    Fabric.with(this, new Crashlytics());

    FacebookSdk.sdkInitialize(this);

    callbackManager = CallbackManager.Factory.create();

    setContentView(R.layout.activity_login);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    presenter = new LoginActivityPresenter(this, dataAccessHandler);

    initializeFacebookLogin();

    setupViewPager();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @OnClick(R.id.signInBTN) public void handleSignInClicked() {
    Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.signUpBTN) public void handleSignUpClicked() {
    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
    startActivity(intent);
  }

  @Subscribe public void handleSuccessfulSignUp(SignedUpSuccessfullyEvent event) {
    finish();
  }

  @Subscribe public void handleSuccessfulSignIn(SignedInSuccessfullyEvent event) {
    finish();
  }

  @Override public void openSetupProfileActivity() {
    Intent intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
    startActivity(intent);
    finish();
  }

  @Override public void saveFacebookAccessToken(String accessToken) {
    prefs.edit().putString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, accessToken).apply();
  }

  @Override public void saveFacebookUserDetails(String userID, String userName, String userEmail) {
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_FULL_NAME, userName).apply();
    prefs.edit()
        .putString(Constants.EXTRAS_USER_DISPLAY_PHOTO,
            "https://graph.facebook.com/" + userID + "/picture?type=large")
        .apply();
    prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, userEmail).apply();
  }

  @Override public void openMainActivity(List<AuthenticationResponseChart> chartsMap) {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
        (ArrayList<AuthenticationResponseChart>) chartsMap);
    startActivity(intent);
    finish();
  }

  @Override public void initializeFacebookLogin() {
    loginFacebookBTN.setReadPermissions("email", "public_profile", "user_friends");
    loginFacebookBTN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    loginFacebookBTN.setCompoundDrawablePadding(0);
    loginFacebookBTN.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        final AccessToken accessToken = loginResult.getAccessToken();

        presenter.handleFacebookSuccessCallback(accessToken);
      }

      @Override public void onCancel() {
      }

      @Override public void onError(FacebookException exception) {
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    EventBusSingleton.getInstance().unregister(this);
  }

  private void setupViewPager() {
    viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        indicatorController.selectPosition(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });

    initController();
  }

  private void initController() {
    if (indicatorController == null) indicatorController = new DefaultIndicatorController();

    FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
    indicatorContainer.addView(indicatorController.newInstance(this));

    indicatorController.initialize(7);
  }

  private class IntroAdapter extends FragmentPagerAdapter {

    IntroAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_1);
        case 1:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_2);
        case 2:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_3);
        case 3:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_4);
        case 4:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_5);
        case 5:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_6);
        case 6:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_7);
        default:
          return null;
      }
    }

    @Override public int getCount() {
      return 7;
    }
  }
}