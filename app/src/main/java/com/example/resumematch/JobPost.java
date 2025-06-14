package com.example.resumematch;

public class JobPost {
    private String title;
    private int resumeCount;

    public JobPost(String title, int resumeCount) {
        this.title = title;
        this.resumeCount = resumeCount;
    }

    public String getTitle() {
        return title;
    }

    public int getResumeCount() {
        return resumeCount;
    }
}
