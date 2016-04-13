package com.mcsaatchi.gmfit.activities;

import android.graphics.Color;
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
import com.mcsaatchi.gmfit.logger.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPassword_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.didntReceivePasswordEmailTV)
    TextView didntReceivePasswordEmailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt("activity_title", R.string.forgot_password_activity_title);

        super.onCreate(bundle);

        setContentView(R.layout.forgot_password_activity);

        ButterKnife.bind(this);

        SpannableString ss = new SpannableString(getString(R.string.didnt_receive_forgot_password_email));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.toaster(ForgotPassword_Activity.this, "Handle forgot password logic");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 29, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        didntReceivePasswordEmailTV.setText(ss);
        didntReceivePasswordEmailTV.setMovementMethod(LinkMovementMethod.getInstance());
        didntReceivePasswordEmailTV.setHighlightColor(Color.TRANSPARENT);
    }
}
