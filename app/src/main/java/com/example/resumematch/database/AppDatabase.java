package com.example.resumematch.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.models.StoreProfile;

@Database(entities = {JobEntity.class, ResumeEntity.class, StoreProfile.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JobDao jobDao();
    public abstract ResumeDao resumeDao();
    public abstract StoreProfileDao storeProfileDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "resume_match_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
} 