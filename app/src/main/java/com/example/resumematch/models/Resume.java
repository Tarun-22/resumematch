package com.example.resumematch.models;

import java.util.List;

public class Resume {
    private String id;
    private String date;
    private String match;
    private String jobId;

    public Resume(String id, String date, String match, String jobId) {
        this.id = id;
        this.date = date;
        this.match = match;
        this.jobId = jobId;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getMatch() {
        return match;
    }

    public String getJobId() {
        return jobId;
    }
}
