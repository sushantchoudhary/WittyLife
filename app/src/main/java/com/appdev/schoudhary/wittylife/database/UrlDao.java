package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.Urls;

import java.util.List;

@Dao
public interface UrlDao {
        @Query("SELECT * FROM destination_url")
        LiveData<List<Urls>> loadAllUrls();

        @Query("DELETE FROM destination_url")
        void deleteAllRows();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertURL(Urls url);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        List<Long> insertUrlsList(List<Urls> urlRecords);

        @Update(onConflict = OnConflictStrategy.REPLACE)
        void updateURL(Urls urls);

        @Delete
        void deleteURL(Urls urls);

        @Query("SELECT * FROM destination_url WHERE  url_id= :id")
        LiveData<Urls> loadURLById(int id);
}
