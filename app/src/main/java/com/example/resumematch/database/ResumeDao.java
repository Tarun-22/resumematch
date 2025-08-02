package com.example.resumematch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.resumematch.models.ResumeEntity;

import java.util.List;

@Dao
public interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY createdAt DESC")
    List<ResumeEntity> getAllResumes();

    @Query("SELECT * FROM resumes WHERE jobId = :jobId ORDER BY createdAt DESC")
    List<ResumeEntity> getResumesForJob(String jobId);

    @Query("SELECT * FROM resumes WHERE id = :resumeId")
    ResumeEntity getResumeById(String resumeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResume(ResumeEntity resume);

    @Update
    void updateResume(ResumeEntity resume);

    @Delete
    void deleteResume(ResumeEntity resume);

    @Query("DELETE FROM resumes WHERE id = :resumeId")
    void deleteResumeById(String resumeId);

    @Query("DELETE FROM resumes")
    void deleteAllResumes();

    @Query("SELECT COUNT(*) FROM resumes")
    int getResumeCount();

    @Query("SELECT COUNT(*) FROM resumes WHERE jobId = :jobId")
    int getResumeCountForJob(String jobId);
} 