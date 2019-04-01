package com.appdev.schoudhary.wittylife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();


    private List<QOLRanking> qolranking;
    private static AppDatabase database;


    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Retrieving task from database");
        database = AppDatabase.getsInstance(this.getApplication());
        qolranking = database.qolDao().loadQOlRank();
    }

    public List<QOLRanking> getQOLRanking() {
        return qolranking;
    }

    public LiveData<QOLRanking> getRankingForCityId(int city_id) {
        return database.qolDao().loadQOLById(city_id);
    }

}
