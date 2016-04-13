package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.DefaultIndicator_Controller;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Login_Activity extends Base_Activity {

    private DefaultIndicator_Controller indicatorController;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.loginFacebookBTN)
    Button loginFacebookBTN;
    @Bind (R.id.signUpBTN)
    Button signUpBTN;
    @Bind(R.id.alreadySignedUpTV)
    TextView alreadySignedUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ButterKnife.bind(this);

        loginFacebookBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, Main_Activity.class);
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

        SpannableString ss = new SpannableString(getString(R.string.already_signed_up));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Login_Activity.this, SignIn_Activity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 16, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        alreadySignedUpTV.setText(ss);
        alreadySignedUpTV.setMovementMethod(LinkMovementMethod.getInstance());
        alreadySignedUpTV.setHighlightColor(Color.TRANSPARENT);

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