package com.example.resumematch;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRepository {
    private JobDao jobDao;
    private ResumeDao resumeDao;
    private ExecutorService executorService;

    public DataRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        jobDao = db.jobDao();
        resumeDao = db.resumeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Job operations
    public void insertJob(JobEntity job, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            jobDao.insertJob(job);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void getAllJobs(DatabaseCallback<List<JobEntity>> callback) {
        executorService.execute(() -> {
            List<JobEntity> jobs = jobDao.getAllJobs();
            if (callback != null) {
                callback.onResult(jobs);
            }
        });
    }

    public void getJobById(String jobId, DatabaseCallback<JobEntity> callback) {
        executorService.execute(() -> {
            JobEntity job = jobDao.getJobById(jobId);
            if (callback != null) {
                callback.onResult(job);
            }
        });
    }

    public void updateJob(JobEntity job, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            jobDao.updateJob(job);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void deleteJob(JobEntity job, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            jobDao.deleteJob(job);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void getJobCount(DatabaseCallback<Integer> callback) {
        executorService.execute(() -> {
            int count = jobDao.getJobCount();
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }

    // Resume operations
    public void insertResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            resumeDao.insertResume(resume);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void getAllResumes(DatabaseCallback<List<ResumeEntity>> callback) {
        executorService.execute(() -> {
            List<ResumeEntity> resumes = resumeDao.getAllResumes();
            if (callback != null) {
                callback.onResult(resumes);
            }
        });
    }

    public void getResumesForJob(String jobId, DatabaseCallback<List<ResumeEntity>> callback) {
        executorService.execute(() -> {
            List<ResumeEntity> resumes = resumeDao.getResumesForJob(jobId);
            if (callback != null) {
                callback.onResult(resumes);
            }
        });
    }

    public void getResumeById(String resumeId, DatabaseCallback<ResumeEntity> callback) {
        executorService.execute(() -> {
            ResumeEntity resume = resumeDao.getResumeById(resumeId);
            if (callback != null) {
                callback.onResult(resume);
            }
        });
    }

    public void updateResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            resumeDao.updateResume(resume);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void deleteResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        executorService.execute(() -> {
            resumeDao.deleteResume(resume);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public void getResumeCount(DatabaseCallback<Integer> callback) {
        executorService.execute(() -> {
            int count = resumeDao.getResumeCount();
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }

    public void getResumeCountForJob(String jobId, DatabaseCallback<Integer> callback) {
        executorService.execute(() -> {
            int count = resumeDao.getResumeCountForJob(jobId);
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }

    // Callback interface
    public interface DatabaseCallback<T> {
        void onResult(T result);
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 