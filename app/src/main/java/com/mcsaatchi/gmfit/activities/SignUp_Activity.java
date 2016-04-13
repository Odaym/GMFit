package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.confirmPasswordET)
    FormEditText confirmPasswordET;
    @Bind(R.id.creatingAccountTOSTV)
    TextView creatingAccountTOSTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt("activity_title", R.string.sign_up_activity_title);

        super.onCreate(bundle);

        setContentView(R.layout.signup_activity);

        ButterKnife.bind(this);

        passwordET.setTypeface(Typeface.DEFAULT);
        confirmPasswordET.setTypeface(Typeface.DEFAULT);

        SpannableString ss = new SpannableString(getString(R.string.creating_account_TOS));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SignUp_Activity.this, TOS_Activity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 40, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        creatingAccountTOSTV.setText(ss);
        creatingAccountTOSTV.setMovementMethod(LinkMovementMethod.getInstance());
        creatingAccountTOSTV.setHighlightColor(Color.TRANSPARENT);
    }
}
