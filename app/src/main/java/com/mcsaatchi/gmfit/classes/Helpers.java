package com.mcsaatchi.gmfit.classes;

import android.os.Bundle;

import com.andreabaccega.widget.FormEditText;

import java.util.ArrayList;

public class Helpers {

    public static Helpers helpers = null;

    private Helpers() {
    }

    public static Helpers getInstance() {
        if (helpers == null) {
            helpers = new Helpers();
        }

        return helpers;
    }

    public static boolean validateFields(ArrayList<FormEditText> allFields) {
        boolean allValid = true;

        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        return allValid;
    }

    public static Bundle createActivityBundleWithProperties(int activityTitleResourceId, boolean enableBackButton) {
        Bundle bundle = new Bundle();

        if (activityTitleResourceId != 0) {
            bundle.putInt(Constants.BUNDLE_ACTIVITY_TITLE, activityTitleResourceId);
        }

        bundle.putBoolean(Constants.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

        return bundle;
    }

//    public static void linkifyString (final Context context, TextView tv, int stringResourceId, int startIndex, int
//            endIndex){
//        SpannableString ss = new SpannableString(context.getString(stringResourceId));
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                Log.toaster(context, "Implement forgot password logic");
//            }
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(false);
//            }
//        };
//
//        ss.setSpan(clickableSpan, startIndex, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        tv.setText(ss);
//        tv.setMovementMethod(LinkMovementMethod.getInstance());
//        tv.setHighlightColor(Color.TRANSPARENT);
//    }
}
