package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ResponsiveScrollView extends HorizontalScrollView {

    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 100;
    private static final String TAG = "MyScrollView";

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public ResponsiveScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollerTask = new Runnable() {

            public void run() {

                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {

                    if (onScrollStoppedListener != null) {

                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollY();
                    ResponsiveScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void setOnScrollStoppedListener(ResponsiveScrollView.OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask() {

        initialPosition = getScrollY();
        ResponsiveScrollView.this.postDelayed(scrollerTask, newCheck);
    }
}
