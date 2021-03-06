package com.appdev.schoudhary.wittylife.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;

public class WittyLife extends Application {

    public  WittyLife wittyLife;
    private Context context;
    private static AppDatabase mDB;

    @SuppressLint("StaticFieldLeak")
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mDB = AppDatabase.getsInstance(getApplicationContext());

        wittyLife = this;
        context = getApplicationContext();

        initializeStetho();

        new AsyncTask<Void, Void, CityRecords>() {
            @Override
            protected CityRecords doInBackground(Void... voids) {
                /**
                 * Load cities from API for city search validation
                 */
                    Call<CityRecords> callCityRecords;
                    ApiService apiService = RetroClient.getApiService();
                    callCityRecords = apiService.getCities(BuildConfig.ApiKey);

                    /**
                     * Fetch city records data from api
                     */
                try {
                    Response<CityRecords> CityRecords = callCityRecords.execute();
                    return CityRecords.body();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CityRecords cityRecords) {
                if( cityRecords != null) {
                    List<City> cities = cityRecords.getCities();
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDB.cityDao().insertCityList(cities);
                    });
                }
            }
        }.execute();

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()
//                .penaltyFlashScreen()
//                .penaltyLog()
//                .build());
//
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .detectActivityLeaks()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
    }

    public void initializeStetho() {
        Stetho.initializeWithDefaults(context);
    }
}
