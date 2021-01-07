package com.example.isreferee.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Record.class}, version = 6, exportSchema = false)
public abstract class RecordsRoomDatabase extends RoomDatabase {

    public abstract RecordDao RecordDao();
    private static RecordsRoomDatabase INSTANCE;

    public static RecordsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordsRoomDatabase.class, "Records_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final RecordDao mDao;
        int tmpHeat = 0;
        String tmpLane = "0";
        String tmpNum = "00:00:00";

        PopulateDbAsync(RecordsRoomDatabase db) {
            mDao = db.RecordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            mDao.deleteAll();  //After Test
//
//            Record record = new Record(tmpHeat,tmpLane, tmpNum);
//            mDao.insert(record);

            return null;
        }
    }
}
