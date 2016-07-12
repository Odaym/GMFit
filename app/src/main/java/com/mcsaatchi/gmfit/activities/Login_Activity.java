package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.DefaultIndicator_Controller;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.fragments.IntroSlider_Fragment;
import com.mcsaatchi.gmfit.models.User;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Login_Activity extends Base_Activity {

    private static final int RC_SIGN_IN = 5;
    private static final String TAG = "Login_Activity";
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.loginFacebookBTN)
    LoginButton loginFacebookBTN;
    @Bind(R.id.signUpBTN)
    Button signUpBTN;
    @Bind(R.id.signInBTN)
    Button signInBTN;

    private DefaultIndicator_Controller indicatorController;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(0, false));
        Fabric.with(this, new Crashlytics());

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        EventBus_Singleton.getInstance().register(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Activity.this, SignIn_Activity.class);
                startActivity(intent);
            }
        });

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, SignUp_Activity.class);
                startActivity(intent);
            }
        });

        initializeFacebookLogin();

        setupViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus_Singleton.getInstance().unregister(this);
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY:
                finish();
                break;
        }
    }

    private void setupViewPager() {
        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorController.selectPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initController();
    }

//    private void hookUpAlreadySignedUpBTN() {
//        SpannableString ss = new SpannableString(getString(R.string.already_signed_up));
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                startActivity(new Intent(Login_Activity.this, SignIn_Activity.class));
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(false);
//            }
//        };
//
//        ss.setSpan(clickableSpan, 16, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        alreadySignedUpTV.setText(ss);
//        alreadySignedUpTV.setMovementMethod(LinkMovementMethod.getInstance());
//        alreadySignedUpTV.setHighlightColor(Color.TRANSPARENT);
//    }

    private void initializeFacebookLogin() {
        loginFacebookBTN.setReadPermissions("email", "public_profile", "user_friends");
        loginFacebookBTN.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(Login_Activity.this, "Facebook logged in successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login_Activity.this, Main_Activity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initController() {
        if (indicatorController == null)
            indicatorController = new DefaultIndicator_Controller();

        FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
        indicatorContainer.addView(indicatorController.newInstance(this));

        indicatorController.initialize(4);
    }

    public class IntroAdapter extends FragmentPagerAdapter {

        public IntroAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_1);
                case 1:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_2);
                case 2:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_3);
                case 3:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_4);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}