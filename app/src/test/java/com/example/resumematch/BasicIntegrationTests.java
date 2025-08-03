package com.example.resumematch;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.resumematch.database.AppDatabase;
import com.example.resumematch.database.JobDao;
import com.example.resumematch.database.ResumeDao;
import com.example.resumematch.database.StoreProfileDao;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.models.StoreProfile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BasicIntegrationTests {

    private AppDatabase database;
    private JobDao jobDao;
    private ResumeDao resumeDao;
    private StoreProfileDao storeProfileDao;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        jobDao = database.jobDao();
        resumeDao = database.resumeDao();
        storeProfileDao = database.storeProfileDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testJobInsertAndRetrieve() {
        JobEntity job = new JobEntity("test-job", "Test Job", "Test Description", "test,keywords", 0, System.currentTimeMillis());
        
        jobDao.insertJob(job);
        
        JobEntity retrievedJob = jobDao.getJobById("test-job");
        
        assertNotNull(retrievedJob);
        assertEquals("test-job", retrievedJob.getId());
        assertEquals("Test Job", retrievedJob.getTitle());
        assertEquals("Test Description", retrievedJob.getDescription());
        assertEquals("test,keywords", retrievedJob.getKeywords());
        assertEquals(0, retrievedJob.getResumeCount());
    }

    @Test
    public void testJobUpdate() {
        JobEntity job = new JobEntity("update-test", "Old Title", "Old Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        job.setTitle("Updated Title");
        job.setDescription("Updated Description");
        job.setKeywords("updated,keywords");
        job.setResumeCount(5);
        jobDao.updateJob(job);
        
        JobEntity updatedJob = jobDao.getJobById("update-test");
        
        assertEquals("Updated Title", updatedJob.getTitle());
        assertEquals("Updated Description", updatedJob.getDescription());
        assertEquals("updated,keywords", updatedJob.getKeywords());
        assertEquals(5, updatedJob.getResumeCount());
    }

    @Test
    public void testJobDelete() {
        JobEntity job = new JobEntity("delete-test", "Test Job", "Test Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        assertNotNull(jobDao.getJobById("delete-test"));
        
        jobDao.deleteJobById("delete-test");
        
        assertNull(jobDao.getJobById("delete-test"));
    }

    @Test
    public void testJobCount() {
        jobDao.deleteAllJobs();
        
        JobEntity job1 = new JobEntity("job1", "Job 1", "Description 1", "", 0, System.currentTimeMillis());
        JobEntity job2 = new JobEntity("job2", "Job 2", "Description 2", "", 0, System.currentTimeMillis());
        
        jobDao.insertJob(job1);
        jobDao.insertJob(job2);
        
        int count = jobDao.getJobCount();
        assertEquals(2, count);
    }

    @Test
    public void testResumeInsertAndRetrieve() {
        ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Test Job", "2024-01-15", "85", "Test content", System.currentTimeMillis());
        
        resumeDao.insertResume(resume);
        
        ResumeEntity retrievedResume = resumeDao.getResumeById("test-resume");
        
        assertNotNull(retrievedResume);
        assertEquals("test-resume", retrievedResume.getId());
        assertEquals("job1", retrievedResume.getJobId());
        assertEquals("Test Job", retrievedResume.getJobTitle());
        assertEquals("2024-01-15", retrievedResume.getDate());
        assertEquals("85", retrievedResume.getMatchScore());
        assertEquals("Test content", retrievedResume.getResumeText());
    }

    @Test
    public void testResumeUpdate() {
        ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
        resumeDao.insertResume(resume);
        
        resume.setMatchScore("90");
        resume.setResumeText("Updated content");
        resumeDao.updateResume(resume);
        
        ResumeEntity updatedResume = resumeDao.getResumeById("test-resume");
        
        assertEquals("90", updatedResume.getMatchScore());
        assertEquals("Updated content", updatedResume.getResumeText());
    }

    @Test
    public void testResumeDelete() {
        ResumeEntity resume = new ResumeEntity("delete-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
        resumeDao.insertResume(resume);
        
        assertNotNull(resumeDao.getResumeById("delete-resume"));
        
        resumeDao.deleteResumeById("delete-resume");
        
        assertNull(resumeDao.getResumeById("delete-resume"));
    }

    @Test
    public void testResumesForJob() {
        resumeDao.deleteAllResumes();
        
        JobEntity job = new JobEntity("job1", "Test Job", "Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        ResumeEntity resume1 = new ResumeEntity("resume1", "job1", "Test Job", "2024-01-15", "85", "Content 1", System.currentTimeMillis());
        ResumeEntity resume2 = new ResumeEntity("resume2", "job1", "Test Job", "2024-01-16", "90", "Content 2", System.currentTimeMillis());
        ResumeEntity resume3 = new ResumeEntity("resume3", "job2", "Other Job", "2024-01-17", "75", "Content 3", System.currentTimeMillis());
        
        resumeDao.insertResume(resume1);
        resumeDao.insertResume(resume2);
        resumeDao.insertResume(resume3);
        
        List<ResumeEntity> resumesForJob1 = resumeDao.getResumesForJob("job1");
        assertEquals(2, resumesForJob1.size());
        
        List<ResumeEntity> resumesForJob2 = resumeDao.getResumesForJob("job2");
        assertEquals(1, resumesForJob2.size());
    }

    @Test
    public void testStoreProfileInsertAndRetrieve() {
        StoreProfile store = new StoreProfile("test-store", "Test Store", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        
        storeProfileDao.insertStore(store);
        
        StoreProfile retrievedStore = storeProfileDao.getStoreById("test-store");
        
        assertNotNull(retrievedStore);
        assertEquals("test-store", retrievedStore.getId());
        assertEquals("Test Store", retrievedStore.getStoreName());
        assertEquals("123 Test St", retrievedStore.getStoreAddress());
        assertEquals("Test City", retrievedStore.getStoreCity());
        assertEquals("TS", retrievedStore.getStoreState());
        assertEquals("12345", retrievedStore.getStoreZipCode());
        assertEquals("555-1234", retrievedStore.getPhone());
        assertEquals("test@email.com", retrievedStore.getEmail());
        assertEquals("Test description", retrievedStore.getDescription());
    }

    @Test
    public void testStoreProfileUpdate() {
        StoreProfile store = new StoreProfile("test-store", "Old Store Name", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        storeProfileDao.insertStore(store);
        
        store.setStoreName("Updated Store Name");
        store.setStoreAddress("456 Updated St");
        storeProfileDao.updateStore(store);
        
        StoreProfile updatedStore = storeProfileDao.getStoreById("test-store");
        
        assertEquals("Updated Store Name", updatedStore.getStoreName());
        assertEquals("456 Updated St", updatedStore.getStoreAddress());
    }

    @Test
    public void testStoreProfileDelete() {
        StoreProfile store = new StoreProfile("delete-store", "Test Store", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        storeProfileDao.insertStore(store);
        
        assertNotNull(storeProfileDao.getStoreById("delete-store"));
        
        storeProfileDao.deleteStoreById("delete-store");
        
        assertNull(storeProfileDao.getStoreById("delete-store"));
    }
} 