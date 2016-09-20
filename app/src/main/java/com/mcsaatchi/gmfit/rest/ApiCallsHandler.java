package com.mcsaatchi.gmfit.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.SlugBreakdown_Activity;
import com.mcsaatchi.gmfit.classes.Cons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallsHandler {

    private static ApiCallsHandler apiCallsHandler;
    private SharedPreferences prefs;

    private ApiCallsHandler(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Cannot pass Context to this constructor because if the activity gets destroyed and this is still holding a reference to it,
     * this will cause a memory leak
     *
     * @param prefs
     * @return
     */
    public static ApiCallsHandler getInstance(SharedPreferences prefs) {
        if (apiCallsHandler == null) {
            apiCallsHandler = new ApiCallsHandler(prefs);
        }

        return apiCallsHandler;
    }

    /**
     * So the Context gets passed here! :D
     *
     * Get the breakdown (Daily, Monthly and Yearly) for the specified chart
     *
     * @param context
     * @param chartTitle
     * @param chartType
     */
    public void getSlugBreakdownForChart(final Context context, final String chartTitle, final String chartType) {
        final ProgressDialog waitingDialog = new ProgressDialog(context);
        waitingDialog.setTitle(context.getResources().getString(R.string.grabbing_breakdown_data_dialog_title));
        waitingDialog.setMessage(context.getResources().getString(R.string.grabbing_breakdown_data_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.grabbing_breakdown_data_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<SlugBreakdownResponse> breakdownForSlugCall = new RestClient().getGMFitService().getBreakdownForSlug(Cons.BASE_URL_ADDRESS +
                "user/metrics/breakdown?slug=" + chartType, prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        breakdownForSlugCall.enqueue(new Callback<SlugBreakdownResponse>() {
            @Override
            public void onResponse(Call<SlugBreakdownResponse> call, Response<SlugBreakdownResponse> response) {
                switch (response.code()) {
                    case 200:

                        waitingDialog.dismiss();

                        Intent intent = new Intent(context, SlugBreakdown_Activity.class);
                        intent.putExtra(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, Cons.EXTRAS_FITNESS_FRAGMENT);
                        intent.putExtra(Cons.EXTRAS_CHART_FULL_NAME, chartTitle);
                        intent.putExtra(Cons.EXTRAS_CHART_TYPE_SELECTED, chartType);
                        intent.putExtra(Cons.BUNDLE_SLUG_BREAKDOWN_DATA, response.body().getData().getBody().getData());
                        context.startActivity(intent);

                        break;
                }
            }

            @Override
            public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
                alertDialog.setMessage(context.getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }
}
