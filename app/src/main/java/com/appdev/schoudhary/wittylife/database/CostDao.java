package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.QOLRanking;

import java.util.List;

@Dao
public interface CostDao {

    @Query("SELECT * FROM costranking ")
    LiveData<List<CostRanking>> loadCostRank();

    @Query("DELETE FROM costranking")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCost(CostRanking costrecord);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCostList(List<CostRanking> costRecords);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCost(CostRanking costRanking);

    @Delete
    void deleteCost(CostRanking costRecord);

    @Query("SELECT * FROM costranking WHERE city_id = :id")
    LiveData<CostRanking> loadCostById(int id);

}
