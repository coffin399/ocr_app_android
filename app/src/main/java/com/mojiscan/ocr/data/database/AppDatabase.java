package com.mojiscan.ocr.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mojiscan.ocr.data.dao.TranscriptionDao;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;

@Database(entities = {TranscriptionEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TranscriptionDao transcriptionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        AppDatabase.class, "ocr_database")
                                .fallbackToDestructiveMigration()
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to initialize AppDatabase", e);
                    }
                }
            }
        }
        return INSTANCE;
    }
}

