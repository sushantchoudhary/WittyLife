package com.appdev.schoudhary.wittylife.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.network.UnsplashApiService;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;

import java.util.List;

import io.reactivex.Observable;

public class RankingRepository {

    private static AppDatabase mDB;
    private LiveData<List<Result>> destinationResults;
    public final  MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public RankingRepository(Context context) {
        mDB = AppDatabase.getsInstance(context);
    }

    public LiveData<List<Result>> loadDestinationResults() {
        Observable<List<QOLRanking>> callnumbeo;

        ApiService apiService = RetroClient.getApiService();
        UnsplashApiService unsplashApiService = RetroClient.getUnsplashApiService();

        callnumbeo = apiService.getQOLRanking(BuildConfig.ApiKey);

        isLoading.setValue(true);

        callnumbeo.flatMapIterable(it -> it).take(6)
                .flatMap(qolRanking -> {

                    AppExecutors.getInstance().diskIO().execute(() -> mDB.runInTransaction(() -> {
                        long rowIds = mDB.qolDao().insertQOL(qolRanking);
                    }));

                    return unsplashApiService.getDestination(BuildConfig.UnsplashApiKey, qolRanking.getCityName());

                }).subscribe(destinationImg ->
                        AppExecutors.getInstance().diskIO().execute(() ->
                                mDB.destinationDao().insertDestinationList(destinationImg.getResults())),
                throwable -> Log.e("WittyLife", throwable.getMessage()),
                () -> {
                    destinationResults = mDB.destinationDao().loadAllImages();
                    isLoading.setValue(false);
                }).dispose();

        return destinationResults;
    }

}



