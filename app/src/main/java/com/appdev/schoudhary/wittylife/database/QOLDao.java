package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.QOLRanking;

import java.util.List;

@Dao
public interface QOLDao {
    @Query("SELECT * FROM qolranking ORDER BY qualityOfLifeIndex DESC")
    LiveData<List<QOLRanking>> loadQOlRank();

    @Query("DELETE FROM qolranking")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQOL(QOLRanking qolrecord);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertQOLList(List<QOLRanking> qolRecords);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateQOL(QOLRanking qolRanking);

    @Delete
    void deleteQOL(QOLRanking qolRecord);

    @Query("SELECT * FROM qolranking WHERE city_id = :id")
    LiveData<QOLRanking> loadQOLById(int id);
}
