package com.example.resumematch;

public class Job {
    private String title;
    private String description;

    public Job(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
