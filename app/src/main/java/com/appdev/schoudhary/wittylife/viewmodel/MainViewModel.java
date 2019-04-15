package com.appdev.schoudhary.wittylife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();


    private LiveData<List<QOLRanking>> qolranking;
    private LiveData<List<CostRanking>> costRanking;
    private LiveData<List<TrafficRanking>> trafficRanking;
    private LiveData<List<City>> cityRecords;
    private CityIndices cityIndices;

    private static AppDatabase database;


    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Retrieving task from database");
        database = AppDatabase.getsInstance(this.getApplication());
        qolranking = database.qolDao().loadQOlRank();
        costRanking = database.costDao().loadCostRank();
        trafficRanking = database.trafficDao().loadTrafficRank();

        cityRecords = database.cityDao().loadCities();
        }

    public LiveData<List<QOLRanking>> getQOLRanking() {
        return qolranking;
    }

    public LiveData<QOLRanking> getRankingForCityId(int city_id) {
        return database.qolDao().loadQOLById(city_id);
    }

    public LiveData<List<CostRanking>> getCostRanking() {
        return costRanking;
    }

    public LiveData<List<TrafficRanking>> getTrafficRanking() {
        return trafficRanking;
    }

    public LiveData<List<City>> getCityRecords() {
        return cityRecords;
    }
}
