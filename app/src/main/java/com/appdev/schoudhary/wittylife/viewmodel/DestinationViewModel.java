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

import java.util.HashMap;
import java.util.List;

public class DestinationViewModel extends AndroidViewModel {

    private static final String TAG = DestinationViewModel.class.getSimpleName();
    private LiveData<List<Result>> destinationUrl;

    HashMap<QOLRanking, Urls> destinationDetails;

    public DestinationViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retrieving destination images from database");
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        destinationUrl = database.destinationDao().loadAllImages();

    }

    public LiveData<List<Result>>  getDestinationUrl() {
        return destinationUrl;
    }

}
