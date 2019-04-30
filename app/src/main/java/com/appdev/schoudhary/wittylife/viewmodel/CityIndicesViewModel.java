package com.appdev.schoudhary.wittylife.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.repository.CityIndicesRepository;

import java.util.List;

public class CityIndicesViewModel extends AndroidViewModel {
    private static final String TAG = CityIndicesViewModel.class.getSimpleName();
    private LiveData cityIndicesLiveData;
    private CityIndices cityIndices;

    private MutableLiveData cityName = new MutableLiveData();
    private final AppDatabase appDatabase;

    private CityIndicesRepository cityIndicesRepository;


    public CityIndicesViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retrieving city indices from database");
        this.appDatabase = AppDatabase.getsInstance(this.getApplication());

        cityIndicesRepository = new CityIndicesRepository(application.getApplicationContext());

        cityIndicesLiveData = Transformations.switchMap(cityName, new Function<String, LiveData<CityIndices>>() {
            @Override
            public LiveData<CityIndices> apply(String cityName) {
                return appDatabase.cityIndicesDao().loadCityByName(cityName);
            }
        });
    }

    public void loadCity(String cityName) {
        this.cityName.setValue(cityName);
    }

    public LiveData<CityIndices> getCityIndices() {
        return this.cityIndicesLiveData;
    }

    public LiveData<Boolean> getIsLoading(){
        return cityIndicesRepository.isLoading;
    }

    public LiveData<CityIndices> getCityIndex(String city) {
        return cityIndicesRepository.refreshCityIndicesFromAPI(city);
    }
}
