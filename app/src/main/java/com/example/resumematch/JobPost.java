package com.example.resumematch;

import java.util.List;

public class JobPost {
    private String id;
    private String title;
    private String description;
    private List<String> keywords;
    private List<Resume> resumes;
    private int resumeCount;


    public JobPost(String id, String title, String description, List<String> keywords, List<Resume> resumes) {
        this.title = title;
        this.resumeCount = resumeCount;
        this.id = id;
        this.description = description;
        this.keywords = keywords;
        this.resumes = resumes;
    }

    public String getTitle() {
        return title;
    }
    public String getId() { return id; }
    public String getDescription() { return description; }
    public List<String> getKeywords() { return keywords; }


    public int getResumeCount() {
        return resumeCount;
    }

    public List<Resume> getResumes() {
        return resumes;
    }
}
