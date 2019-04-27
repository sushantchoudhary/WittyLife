package com.appdev.schoudhary.wittylife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Urls;

import java.util.HashMap;
import java.util.List;

public class DestinationUrlViewModel extends AndroidViewModel {
    private static final String TAG = DestinationUrlViewModel.class.getSimpleName();
    private LiveData<List<Urls>> destinationUrl;

    HashMap<QOLRanking, Urls> destinationDetails;

    public DestinationUrlViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retrieving destination image from database");
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        destinationUrl = database.urlDao().loadAllUrls();


    }

    public LiveData<List<Urls>> getDestinationUrl() {
        return destinationUrl;
    }

}
