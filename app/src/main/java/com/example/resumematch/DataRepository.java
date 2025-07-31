package com.example.resumematch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

public class DataRepository {
    private JobDao jobDao;
    private ResumeDao resumeDao;
    private StoreProfileDao storeProfileDao;
    private ExecutorService executorService;

    public DataRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        jobDao = db.jobDao();
        resumeDao = db.resumeDao();
        storeProfileDao = db.storeProfileDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Job operations with AsyncTask
    public void insertJob(JobEntity job, DatabaseCallback<Void> callback) {
        new InsertJobAsyncTask(jobDao, callback).execute(job);
    }

    public void getAllJobs(DatabaseCallback<List<JobEntity>> callback) {
        new GetAllJobsAsyncTask(jobDao, callback).execute();
    }

    public void getJobById(String jobId, DatabaseCallback<JobEntity> callback) {
        new GetJobByIdAsyncTask(jobDao, callback).execute(jobId);
    }

    public void updateJob(JobEntity job, DatabaseCallback<Void> callback) {
        new UpdateJobAsyncTask(jobDao, callback).execute(job);
    }

    public void deleteJob(JobEntity job, DatabaseCallback<Void> callback) {
        new DeleteJobAsyncTask(jobDao, callback).execute(job);
    }

    public void getJobCount(DatabaseCallback<Integer> callback) {
        new GetJobCountAsyncTask(jobDao, callback).execute();
    }

    public void deleteAllJobs(DatabaseCallback<Void> callback) {
        new DeleteAllJobsAsyncTask(jobDao, callback).execute();
    }

    // Resume operations with AsyncTask
    public void insertResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new InsertResumeAsyncTask(resumeDao, callback).execute(resume);
    }

    public void getAllResumes(DatabaseCallback<List<ResumeEntity>> callback) {
        new GetAllResumesAsyncTask(resumeDao, callback).execute();
    }

    public void getResumesForJob(String jobId, DatabaseCallback<List<ResumeEntity>> callback) {
        new GetResumesForJobAsyncTask(resumeDao, callback).execute(jobId);
    }

    public void getResumeById(String resumeId, DatabaseCallback<ResumeEntity> callback) {
        new GetResumeByIdAsyncTask(resumeDao, callback).execute(resumeId);
    }

    public void updateResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new UpdateResumeAsyncTask(resumeDao, callback).execute(resume);
    }

    public void deleteResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new DeleteResumeAsyncTask(resumeDao, callback).execute(resume);
    }

    public void deleteAllResumes(DatabaseCallback<Void> callback) {
        new DeleteAllResumesAsyncTask(resumeDao, callback).execute();
    }

    public void getResumeCount(DatabaseCallback<Integer> callback) {
        new GetResumeCountAsyncTask(resumeDao, callback).execute();
    }

    public void getResumeCountForJob(String jobId, DatabaseCallback<Integer> callback) {
        new GetResumeCountForJobAsyncTask(resumeDao, callback).execute(jobId);
    }

    // Store Profile operations with AsyncTask
    public void insertStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new InsertStoreAsyncTask(storeProfileDao, callback).execute(store);
    }

    public void getFirstStore(DatabaseCallback<StoreProfile> callback) {
        new GetFirstStoreAsyncTask(storeProfileDao, callback).execute();
    }

    public void updateStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new UpdateStoreAsyncTask(storeProfileDao, callback).execute(store);
    }

    public void deleteStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new DeleteStoreAsyncTask(storeProfileDao, callback).execute(store);
    }

    public void getStoreCount(DatabaseCallback<Integer> callback) {
        new GetStoreCountAsyncTask(storeProfileDao, callback).execute();
    }

    // Callback interface
    public interface DatabaseCallback<T> {
        void onResult(T result);
    }

    // AsyncTask implementations for Jobs
    private static class InsertJobAsyncTask extends AsyncTask<JobEntity, Void, Void> {
        private JobDao jobDao;
        private DatabaseCallback<Void> callback;

        InsertJobAsyncTask(JobDao jobDao, DatabaseCallback<Void> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(JobEntity... jobs) {
            jobDao.insertJob(jobs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetAllJobsAsyncTask extends AsyncTask<Void, Void, List<JobEntity>> {
        private JobDao jobDao;
        private DatabaseCallback<List<JobEntity>> callback;

        GetAllJobsAsyncTask(JobDao jobDao, DatabaseCallback<List<JobEntity>> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected List<JobEntity> doInBackground(Void... voids) {
            try {
                return jobDao.getAllJobs();
            } catch (Exception e) {
                android.util.Log.e("GetAllJobsAsyncTask", "Error getting jobs: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<JobEntity> result) {
            try {
                if (callback != null) {
                    callback.onResult(result);
                }
            } catch (Exception e) {
                android.util.Log.e("GetAllJobsAsyncTask", "Error in onPostExecute: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static class GetJobByIdAsyncTask extends AsyncTask<String, Void, JobEntity> {
        private JobDao jobDao;
        private DatabaseCallback<JobEntity> callback;

        GetJobByIdAsyncTask(JobDao jobDao, DatabaseCallback<JobEntity> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected JobEntity doInBackground(String... jobIds) {
            return jobDao.getJobById(jobIds[0]);
        }

        @Override
        protected void onPostExecute(JobEntity result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class UpdateJobAsyncTask extends AsyncTask<JobEntity, Void, Void> {
        private JobDao jobDao;
        private DatabaseCallback<Void> callback;

        UpdateJobAsyncTask(JobDao jobDao, DatabaseCallback<Void> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(JobEntity... jobs) {
            jobDao.updateJob(jobs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class DeleteJobAsyncTask extends AsyncTask<JobEntity, Void, Void> {
        private JobDao jobDao;
        private DatabaseCallback<Void> callback;

        DeleteJobAsyncTask(JobDao jobDao, DatabaseCallback<Void> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(JobEntity... jobs) {
            jobDao.deleteJob(jobs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetJobCountAsyncTask extends AsyncTask<Void, Void, Integer> {
        private JobDao jobDao;
        private DatabaseCallback<Integer> callback;

        GetJobCountAsyncTask(JobDao jobDao, DatabaseCallback<Integer> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return jobDao.getJobCount();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    // AsyncTask implementations for Resumes
    private static class InsertResumeAsyncTask extends AsyncTask<ResumeEntity, Void, Void> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Void> callback;

        InsertResumeAsyncTask(ResumeDao resumeDao, DatabaseCallback<Void> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ResumeEntity... resumes) {
            resumeDao.insertResume(resumes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetAllResumesAsyncTask extends AsyncTask<Void, Void, List<ResumeEntity>> {
        private ResumeDao resumeDao;
        private DatabaseCallback<List<ResumeEntity>> callback;

        GetAllResumesAsyncTask(ResumeDao resumeDao, DatabaseCallback<List<ResumeEntity>> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected List<ResumeEntity> doInBackground(Void... voids) {
            return resumeDao.getAllResumes();
        }

        @Override
        protected void onPostExecute(List<ResumeEntity> result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetResumesForJobAsyncTask extends AsyncTask<String, Void, List<ResumeEntity>> {
        private ResumeDao resumeDao;
        private DatabaseCallback<List<ResumeEntity>> callback;

        GetResumesForJobAsyncTask(ResumeDao resumeDao, DatabaseCallback<List<ResumeEntity>> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected List<ResumeEntity> doInBackground(String... jobIds) {
            return resumeDao.getResumesForJob(jobIds[0]);
        }

        @Override
        protected void onPostExecute(List<ResumeEntity> result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetResumeByIdAsyncTask extends AsyncTask<String, Void, ResumeEntity> {
        private ResumeDao resumeDao;
        private DatabaseCallback<ResumeEntity> callback;

        GetResumeByIdAsyncTask(ResumeDao resumeDao, DatabaseCallback<ResumeEntity> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected ResumeEntity doInBackground(String... resumeIds) {
            return resumeDao.getResumeById(resumeIds[0]);
        }

        @Override
        protected void onPostExecute(ResumeEntity result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class UpdateResumeAsyncTask extends AsyncTask<ResumeEntity, Void, Void> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Void> callback;

        UpdateResumeAsyncTask(ResumeDao resumeDao, DatabaseCallback<Void> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ResumeEntity... resumes) {
            resumeDao.updateResume(resumes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class DeleteResumeAsyncTask extends AsyncTask<ResumeEntity, Void, Void> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Void> callback;

        DeleteResumeAsyncTask(ResumeDao resumeDao, DatabaseCallback<Void> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ResumeEntity... resumes) {
            resumeDao.deleteResume(resumes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetResumeCountAsyncTask extends AsyncTask<Void, Void, Integer> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Integer> callback;

        GetResumeCountAsyncTask(ResumeDao resumeDao, DatabaseCallback<Integer> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return resumeDao.getResumeCount();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetResumeCountForJobAsyncTask extends AsyncTask<String, Void, Integer> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Integer> callback;

        GetResumeCountForJobAsyncTask(ResumeDao resumeDao, DatabaseCallback<Integer> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(String... jobIds) {
            return resumeDao.getResumeCountForJob(jobIds[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    // AsyncTask implementations for Store Profiles
    private static class InsertStoreAsyncTask extends AsyncTask<StoreProfile, Void, Void> {
        private StoreProfileDao storeProfileDao;
        private DatabaseCallback<Void> callback;

        InsertStoreAsyncTask(StoreProfileDao storeProfileDao, DatabaseCallback<Void> callback) {
            this.storeProfileDao = storeProfileDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(StoreProfile... stores) {
            storeProfileDao.insertStore(stores[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetFirstStoreAsyncTask extends AsyncTask<Void, Void, StoreProfile> {
        private StoreProfileDao storeProfileDao;
        private DatabaseCallback<StoreProfile> callback;

        GetFirstStoreAsyncTask(StoreProfileDao storeProfileDao, DatabaseCallback<StoreProfile> callback) {
            this.storeProfileDao = storeProfileDao;
            this.callback = callback;
        }

        @Override
        protected StoreProfile doInBackground(Void... voids) {
            return storeProfileDao.getFirstStore();
        }

        @Override
        protected void onPostExecute(StoreProfile result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class UpdateStoreAsyncTask extends AsyncTask<StoreProfile, Void, Void> {
        private StoreProfileDao storeProfileDao;
        private DatabaseCallback<Void> callback;

        UpdateStoreAsyncTask(StoreProfileDao storeProfileDao, DatabaseCallback<Void> callback) {
            this.storeProfileDao = storeProfileDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(StoreProfile... stores) {
            storeProfileDao.updateStore(stores[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class DeleteStoreAsyncTask extends AsyncTask<StoreProfile, Void, Void> {
        private StoreProfileDao storeProfileDao;
        private DatabaseCallback<Void> callback;

        DeleteStoreAsyncTask(StoreProfileDao storeProfileDao, DatabaseCallback<Void> callback) {
            this.storeProfileDao = storeProfileDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(StoreProfile... stores) {
            storeProfileDao.deleteStore(stores[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class GetStoreCountAsyncTask extends AsyncTask<Void, Void, Integer> {
        private StoreProfileDao storeProfileDao;
        private DatabaseCallback<Integer> callback;

        GetStoreCountAsyncTask(StoreProfileDao storeProfileDao, DatabaseCallback<Integer> callback) {
            this.storeProfileDao = storeProfileDao;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return storeProfileDao.getStoreCount();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class DeleteAllJobsAsyncTask extends AsyncTask<Void, Void, Void> {
        private JobDao jobDao;
        private DatabaseCallback<Void> callback;

        DeleteAllJobsAsyncTask(JobDao jobDao, DatabaseCallback<Void> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            jobDao.deleteAllJobs();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    private static class DeleteAllResumesAsyncTask extends AsyncTask<Void, Void, Void> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Void> callback;

        DeleteAllResumesAsyncTask(ResumeDao resumeDao, DatabaseCallback<Void> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            resumeDao.deleteAllResumes();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 