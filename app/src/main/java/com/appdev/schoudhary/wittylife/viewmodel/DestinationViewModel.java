package com.appdev.schoudhary.wittylife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.model.Urls;
import com.appdev.schoudhary.wittylife.repository.RankingRepository;

import java.util.HashMap;
import java.util.List;

public class DestinationViewModel extends AndroidViewModel {

    private static final String TAG = DestinationViewModel.class.getSimpleName();
    private RankingRepository repository ;

    public DestinationViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retrieving destination images from database");

        repository = new RankingRepository(application.getApplicationContext());
    }

    public LiveData<List<Result>>  getDestinationUrl() {
        return repository.loadDestinationResults();
    }

    public LiveData<Boolean> getIsLoading(){
        return repository.isLoading;
    }



}
