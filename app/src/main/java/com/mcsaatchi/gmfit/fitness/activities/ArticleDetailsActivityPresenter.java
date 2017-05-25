package com.mcsaatchi.gmfit.fitness.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailsActivityPresenter {
  private ArticleDetailsActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ArticleDetailsActivityPresenter(ArticleDetailsActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getArticles(String sectionName) {
    dataAccessHandler.getArticles(sectionName, new Callback<ArticlesResponse>() {
      @Override
      public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateArticles(response.body().getData().getBody());
            break;
        }
      }

      @Override public void onFailure(Call<ArticlesResponse> call, Throwable t) {
      }
    });
  }

  interface ArticleDetailsActivityView extends BaseActivityPresenter.BaseActivityView {
    void populateArticles(List<ArticlesResponseBody> articlesResponseBodies);
  }
}
