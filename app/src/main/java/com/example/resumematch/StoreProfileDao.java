package com.example.resumematch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StoreProfileDao {
    @Query("SELECT * FROM store_profile")
    List<StoreProfile> getAllStores();

    @Query("SELECT * FROM store_profile WHERE id = :id")
    StoreProfile getStoreById(String id);

    @Query("SELECT * FROM store_profile LIMIT 1")
    StoreProfile getFirstStore();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(StoreProfile store);

    @Update
    void updateStore(StoreProfile store);

    @Delete
    void deleteStore(StoreProfile store);

    @Query("DELETE FROM store_profile WHERE id = :id")
    void deleteStoreById(String id);

    @Query("SELECT COUNT(*) FROM store_profile")
    int getStoreCount();
} 