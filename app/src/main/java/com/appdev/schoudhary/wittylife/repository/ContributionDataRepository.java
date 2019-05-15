package com.appdev.schoudhary.wittylife.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.util.Pair;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.model.ClimateData;
import com.appdev.schoudhary.wittylife.model.CrimeData;
import com.appdev.schoudhary.wittylife.model.HealthCareData;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ContributionDataRepository {

    private MutableLiveData<Pair<Integer, Integer>> contributionData = new MutableLiveData<>();


    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Pair<Integer, Integer>> loadContributionResults(String city) {
        refreshContributionFromApi(city);
        return contributionData;
    }

    private void refreshContributionFromApi(String destination_city) {
        isLoading.setValue(true);
        ApiService apiService = RetroClient.getApiService();

        Single<CrimeData> crimeDataCall = apiService.getDestinationCrimeData(BuildConfig.ApiKey, destination_city);
        Single<HealthCareData> healthDataCall = apiService.getDestinationHealthData(BuildConfig.ApiKey, destination_city);
        Single<ClimateData> climateDataCall = apiService.getDestinationClimateData(BuildConfig.ApiKey, destination_city);

        Single.zip(crimeDataCall, healthDataCall, climateDataCall, (crimeData, healthCareData, climateData) -> {
            List<Integer> data = Arrays.asList(crimeData.getContributors(), healthCareData.getContributors(), climateData.getContributors());
            Integer maxValue = data.stream().mapToInt(v -> v).max().getAsInt();
            Integer minValue = data.stream().mapToInt(v -> v).min().getAsInt();

            return new Pair<>(maxValue, minValue);
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Pair<Integer, Integer>>() {
            @Override
            public void accept(Pair<Integer, Integer> contribData) throws Exception {
                contributionData.setValue(contribData);
                isLoading.setValue(false);
            }
        }, throwable -> {
            Log.e(String.valueOf(R.string.app_name), throwable.getMessage());
        });
    }

}

