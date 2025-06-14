package com.example.resumematch;

public class Resume {
    String id, date, match;

    public Resume(String id, String date, String match) {
        this.id = id;
        this.date = date;
        this.match = match;
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
}
