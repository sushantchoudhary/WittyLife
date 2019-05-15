package com.appdev.schoudhary.wittylife.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;

import java.util.List;


public class GridWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridViewRemoteViewFactory(this, intent);
    }

}

class GridViewRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = GridViewRemoteViewFactory.class.getSimpleName();

    private Context mContext;
    private static AppDatabase mDB;
    private String rankingData;
    private List<QOLRanking> qolRankingData;


    GridViewRemoteViewFactory(Context context, Intent intent) {
        this.mContext = context;
        if (intent != null) {
            if (intent.hasExtra("rankingData")) {
                rankingData = intent.getStringExtra("rankingData");
            }
        }
        mDB = AppDatabase.getsInstance(context);

    }

    @Override
    public void onCreate() {

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDataSetChanged() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            qolRankingData = mDB.qolDao().loadQOlRankRaw();
        });

        if(qolRankingData != null) {

            StringBuilder sbl = new StringBuilder();
            sbl.append("Purchasing Power: " + String.format("%.2f",qolRankingData.get(0).getPurchasingPowerInclRentIndex()) + "\n");
            sbl.append("Safety Index: " + String.format("%.2f", qolRankingData.get(0).getSafetyIndex()) + "\n");
            sbl.append("Health Care Index: " + String.format("%.2f",qolRankingData.get(0).getHealthcareIndex()) + "\n");

            rankingData = sbl.toString();
        }

        Log.d(TAG, "widget data has changed");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(qolRankingData != null) {
            return 1;
        } else
        { return 0;}
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.witty_widget);
        if(qolRankingData != null) {
            views.setViewVisibility(R.id.widget_witty_image, View.GONE);
            views.setTextViewText(R.id.destination_name, "WittyLife Top Ranking: " + qolRankingData.get(0).getCityName());
            views.setTextViewText(R.id.destination_ranking, rankingData);
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
