package com.appdev.schoudhary.wittylife.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CityIndicesRepository {


    //FIXME Use dagger for injecting deps
    private static AppDatabase mDB;
    private Boolean isDBEmpty = true;


    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<CityIndices> cityIndicesMutableLiveData = new MutableLiveData<>();

    public CityIndicesRepository(Context context) {
        mDB = AppDatabase.getsInstance(context);
    }

    public LiveData<CityIndices> refreshCityIndicesFromAPI(String cityName) {

        Single<CityIndices> callCityIndices;
        ApiService apiService = RetroClient.getApiService();
        callCityIndices = apiService.getCityIndices(BuildConfig.ApiKey, cityName);
        isLoading.setValue(true);

        callCityIndices.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityIndices -> {
                            if (cityIndices.getName() != null) {
                                cityIndicesMutableLiveData.setValue(cityIndices);
                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    mDB.cityIndicesDao().insertIndices(cityIndices);
                                });
                                isLoading.setValue(false);
                            }

                        }, throwable -> {
                            isLoading.setValue(false);
                            Log.e("WittyLife", throwable.getMessage());
                        }
                );
        return cityIndicesMutableLiveData;
    }


    private Boolean checkRoomForData() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            if (mDB.cityIndicesDao().getRowCount() > 0) {
                isDBEmpty = false;
            }
        });
        return isDBEmpty;
    }
}
