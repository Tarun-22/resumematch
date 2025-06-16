package com.example.resumematch;

import java.util.ArrayList;
import java.util.List;

public class JobStorage {
    public static List<JobPost> jobList = new ArrayList<>();
    public static List<Resume> resumeList = new ArrayList<>();

    public static void addJob(JobPost job) {
        jobList.add(job);
    }

    public static List<JobPost> getAllJobs() {
        return jobList;
    }

    public static void addResume(Resume resume) {
        resumeList.add(resume);
    }

    public static List<Resume> getResumesForJob(String jobId) {
        List<Resume> result = new ArrayList<>();
        for (Resume r : resumeList) {
            if (r.getJobId().equals(jobId)) {
                result.add(r);
            }
        }
        return result;
    }
}
