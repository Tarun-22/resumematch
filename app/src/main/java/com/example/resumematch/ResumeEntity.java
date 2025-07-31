package com.example.resumematch;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resumes")
public class ResumeEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String jobId;
    private String jobTitle; // Store job title for easy display
    private String date;
    private String matchScore;
    private String resumeText; // Store the scanned resume text
    private long createdAt;

    public ResumeEntity(String id, String jobId, String jobTitle, String date, String matchScore, String resumeText, long createdAt) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.date = date;
        this.matchScore = matchScore;
        this.resumeText = resumeText;
        this.createdAt = createdAt;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getJobTitle() { return jobTitle; }
    public String getDate() { return date; }
    public String getMatchScore() { return matchScore; }
    public String getResumeText() { return resumeText; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setDate(String date) { this.date = date; }
    public void setMatchScore(String matchScore) { this.matchScore = matchScore; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
} 