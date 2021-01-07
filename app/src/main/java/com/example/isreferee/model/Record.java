package com.example.isreferee.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recordsTable", primaryKeys = {"HeatNumber","LaneNumber"})
public class Record {

    @NonNull
    @ColumnInfo(name = "HeatNumber")
    public int HeatNum;

    @NonNull
    @ColumnInfo(name = "LaneNumber")
    public String LaneNum;

    @NonNull
    @ColumnInfo(name = "Record")
    public String Record;

    public Record(@NonNull int HeatNum, @NonNull String LaneNum, @NonNull String Record) {
        this.HeatNum = HeatNum;
        this.LaneNum = LaneNum;
        this.Record = Record;
    }

    public int getHeatNumber(){return this.HeatNum;}

    public String getLaneNumber(){return this.LaneNum;}

    public String getRecord(){return this.Record;}

//    public void setHeatNUmber(@NonNull int heatNum){this.HeatNum = heatNum;}
//
//    public void setLaneNumber(@NonNull String laneNum){this.LaneNum = laneNum;}
//
//    public void setRecord(@NonNull String record){ this.Record = record;}
}
