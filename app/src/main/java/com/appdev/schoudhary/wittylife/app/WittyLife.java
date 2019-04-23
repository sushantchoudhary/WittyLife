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
import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class WittyLife extends Application {

    private static WittyLife wittyLife;
    private static Context context;
    private static AppDatabase mDB;


    public static WittyLife getMyApplication() {
        return wittyLife;
    }

    public static Context getAppContext() {
        return WittyLife.context;
    }

    @SuppressLint("StaticFieldLeak")
    public void onCreate() {
        super.onCreate();

        mDB = AppDatabase.getsInstance(getApplicationContext());

        wittyLife = this;
        WittyLife.context = getApplicationContext();

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

    }

    public void initializeStetho() {
        Stetho.initializeWithDefaults(context);
    }
}
