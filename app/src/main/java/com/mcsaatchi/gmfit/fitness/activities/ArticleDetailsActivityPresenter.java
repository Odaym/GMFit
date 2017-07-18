package com.mcsaatchi.gmfit.fitness.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticleDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticleDetailsResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
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

  void getArticleDetails(String fullUrl) {
    dataAccessHandler.getArticleDetails(fullUrl, new Callback<ArticleDetailsResponse>() {
      @Override
      public void onResponse(Call<ArticleDetailsResponse> call, Response<ArticleDetailsResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayArticleDetails(response.body().getData().getBody().get(0).get(0));
            break;
        }
      }

      @Override public void onFailure(Call<ArticleDetailsResponse> call, Throwable t) {
      }
    });
  }

  interface ArticleDetailsActivityView extends BaseActivityPresenter.BaseActivityView {
    void displayArticleDetails(ArticleDetailsResponseBody articleDetailsResponseBody);
  }
}
