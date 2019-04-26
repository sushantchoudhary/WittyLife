package com.appdev.schoudhary.wittylife.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.model.QOLRanking;

public class RankingUpdateService extends IntentService {

    public static final String ACTION_UPDATE_RANKING_WIDGET = "com.appdev.schoudhary.wittylife.action.update_widget";
    public static final String ACTION_UPDATE_RANKING = "com.appdev.schoudhary.wittylife.action.update_ranking";

    private static final String EXTRA_RANKING = "com.appdev.schoudhary.wittylife.extra.RANKING" ;

    private static QOLRanking ranking;


    public RankingUpdateService() {
        super("RankingUpdateService");
    }

    public static void startActionUpdate(Context context) {
        Intent intent = new Intent(context, RankingUpdateService.class);
        intent.setAction(ACTION_UPDATE_RANKING);
        context.startService(intent);
    }

    public static void startActionUpdateRanking(Context context, QOLRanking ranking) {
        Intent intent = new Intent(context, RankingUpdateService.class);
        intent.putExtra(EXTRA_RANKING, ranking);
        intent.setAction(ACTION_UPDATE_RANKING_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_RANKING_WIDGET.equals(action) ) {
                ranking = intent.getParcelableExtra(EXTRA_RANKING);
                handleActionUpdate(ranking);
            } else if(ACTION_UPDATE_RANKING.equals(action) ) {
                handleActionUpdateRanking();
            }
        }
    }

    private void handleActionUpdateRanking() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RankingUpdateWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);

        RankingUpdateWidgetProvider.updateRankingWidgets(this, appWidgetManager, ranking, appWidgetIds );
    }

    private void handleActionUpdate(QOLRanking updatedRank) {
        ranking = updatedRank;
        startActionUpdate(this);
    }
}
