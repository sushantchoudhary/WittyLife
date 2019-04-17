package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.City;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM city ")
    LiveData<List<City>> loadCities();

    @Query("DELETE FROM city")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCityList(List<City> cityList);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCity(City city);

    @Delete
    void deleteCity(City city);

    @Query("SELECT * FROM city WHERE city_name = :name COLLATE NOCASE")
    LiveData<City> loadCityByName(String name);

    @Query("SELECT count(*) FROM city")
    int getRowCount();
}
