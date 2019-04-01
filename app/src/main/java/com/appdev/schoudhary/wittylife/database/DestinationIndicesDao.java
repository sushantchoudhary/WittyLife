package com.appdev.schoudhary.wittylife.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdev.schoudhary.wittylife.model.DestinationIndices;
import com.appdev.schoudhary.wittylife.model.QOLRanking;

import java.util.List;

public interface DestinationIndicesDao {
    @Query("SELECT * FROM destination_indices")
    LiveData<List<DestinationIndices>> loadDestinationIndices();

    @Query("DELETE FROM destination_indices")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDestinationIndices(DestinationIndices destinationIndices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertDestinationIndices(List<DestinationIndices> destinationIndices);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDestinationIndices(DestinationIndices destinationIndices);

    @Delete
    void deleteDestinationIndices(DestinationIndices destinationIndices);

    @Query("SELECT * FROM destination_indices WHERE city_name = :city_name")
    LiveData<DestinationIndices> loadIndicesByName(String city_name);
}
