package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

  @Bind(R.id.dateCarousel) ScrollView dateCarousel;
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

  public void setupDateCarousel() {
    dt = new LocalDate();

    dateCarousel.setVerticalScrollBarEnabled(false);
    dateCarousel.setHorizontalScrollBarEnabled(false);

    dateCarousel.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        getResources().getDimensionPixelSize(R.dimen.date_carousel_height)));

    int daysExtraToShow = 3;

    LocalDate dateToStartFrom = dt.plusDays(daysExtraToShow);

    for (int i = Constants.NUMBER_OF_DAYS_IN_DATE_CAROUSEL; i >= 0; i--) {
      View itemDateCarouselLayout = mInflater.inflate(R.layout.item_date_carousel, null);
      itemDateCarouselLayout.setPadding(
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0,
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0);

      final TextView dayOfMonthTV = itemDateCarouselLayout.findViewById(R.id.dayOfMonthTV);
      final TextView monthOfYearTV = itemDateCarouselLayout.findViewById(R.id.monthOfYearTV);

      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
          getResources().getDimension(R.dimen.date_carousel_day_of_month_unfocused_textsize));
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
          getResources().getDimension(R.dimen.date_carousel_month_of_year_unfocused_textsize));

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
        itemDateCarouselLayout.setOnClickListener(view -> {
          focusOnView(dateCarouselContainer, view);

          DateTimeFormatter formatterForDisplay = DateTimeFormat.forPattern("dd/MMM/yyyy");

          DateTime formattedDate = formatterForDisplay.parseDateTime(
              dayOfMonthTV.getText().toString()
                  + "/"
                  + monthOfYearTV.getText().toString()
                  + "/"
                  + dt.year().getAsText());

          String finalDesiredDate = Helpers.formatDateToDefault(formattedDate.toLocalDate());

          String todaysDateFormatted =
              dt.getYear() + "-" + dt.getMonthOfYear() + "-" + dt.getDayOfMonth();
          //
          //String finalDesiredDate =
          //    formattedDate.getYear() + "-" + formattedDate.getMonthOfYear() + "-" + formattedDate
          //        .getDayOfMonth();

          for (CarouselClickListener listener : clickListeners) {
            listener.handleClick(todaysDateFormatted, finalDesiredDate);
          }
        });
      }
    }
  }

  public void focusOnView(LinearLayout dateCarouselContainer, final View view) {
    TextView dayOfMonthTV;
    TextView monthOfYearTV;
    ImageView indicatorArrowIV;
    LinearLayout dateEntryLayout;

    for (int i = 0; i < dateCarouselContainer.getChildCount(); i++) {
      dateEntryLayout = dateCarouselContainer.getChildAt(i).findViewById(R.id.dateEntryLayout);
      dayOfMonthTV = dateCarouselContainer.getChildAt(i).findViewById(R.id.dayOfMonthTV);
      monthOfYearTV = dateCarouselContainer.getChildAt(i).findViewById(R.id.monthOfYearTV);
      indicatorArrowIV = dateCarouselContainer.getChildAt(i).findViewById(R.id.indicatorArrowIV);

      dateEntryLayout.setBackgroundColor(0);
      dateEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
          getResources().getDimensionPixelSize(R.dimen.date_carousel_unfocused_item_cell_width),
          ViewGroup.LayoutParams.MATCH_PARENT));

      indicatorArrowIV.setVisibility(View.GONE);
      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
          getResources().getDimension(R.dimen.date_carousel_day_of_month_unfocused_textsize));
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
          getResources().getDimension(R.dimen.date_carousel_month_of_year_unfocused_textsize));

      dayOfMonthTV.setPadding(0, 40, 0, 20);

      monthOfYearTV.setPadding(0, 0, 0, 20);

      dayOfMonthTV.setTypeface(null, Typeface.NORMAL);
      monthOfYearTV.setTypeface(null, Typeface.NORMAL);
    }

    dateEntryLayout = view.findViewById(R.id.dateEntryLayout);
    dayOfMonthTV = view.findViewById(R.id.dayOfMonthTV);
    monthOfYearTV = view.findViewById(R.id.monthOfYearTV);
    indicatorArrowIV = view.findViewById(R.id.indicatorArrowIV);

    dateEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
        getResources().getDimensionPixelSize(R.dimen.date_carousel_focused_item_cell_width),
        ViewGroup.LayoutParams.WRAP_CONTENT));

    indicatorArrowIV.setVisibility(View.VISIBLE);
    dateEntryLayout.setBackgroundColor(getResources().getColor(R.color.offwhite_transparent));
    dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
        getResources().getDimension(R.dimen.date_carousel_day_of_month_focused_textsize));
    monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,
        getResources().getDimension(R.dimen.date_carousel_month_of_year_focused_textsize));

    dayOfMonthTV.setPadding(0, 0, 0, 0);

    monthOfYearTV.setPadding(0, 0, 0, 0);

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
