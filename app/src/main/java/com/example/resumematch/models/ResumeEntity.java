package com.example.resumematch.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resumes")
public class ResumeEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String jobId;
    private String jobTitle;
    private String date;
    private String matchScore;
    private String resumeText;
    private String photoPath;
    private String extractedDataJson;
    private long createdAt;

    public ResumeEntity(@NonNull String id, String jobId, String jobTitle, String date, 
                       String matchScore, String resumeText, long createdAt) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.date = date;
        this.matchScore = matchScore;
        this.resumeText = resumeText;
        this.createdAt = createdAt;
    }


    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMatchScore() { return matchScore; }
    public void setMatchScore(String matchScore) { this.matchScore = matchScore; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getExtractedDataJson() { return extractedDataJson; }
    public void setExtractedDataJson(String extractedDataJson) { this.extractedDataJson = extractedDataJson; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
} 