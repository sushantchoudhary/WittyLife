package com.appdev.schoudhary.wittylife.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.DestinationIndices;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;
import com.appdev.schoudhary.wittylife.model.Urls;

@Database(entities = {QOLRanking.class, CostRanking.class,
        TrafficRanking.class, Result.class, Urls.class, DestinationIndices.class, City.class, CityIndices.class}, version = 1, exportSchema = false)
@TypeConverters({DestinationUrlConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "wittydb";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new witty database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME).build();
            }

        }
        Log.d(LOG_TAG, "Fetching the database instance");
        return sInstance;
    }

    public abstract CostDao costDao();

    public abstract QOLDao qolDao();

    public abstract TrafficDao trafficDao();

    public abstract DestinationDao destinationDao();

    public  abstract  UrlDao urlDao();

    public abstract CityDao cityDao();

    public abstract CityIndicesDao cityIndicesDao();
}
