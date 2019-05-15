package com.appdev.schoudhary.wittylife.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CityIndicesRepository {


    //FIXME Use dagger for injecting deps
    private static AppDatabase mDB;
    private Boolean isDBEmpty = true;


    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<CityIndices> cityIndicesMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<City>> cityListMutableLiveData = new MutableLiveData<>();


    public CityIndicesRepository(Context context) {
        mDB = AppDatabase.getsInstance(context);
    }

    public LiveData<List<City>> loadCityList() {
        fetchAndUpdateSpinnerFromAPI();
        return mDB.cityDao().loadCities();
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
                            }
                            isLoading.setValue(false);
                        }, throwable -> {
                            isLoading.setValue(false);
                        cityIndicesMutableLiveData.setValue(null);
                            Log.e(String.valueOf(R.string.app_name), throwable.getMessage());
                        }
                );
        return cityIndicesMutableLiveData;
    }

    private void fetchAndUpdateSpinnerFromAPI() {
        Observable<CityRecords> callCityRecords;
        ApiService apiService = RetroClient.getApiService();
        callCityRecords = apiService.getCityRecords(BuildConfig.ApiKey);
        isLoading.postValue(true);

        /**
         * Fetch city records data from api
         */
        callCityRecords.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CityRecords>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CityRecords cityRecords) {
                        List<City> cities = cityRecords.getCities();
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.cityDao().insertCityList(cities);
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.setValue(false);
                        Log.e(String.valueOf(R.string.app_name), e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        isLoading.setValue(false);
                    }
                });
    }


}
