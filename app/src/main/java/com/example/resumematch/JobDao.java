package com.example.resumematch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    List<JobEntity> getAllJobs();

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    JobEntity getJobById(String jobId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJob(JobEntity job);

    @Update
    void updateJob(JobEntity job);

    @Delete
    void deleteJob(JobEntity job);

    @Query("DELETE FROM jobs WHERE id = :jobId")
    void deleteJobById(String jobId);

    @Query("DELETE FROM jobs")
    void deleteAllJobs();

    @Query("SELECT COUNT(*) FROM jobs")
    int getJobCount();
} 