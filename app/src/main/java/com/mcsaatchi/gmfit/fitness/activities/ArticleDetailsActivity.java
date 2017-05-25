package com.mcsaatchi.gmfit.fitness.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.squareup.picasso.Picasso;

public class ArticleDetailsActivity extends BaseActivity {
  @Bind(R.id.articleImageIV) ImageView articleImageIV;
  @Bind(R.id.articleTitleTV) TextView articleTitleTV;
  @Bind(R.id.articleDateTV) TextView articleDateTV;
  @Bind(R.id.articleContentsTV) TextView articleContentsTV;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_article_details);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      ArticlesResponseBody articlesResponseBody = getIntent().getExtras().getParcelable("ARTICLE");

      setupToolbar(getClass().getSimpleName(), toolbar, articlesResponseBody.getTitle(), true);

      Picasso.with(this).load(articlesResponseBody.getImage()).into(articleImageIV);
      articleTitleTV.setText(articlesResponseBody.getTitle());
      articleDateTV.setText(articlesResponseBody.getDatePublishing());
      articleContentsTV.setText(articlesResponseBody.getContent());
    }
  }
}
