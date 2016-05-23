package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;

import com.andreabaccega.widget.FormEditText;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mcsaatchi.gmfit.R;

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
            bundle.putInt(Cons.BUNDLE_ACTIVITY_TITLE, activityTitleResourceId);
        }

        bundle.putBoolean(Cons.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

        return bundle;
    }

    public static Bundle createActivityBundleWithProperties(String activityTitle, boolean enableBackButton) {
        Bundle bundle = new Bundle();

        if (activityTitle != null) {
            bundle.putString(Cons.BUNDLE_ACTIVITY_TITLE, activityTitle);
        }

        bundle.putBoolean(Cons.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

        return bundle;
    }

    public static void setChartData(BarChart chart, int xLimits, int yLimits) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < yLimits + 1; i++) {
            float mult = (yLimits + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new BarEntry((int) val1, i));
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < xLimits + 1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + "");
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Legend");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setBarShadowColor(R.color.bpblack);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription("");
        chart.setData(data);
        chart.invalidate();
    }

    public static void setUpDecoViewArc(Context ctx, DecoView dynamicArc) {
        SeriesItem seriesItem1 = new SeriesItem.Builder(ctx.getResources().getColor(android.R.color.holo_orange_dark))
                .setRange(0, 100, 0)
                .setSpinDuration(2500)
                .setInterpolator(new BounceInterpolator())
                .setLineWidth(35f)
                .build();

        int series1Index = dynamicArc.addSeries(seriesItem1);

        dynamicArc.addEvent(new DecoEvent.Builder(75).setIndex(series1Index).build());
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
