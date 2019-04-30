package com.appdev.schoudhary.wittylife.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.appdev.schoudhary.wittylife.database.AppDatabase;

public class ContribDataViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final String cityName;

    public ContribDataViewModelFactory(String cityName) {
        this.cityName= cityName;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ContribDataViewModel(cityName);
    }
}
