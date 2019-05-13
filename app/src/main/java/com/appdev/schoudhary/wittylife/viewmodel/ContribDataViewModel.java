package com.appdev.schoudhary.wittylife.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import com.appdev.schoudhary.wittylife.repository.ContributionDataRepository;

public class ContribDataViewModel extends ViewModel {

    private final String cityName;
    private static ContributionDataRepository dataRepository;

    Pair<Integer, Integer> contribData = new     Pair<>(0,0);

    public ContribDataViewModel(String destination) {
        cityName = destination;
        dataRepository = new ContributionDataRepository();
    }

    public LiveData<Pair<Integer,Integer>> getContribData() {
        return dataRepository.loadContributionResults(cityName);
    }

    public LiveData<Boolean> getIsLoading(){
        return dataRepository.isLoading;
    }
}
