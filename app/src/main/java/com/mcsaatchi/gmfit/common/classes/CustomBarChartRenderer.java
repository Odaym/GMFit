package com.mcsaatchi.gmfit.common.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;

public class CustomBarChartRenderer extends BarChartRenderer {

  private Paint shadedPaint;

  public CustomBarChartRenderer(Context context, String whichFragment, BarDataProvider chart,
      ChartAnimator animator, ViewPortHandler viewPortHandler) {
    super(chart, animator, viewPortHandler);

    initBuffers();

    int height = chart.getHeight();

    shadedPaint = getPaintRender();

    int[] gradientColors = new int[2];

    switch (whichFragment) {
      case Constants.EXTRAS_FITNESS_FRAGMENT:
        gradientColors = new int[] {
            context.getResources().getColor(R.color.fitness_teal),
            context.getResources().getColor(R.color.fitness_pink)
        };
        break;
      case Constants.EXTRAS_NUTRITION_FRAGMENT:
        gradientColors = new int[] {
            context.getResources().getColor(R.color.nutrition_goals_pink),
            context.getResources().getColor(R.color.nutrition_red)
        };
        break;
    }

    LinearGradient linGrad =
        new LinearGradient(0, 0, 0, height, gradientColors, new float[]{0.1f, 1f}, Shader.TileMode.REPEAT);

    shadedPaint.setShader(linGrad);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

    float phaseX = mAnimator.getPhaseX();
    float phaseY = mAnimator.getPhaseY();

    // initialize the buffer
    BarBuffer buffer = mBarBuffers[index];
    buffer.setPhases(phaseX, phaseY);
    buffer.setBarSpace(dataSet.getBarSpace());
    buffer.setDataSet(index);
    buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

    buffer.feed(dataSet);

    trans.pointValuesToPixel(buffer.buffer);

    for (int j = 0; j < buffer.size(); j += 4) {

      if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) continue;

      if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break;

      int rad = 20;

      int bar_padding_bottom = 10;

      c.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
          buffer.buffer[j + 3] + bar_padding_bottom, rad, rad, shadedPaint);
    }
  }
}
