package com.example.resumematch.database;
//we used genAI on how to perfectly setup the data repository
//we created our strucuture, and asked genAI to properly strucuture,
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.models.StoreProfile;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

public class DataRepository {
    private JobDao job_dao;
    private ResumeDao resume_dao;
    private StoreProfileDao store_dao;
    private ExecutorService executor_service;

    public DataRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        job_dao = db.jobDao();
        resume_dao = db.resumeDao();
        store_dao = db.storeProfileDao();
        executor_service = Executors.newSingleThreadExecutor();
    }

    public void insertJob(JobEntity job, DatabaseCallback<Void> callback) {
        new InsertJobAsyncTask(job_dao, callback).execute(job);
    }

    public void get_all_jobs(DatabaseCallback<List<JobEntity>> callback) {
        new GetAllJobsAsyncTask(job_dao, callback).execute();
    }

    public void get_job_id(String jobId, DatabaseCallback<JobEntity> callback) {
        new GetJobByIdAsyncTask(job_dao, callback).execute(jobId);
    }

    public void update_Job(JobEntity job, DatabaseCallback<Void> callback) {
        new UpdateJobAsyncTask(job_dao, callback).execute(job);
    }

    public void delete_Job(JobEntity job, DatabaseCallback<Void> callback) {
        new DeleteJobAsyncTask(job_dao, callback).execute(job);
    }

    public void deleteJob(String jobId, DatabaseCallback<Void> callback) {
        new DeleteJobByIdAsyncTask(job_dao, callback).execute(jobId);
    }

    public void getJobCount(DatabaseCallback<Integer> callback) {
        new GetJobCountAsyncTask(job_dao, callback).execute();
    }

    public void deleteAllJobs(DatabaseCallback<Void> callback) {
        new DeleteAllJobsAsyncTask(job_dao, callback).execute();
    }

    public void insertResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new InsertResumeAsyncTask(resume_dao, callback).execute(resume);
    }

    public void getAllResumes(DatabaseCallback<List<ResumeEntity>> callback) {
        new GetAllResumesAsyncTask(resume_dao, callback).execute();
    }

    public void getResumesForJob(String jobId, DatabaseCallback<List<ResumeEntity>> callback) {
        new GetResumesForJobAsyncTask(resume_dao, callback).execute(jobId);
    }

    public void getResumeById(String resumeId, DatabaseCallback<ResumeEntity> callback) {
        new GetResumeByIdAsyncTask(resume_dao, callback).execute(resumeId);
    }

    public void updateResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new UpdateResumeAsyncTask(resume_dao, callback).execute(resume);
    }

    public void deleteResume(ResumeEntity resume, DatabaseCallback<Void> callback) {
        new DeleteResumeAsyncTask(resume_dao, callback).execute(resume);
    }

    public void deleteResume(String resumeId, DatabaseCallback<Void> callback) {
        new DeleteResumeByIdAsyncTask(resume_dao, callback).execute(resumeId);
    }

    public void deleteAllResumes(DatabaseCallback<Void> callback) {
        new DeleteAllResumesAsyncTask(resume_dao, callback).execute();
    }

    public void getcount(DatabaseCallback<Integer> callback) {
        new GetResumeCountAsyncTask(resume_dao, callback).execute();
    }

    public void getResumeCountForJob(String jobId, DatabaseCallback<Integer> callback) {
        new GetResumeCountForJobAsyncTask(resume_dao, callback).execute(jobId);
    }

    public void insertStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new InsertStoreAsyncTask(store_dao, callback).execute(store);
    }

    public void getFirstStore(DatabaseCallback<StoreProfile> callback) {
        new GetFirstStoreAsyncTask(store_dao, callback).execute();
    }

    public void updateStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new UpdateStoreAsyncTask(store_dao, callback).execute(store);
    }

    public void deleteStore(StoreProfile store, DatabaseCallback<Void> callback) {
        new DeleteStoreAsyncTask(store_dao, callback).execute(store);
    }

    public void getStoreCount(DatabaseCallback<Integer> callback) {
        new GetStoreCountAsyncTask(store_dao, callback).execute();
    }

    public interface DatabaseCallback<T> {
        void onResult(T result);
    }

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
                Log.e("GetAllJobsAsyncTask", "Error getting jobs: " + e.getMessage());
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
                Log.e("GetAllJobsAsyncTask", "Error in onPostExecute: " + e.getMessage());
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

    private static class DeleteJobByIdAsyncTask extends AsyncTask<String, Void, Void> {
        private JobDao jobDao;
        private DatabaseCallback<Void> callback;

        DeleteJobByIdAsyncTask(JobDao jobDao, DatabaseCallback<Void> callback) {
            this.jobDao = jobDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... jobIds) {
            jobDao.deleteJobById(jobIds[0]);
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

    private static class DeleteResumeByIdAsyncTask extends AsyncTask<String, Void, Void> {
        private ResumeDao resumeDao;
        private DatabaseCallback<Void> callback;

        DeleteResumeByIdAsyncTask(ResumeDao resumeDao, DatabaseCallback<Void> callback) {
            this.resumeDao = resumeDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... resumeIds) {
            resumeDao.deleteResumeById(resumeIds[0]);
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
        executor_service.shutdown();
    }
} 