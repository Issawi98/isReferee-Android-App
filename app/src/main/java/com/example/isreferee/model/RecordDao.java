package com.example.isreferee.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Record record);

    @Query("DELETE FROM recordsTable")
    void deleteAll();

    @Delete()
    void delete(Record record);

    @Query("SELECT * from recordsTable ORDER BY HeatNumber, LaneNumber ASC")
    LiveData<List<Record>> getAllRecords();

    @Query("SELECT Record from recordsTable where HeatNumber = :number AND LaneNumber = :lane")
    String getRecord(int number, int lane);
}
