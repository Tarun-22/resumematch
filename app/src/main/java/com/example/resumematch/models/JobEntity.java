package com.example.resumematch.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "jobs")
public class JobEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String description;
    private String keywords;
    private int resumeCount;
    private long createdAt;

    public JobEntity(String id, String title, String description, String keywords, int resumeCount, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.resumeCount = resumeCount;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getKeywords() { return keywords; }
    public int getResumeCount() { return resumeCount; }
    public long getCreatedAt() { return createdAt; }

    public void setId(@NonNull String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public void setResumeCount(int resumeCount) { this.resumeCount = resumeCount; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
} 