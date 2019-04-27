package com.appdev.schoudhary.wittylife.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.DestinationIndices;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;
import com.appdev.schoudhary.wittylife.model.Urls;
import com.appdev.schoudhary.wittylife.model.User;

@Database(entities = {QOLRanking.class, CostRanking.class,
        TrafficRanking.class, Result.class, Urls.class, DestinationIndices.class, City.class, CityIndices.class, User.class}, version = 1, exportSchema = false)
@TypeConverters({DestinationUrlConverter.class, UserConverter.class})
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
                        AppDatabase.class, DATABASE_NAME).addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }
                }).build();
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

    public abstract PhotographerDao photographerDao();
}
