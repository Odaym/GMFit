package com.mcsaatchi.gmfit.fitness.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticleDetailsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ArticleDetailsActivity extends BaseActivity
    implements ArticleDetailsActivityPresenter.ArticleDetailsActivityView {
  @Bind(R.id.articleImageIV) ImageView articleImageIV;
  @Bind(R.id.articleTitleTV) TextView articleTitleTV;
  @Bind(R.id.articleDateTV) TextView articleDateTV;
  @Bind(R.id.articleContentsTV) TextView articleContentsTV;
  @Bind(R.id.headerLayout) LinearLayout headerLayout;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_article_details);

    ButterKnife.bind(this);

    ArticleDetailsActivityPresenter presenter =
        new ArticleDetailsActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      ArticlesResponseBody articlesResponseBody = getIntent().getExtras().getParcelable("ARTICLE");

      String section = getIntent().getExtras().getString("SECTION");

      if (section != null) {
        switch (section) {
          case "fitness":
            headerLayout.setBackgroundResource(R.drawable.fitness_background);
            break;
          case "nutrition":
            headerLayout.setBackgroundResource(R.drawable.nutrition_background);
            break;
          case "medical":
            headerLayout.setBackgroundResource(R.drawable.health_background);
            break;
        }
      }

      if (articlesResponseBody != null) {
        presenter.getArticleDetails(
            Constants.BASE_URL_ADDRESS + "articles/" + articlesResponseBody.getId());
      }
    }
  }

  @Override
  public void displayArticleDetails(ArticleDetailsResponseBody articleDetailsResponseBody) {

    setupToolbar(getClass().getSimpleName(), toolbar, articleDetailsResponseBody.getTitle(), true);

    Picasso.with(this).load(articleDetailsResponseBody.getImage()).into(articleImageIV);

    articleTitleTV.setText(articleDetailsResponseBody.getTitle());

    try {
      articleDateTV.setText(new SimpleDateFormat("dd MMMM, yyyy").format(
          new SimpleDateFormat("yyyy-MM-dd").parse(
              articleDetailsResponseBody.getDatePublishing().split(" ")[0])));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      articleContentsTV.setText(
          Html.fromHtml(articleDetailsResponseBody.getContent(), Html.FROM_HTML_MODE_COMPACT));
    } else {
      if (articleDetailsResponseBody.getContent() != null) {
        articleContentsTV.setText(Html.fromHtml(articleDetailsResponseBody.getContent()));
      }
    }
  }
}
