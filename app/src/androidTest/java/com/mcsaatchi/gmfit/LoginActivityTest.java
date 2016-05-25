package com.mcsaatchi.gmfit;

import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.mcsaatchi.gmfit.activities.Login_Activity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<Login_Activity> {

    private Login_Activity loginActivity;

    public LoginActivityTest() {
        super(Login_Activity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();
    }

    public void testViewPagerNotNull(){
        ViewPager viewPager = (ViewPager) loginActivity.findViewById(R.id.viewpager);

        assertNotNull(viewPager);
    }

    public void testIndicatorControllerNotNull(){
        FrameLayout indicatorController = (FrameLayout) loginActivity.findViewById(R.id.indicator_container);

        assertNotNull(indicatorController);
    }

    public void testSignInButtonsLayoutNotNull(){
        LinearLayout signInButtonsLayout = (LinearLayout) loginActivity.findViewById(R.id.signInButtonsLayout);

        assertNotNull(signInButtonsLayout);
    }

    public void testFacebookLoginBTNNotNull(){
        LoginButton facebookLoginBTN = (LoginButton) loginActivity.findViewById(R.id.loginFacebookBTN);

        assertNotNull(facebookLoginBTN);
    }

    public void testTestGoogleLoginBTNNotNull(){
        SignInButton googleLoginBTN = (SignInButton) loginActivity.findViewById(R.id.loginGoogleBTN);

        assertNotNull(googleLoginBTN);
    }

    public void testSignUpBTNNotNull(){
        Button signUpBTN = (Button) loginActivity.findViewById(R.id.signUpBTN);

        assertNotNull(signUpBTN);
    }

    public void testAlreadySignedUpTVNotNull(){
        TextView alreadySignedUpTV = (TextView) loginActivity.findViewById(R.id.alreadySignedUpTV);

        assertNotNull(alreadySignedUpTV);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
