package com.appdev.schoudhary.wittylife.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.appdev.schoudhary.wittylife.model.CityIndices;

class CustomLiveData extends MediatorLiveData<Pair<CityIndices, CityIndices>> {
    public CustomLiveData(LiveData<CityIndices> sourceCity, LiveData<CityIndices> selectedCity) {
        addSource(sourceCity, new Observer<CityIndices>() {
            public void onChanged(@Nullable CityIndices first) {
                setValue(Pair.create(first, selectedCity.getValue()));
            }
        });
        addSource(selectedCity, new Observer<CityIndices>() {
            public void onChanged(@Nullable CityIndices second) {
                setValue(Pair.create(sourceCity.getValue(), second));
            }
        });
    }
}
