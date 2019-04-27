package com.appdev.schoudhary.wittylife.viewmodel;

import android.arch.lifecycle.ViewModelProvider;

import com.appdev.schoudhary.wittylife.database.AppDatabase;

public class CityIndicesViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDB;
    private final String cityName;

    public CityIndicesViewModelFactory(AppDatabase mDB, String cityName) {
        this.mDB = mDB;
        this.cityName= cityName;
    }

//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
////        return (T) new CityIndicesViewModel();
//    }
}
