package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CostRanking;

import java.util.List;

@Dao
public interface CityIndicesDao {

    @Query("SELECT * FROM city_indices")
    LiveData<List<CityIndices>> loadCityIndices();

    @Query("DELETE FROM city_indices")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertIndices(CityIndices cityIndices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertIndicesList(List<CityIndices> cityIndices);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCost(CityIndices cityIndices);

    @Delete
    void deleteCost(CityIndices cityIndices);

    @Query("SELECT * FROM city_indices WHERE city_name = :name")
    LiveData<CityIndices> loadCityByName(String name);

    @Query("SELECT * FROM city_indices WHERE city_name = :name")
    CityIndices loadCityByNameRaw(String name);

    @Query("SELECT count(*) FROM city_indices")
    int getRowCount();
}
