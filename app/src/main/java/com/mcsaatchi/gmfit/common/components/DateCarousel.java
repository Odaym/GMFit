package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateCarousel extends HorizontalScrollView {

  @Bind(R.id.dateCarousel) HorizontalScrollView dateCarousel;
  @Bind(R.id.dateCarouselContainer) LinearLayout dateCarouselContainer;
  private LocalDate dt;
  private LayoutInflater mInflater;
  private ArrayList<CarouselClickListener> clickListeners = new ArrayList<>();

  public DateCarousel(Context context, AttributeSet attrs) {
    super(context, attrs);

    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.date_carousel, this, true);

    ButterKnife.bind(this, v);

    setupDateCarousel();
  }

  public void addClickListener(CarouselClickListener listener) {
    this.clickListeners.add(listener);
  }

  private void setupDateCarousel() {
    dt = new LocalDate();

    int daysExtraToShow = 3;

    LocalDate dateToStartFrom = dt.plusDays(daysExtraToShow);

    for (int i = Constants.NUMBER_OF_DAYS_IN_DATE_CAROUSEL; i >= 0; i--) {
      View itemDateCarouselLayout = mInflater.inflate(R.layout.item_date_carousel, null);
      itemDateCarouselLayout.setPadding(
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0,
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0);

      final TextView dayOfMonthTV =
          (TextView) itemDateCarouselLayout.findViewById(R.id.dayOfMonthTV);
      final TextView monthOfYearTV =
          (TextView) itemDateCarouselLayout.findViewById(R.id.monthOfYearTV);

      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      dayOfMonthTV.setTypeface(null, Typeface.NORMAL);
      monthOfYearTV.setTypeface(null, Typeface.NORMAL);

      LocalDate dateAsLocal = dateToStartFrom.minusDays(i);
      DateTimeFormatter monthFormatter = DateTimeFormat.forPattern("MMM");

      dayOfMonthTV.setText(String.valueOf(dateAsLocal.getDayOfMonth()));
      monthOfYearTV.setText(String.valueOf(monthFormatter.print(dateAsLocal).toUpperCase()));

      dateCarouselContainer.addView(itemDateCarouselLayout);

      if (i == daysExtraToShow) {
        focusOnView(dateCarouselContainer, itemDateCarouselLayout);
      }

      if (i < daysExtraToShow) {
        fadeOutView(itemDateCarouselLayout);
      } else if (i >= daysExtraToShow) {
        itemDateCarouselLayout.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            focusOnView(dateCarouselContainer, view);

            DateTimeFormatter formatterForDisplay = DateTimeFormat.forPattern("dd/MMM/yyyy");

            DateTime formattedDate = formatterForDisplay.parseDateTime(
                dayOfMonthTV.getText().toString()
                    + "/"
                    + monthOfYearTV.getText().toString()
                    + "/"
                    + dt.year().getAsText());

            String finalDesiredDate = Helpers.prepareDateForAPIRequest(formattedDate.toLocalDate());

            String todaysDateFormatted =
                dt.getYear() + "-" + dt.getMonthOfYear() + "-" + dt.getDayOfMonth();
            //
            //String finalDesiredDate =
            //    formattedDate.getYear() + "-" + formattedDate.getMonthOfYear() + "-" + formattedDate
            //        .getDayOfMonth();

            for (CarouselClickListener listener : clickListeners) {
              listener.handleClick(todaysDateFormatted, finalDesiredDate);
            }
          }
        });
      }
    }
  }

  private void focusOnView(LinearLayout dateCarouselContainer, final View view) {
    TextView dayOfMonthTV;
    TextView monthOfYearTV;
    LinearLayout dateEntryLayout;

    for (int i = 0; i < dateCarouselContainer.getChildCount(); i++) {
      dateEntryLayout =
          (LinearLayout) dateCarouselContainer.getChildAt(i).findViewById(R.id.dateEntryLayout);
      dayOfMonthTV = (TextView) dateCarouselContainer.getChildAt(i).findViewById(R.id.dayOfMonthTV);
      monthOfYearTV =
          (TextView) dateCarouselContainer.getChildAt(i).findViewById(R.id.monthOfYearTV);

      dateEntryLayout.setBackgroundColor(0);
      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      dayOfMonthTV.setTypeface(null, Typeface.NORMAL);
      monthOfYearTV.setTypeface(null, Typeface.NORMAL);
    }

    dateEntryLayout = (LinearLayout) view.findViewById(R.id.dateEntryLayout);
    dayOfMonthTV = (TextView) view.findViewById(R.id.dayOfMonthTV);
    monthOfYearTV = (TextView) view.findViewById(R.id.monthOfYearTV);

    dateEntryLayout.setBackgroundColor(getResources().getColor(R.color.offwhite_transparent));
    dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    dayOfMonthTV.setTypeface(null, Typeface.BOLD);
    monthOfYearTV.setTypeface(null, Typeface.BOLD);
  }

  private void fadeOutView(View view) {
    view.setAlpha(0.5f);
  }

  public interface CarouselClickListener {
    void handleClick(String todayDate, String finalDate);
  }
}
