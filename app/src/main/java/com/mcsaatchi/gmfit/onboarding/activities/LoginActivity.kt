package com.mcsaatchi.gmfit.onboarding.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.FrameLayout
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.mcsaatchi.gmfit.R
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChart
import com.mcsaatchi.gmfit.common.Constants
import com.mcsaatchi.gmfit.common.activities.BaseActivity
import com.mcsaatchi.gmfit.common.activities.MainActivity
import com.mcsaatchi.gmfit.common.classes.DefaultIndicatorController
import com.mcsaatchi.gmfit.common.classes.Helpers
import com.mcsaatchi.gmfit.onboarding.fragments.IntroSliderFragment
import com.squareup.otto.Subscribe
import io.fabric.sdk.android.Fabric
import java.util.*

class LoginActivity : BaseActivity(), LoginActivityPresenter.LoginActivityView {

  @Bind(R.id.loginFacebookBTN) internal var loginFacebookBTN: LoginButton? = null
  @Bind(R.id.viewpager) internal var viewPager: ViewPager? = null

  private var indicatorController: DefaultIndicatorController? = null
  private var presenter: LoginActivityPresenter? = null
  private var callbackManager: CallbackManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(Helpers.createActivityBundleWithProperties(0, false))
    Fabric.with(this, Crashlytics())

    FacebookSdk.sdkInitialize(this)
    callbackManager = CallbackManager.Factory.create()

    setContentView(R.layout.activity_login)

    ButterKnife.bind(this)

    EventBusSingleton.getInstance().register(this)

    presenter = LoginActivityPresenter(this, dataAccessHandler)

    initializeFacebookLogin()

    setupViewPager()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager!!.onActivityResult(requestCode, resultCode, data)
  }

  @OnClick(R.id.signInBTN) fun handleSignInClicked() {
    val intent = Intent(this@LoginActivity, SignInActivity::class.java)
    startActivity(intent)
  }

  @OnClick(R.id.signUpBTN) fun handleSignUpClicked() {
    val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
    startActivity(intent)
  }

  @Subscribe fun handleSuccessfulSignUp(event: SignedUpSuccessfullyEvent) {
    finish()
  }

  @Subscribe fun handleSuccessfulSignIn(event: SignedInSuccessfullyEvent) {
    finish()
  }

  override fun openSetupProfileActivity() {
    val intent = Intent(this@LoginActivity, SetupProfileActivity::class.java)
    startActivity(intent)
    finish()
  }

  override fun saveFacebookAccessToken(accessToken: String) {
    prefs.edit().putString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, accessToken).apply()
  }

  override fun saveFacebookUserDetails(userID: String, userName: String, userEmail: String) {
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply()
    prefs.edit().putString(Constants.EXTRAS_USER_FULL_NAME, userName).apply()
    prefs.edit()
        .putString(Constants.EXTRAS_USER_DISPLAY_PHOTO,
            "https://graph.facebook.com/$userID/picture?type=large")
        .apply()
    prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, userEmail).apply()
  }

  override fun openMainActivity(chartsMap: List<AuthenticationResponseChart>) {
    val intent = Intent(this@LoginActivity, MainActivity::class.java)
    intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
        chartsMap as ArrayList<AuthenticationResponseChart>)
    startActivity(intent)
    finish()
  }

  override fun initializeFacebookLogin() {
    loginFacebookBTN!!.setReadPermissions("email", "public_profile", "user_friends")
    loginFacebookBTN!!.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    loginFacebookBTN!!.compoundDrawablePadding = 0
    loginFacebookBTN!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
      override fun onSuccess(loginResult: LoginResult) {
        val accessToken = loginResult.accessToken

        presenter!!.handleFacebookSuccessCallback(accessToken)
      }

      override fun onCancel() {}

      override fun onError(exception: FacebookException) {}
    })
  }

  override fun onDestroy() {
    super.onDestroy()

    EventBusSingleton.getInstance().unregister(this)
  }

  private fun setupViewPager() {
    viewPager!!.adapter = IntroAdapter(supportFragmentManager)

    viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

      }

      override fun onPageSelected(position: Int) {
        indicatorController!!.selectPosition(position)
      }

      override fun onPageScrollStateChanged(state: Int) {

      }
    })

    initController()
  }

  private fun initController() {
    if (indicatorController == null) indicatorController = DefaultIndicatorController()

    val indicatorContainer = findViewById(R.id.indicator_container) as FrameLayout
    indicatorContainer.addView(indicatorController!!.newInstance(this))

    indicatorController!!.initialize(7)
  }

  private inner class IntroAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(
      fm) {

    override fun getItem(position: Int): Fragment? {
      when (position) {
        0 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_1)
        1 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_2)
        2 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_3)
        3 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_4)
        4 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_5)
        5 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_6)
        6 -> return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_7)
        else -> return null
      }
    }

    override fun getCount(): Int {
      return 7
    }
  }
}