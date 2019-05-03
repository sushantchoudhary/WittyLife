package com.appdev.schoudhary.wittylife.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.DestinationImg;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.network.UnsplashApiService;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RankingRepository {

    //FIXME Use dagger for injecting deps
    private static AppDatabase mDB;
    private MutableLiveData<List<Result>> destinationResults = new MutableLiveData<>();
    private Boolean isDBEmpty = true;


    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private MutableLiveData<Boolean> result = new MutableLiveData<>();

    public RankingRepository(Context context) {
        mDB = AppDatabase.getsInstance(context);
    }

    public LiveData<List<Result>> loadDestinationResults() {
        checkRepoForData();
        return mDB.destinationDao().loadAllImages();
        }

    private void refreshDestinationFromApi() {

        if (result.getValue() == null) {
            clearDatabase();
            Observable<List<QOLRanking>> callnumbeo;

            ApiService apiService = RetroClient.getApiService();
            UnsplashApiService unsplashApiService = RetroClient.getUnsplashApiService();

            callnumbeo = apiService.getQOLRanking(BuildConfig.ApiKey);

            isLoading.postValue(true);

            callnumbeo.flatMapIterable(it -> it).take(10)
                    .flatMap(qolRanking -> {

//                        AppExecutors.getInstance().diskIO().execute(() -> mDB.runInTransaction(() -> {
//                        }));
                        long rowIds = mDB.qolDao().insertQOL(qolRanking);

                        return unsplashApiService.getDestination(BuildConfig.UnsplashApiKey, qolRanking.getCityName());

                    }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<DestinationImg>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(DestinationImg destinationImg) {
                            AppExecutors.getInstance().diskIO().execute(() ->
                            mDB.destinationDao().insertDestinationList(destinationImg.getResults()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(String.valueOf(R.string.app_name), e.getMessage());
                        }

                        @Override
                        public void onComplete() {
//                            destinationResults = mDB.destinationDao().loadAllImages().;
                            isLoading.setValue(false);
                            result.setValue(true);
                        }
                    });
        }
    }

    private void checkRepoForData() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            if (mDB.destinationDao().getRowCount() > 0) {
                result.postValue(true);
            } else {
                refreshDestinationFromApi();
            }
        });
    }


    private void clearDatabase() {
        /**
         * Clean database before fresh insertion
         */
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDB.urlDao().deleteAllRows();
            mDB.photographerDao().deleteAllRows();
            mDB.qolDao().deleteAllRows();
            mDB.destinationDao().deleteAllRows();
        });
    }

}



