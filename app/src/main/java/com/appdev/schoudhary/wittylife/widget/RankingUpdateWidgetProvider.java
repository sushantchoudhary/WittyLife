package com.appdev.schoudhary.wittylife.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.ui.main.MainActivity;
import com.appdev.schoudhary.wittylife.ui.main.RankingActivity;

import static android.view.View.GONE;

public class RankingUpdateWidgetProvider extends AppWidgetProvider {

    @SuppressLint("DefaultLocale")
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, QOLRanking ranking,
                                int appWidgetId) {
        if (ranking == null) {
            return;
        }
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
        StringBuilder sbl = new StringBuilder();

        sbl.append("Purchasing Power: " + String.format("%.2f",ranking.getPurchasingPowerInclRentIndex()) + "\n");
        sbl.append("Safety Index: " + String.format("%.2f",ranking.getSafetyIndex()) + "\n");
        sbl.append("Health Care Index: " + String.format("%.2f",ranking.getHealthcareIndex()) + "\n");

        if (width < 300) {
            rv = new RemoteViews(context.getPackageName(), R.layout.witty_widget);

            Intent intent = new Intent(context, RankingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            rv.setOnClickPendingIntent(R.id.destination_ranking, pendingIntent);
            rv.setViewVisibility(R.id.widget_witty_image, GONE);
            rv.setTextViewText(R.id.destination_name, "Last Selected WittyLife Ranking: "+ ranking.getCityName());
            rv.setTextViewText(R.id.destination_ranking, sbl.toString());
        } else {
            rv = getRankingGridRemoteView(context, ranking);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RankingUpdateService.startActionUpdate(context);
    }

    public static void updateRankingWidgets(Context context, AppWidgetManager appWidgetManager,
                                           QOLRanking ranking, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, ranking, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RankingUpdateService.startActionUpdate(context);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @SuppressLint("DefaultLocale")
    private static RemoteViews getRankingGridRemoteView(Context context, QOLRanking ranking) {
        //
        StringBuilder sbl = new StringBuilder();
        sbl.append("Purchasing Power: " + String.format("%.2f",ranking.getPurchasingPowerInclRentIndex()) + "\n");
        sbl.append("Safety Index: " + String.format("%.2f",ranking.getSafetyIndex()) + "\n");
        sbl.append("Health Care Index: " + String.format("%.2f",ranking.getHealthcareIndex()) + "\n");


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridWidgetService.class);
        intent.putExtra("rankingData", sbl.toString());
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        // Set the MainActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);

        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        // Handle empty ranking
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }
}
