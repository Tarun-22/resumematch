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

    // JobDao Tests
    @Test
    public void testJobInsertAndRetrieve() {
        // Create test job
        JobEntity job = new JobEntity("test-job", "Test Job", "Test Description", "test,keywords", 0, System.currentTimeMillis());
        
        // Insert job
        jobDao.insertJob(job);
        
        // Retrieve job
        JobEntity retrievedJob = jobDao.getJobById("test-job");
        
        // Verify
        assertNotNull(retrievedJob);
        assertEquals("test-job", retrievedJob.getId());
        assertEquals("Test Job", retrievedJob.getTitle());
        assertEquals("Test Description", retrievedJob.getDescription());
        assertEquals("test,keywords", retrievedJob.getKeywords());
        assertEquals(0, retrievedJob.getResumeCount());
    }

    @Test
    public void testJobUpdate() {
        // Create and insert test job
        JobEntity job = new JobEntity("update-test", "Old Title", "Old Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        // Update job
        job.setTitle("Updated Title");
        job.setDescription("Updated Description");
        job.setKeywords("updated,keywords");
        job.setResumeCount(5);
        jobDao.updateJob(job);
        
        // Retrieve updated job
        JobEntity updatedJob = jobDao.getJobById("update-test");
        
        // Verify updates
        assertEquals("Updated Title", updatedJob.getTitle());
        assertEquals("Updated Description", updatedJob.getDescription());
        assertEquals("updated,keywords", updatedJob.getKeywords());
        assertEquals(5, updatedJob.getResumeCount());
    }

    @Test
    public void testJobDelete() {
        // Create and insert test job
        JobEntity job = new JobEntity("delete-test", "Test Job", "Test Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        // Verify job exists
        assertNotNull(jobDao.getJobById("delete-test"));
        
        // Delete job
        jobDao.deleteJobById("delete-test");
        
        // Verify job is deleted
        assertNull(jobDao.getJobById("delete-test"));
    }

    @Test
    public void testJobCount() {
        // Clear existing jobs
        jobDao.deleteAllJobs();
        
        // Insert test jobs
        JobEntity job1 = new JobEntity("job1", "Job 1", "Description 1", "", 0, System.currentTimeMillis());
        JobEntity job2 = new JobEntity("job2", "Job 2", "Description 2", "", 0, System.currentTimeMillis());
        
        jobDao.insertJob(job1);
        jobDao.insertJob(job2);
        
        // Test count
        int count = jobDao.getJobCount();
        assertEquals(2, count);
    }

    // ResumeDao Tests
    @Test
    public void testResumeInsertAndRetrieve() {
        // Create test resume
        ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Test Job", "2024-01-15", "85", "Test content", System.currentTimeMillis());
        
        // Insert resume
        resumeDao.insertResume(resume);
        
        // Retrieve resume
        ResumeEntity retrievedResume = resumeDao.getResumeById("test-resume");
        
        // Verify
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
        // Create and insert test resume
        ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
        resumeDao.insertResume(resume);
        
        // Update resume
        resume.setMatchScore("90");
        resume.setResumeText("Updated content");
        resumeDao.updateResume(resume);
        
        // Retrieve updated resume
        ResumeEntity updatedResume = resumeDao.getResumeById("test-resume");
        
        // Verify updates
        assertEquals("90", updatedResume.getMatchScore());
        assertEquals("Updated content", updatedResume.getResumeText());
    }

    @Test
    public void testResumeDelete() {
        // Create and insert test resume
        ResumeEntity resume = new ResumeEntity("delete-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
        resumeDao.insertResume(resume);
        
        // Verify resume exists
        assertNotNull(resumeDao.getResumeById("delete-resume"));
        
        // Delete resume
        resumeDao.deleteResumeById("delete-resume");
        
        // Verify resume is deleted
        assertNull(resumeDao.getResumeById("delete-resume"));
    }

    @Test
    public void testResumesForJob() {
        // Clear existing resumes
        resumeDao.deleteAllResumes();
        
        // Create test job
        JobEntity job = new JobEntity("job1", "Test Job", "Description", "", 0, System.currentTimeMillis());
        jobDao.insertJob(job);
        
        // Create test resumes
        ResumeEntity resume1 = new ResumeEntity("resume1", "job1", "Test Job", "2024-01-15", "85", "Content 1", System.currentTimeMillis());
        ResumeEntity resume2 = new ResumeEntity("resume2", "job1", "Test Job", "2024-01-16", "90", "Content 2", System.currentTimeMillis());
        ResumeEntity resume3 = new ResumeEntity("resume3", "job2", "Other Job", "2024-01-17", "75", "Content 3", System.currentTimeMillis());
        
        // Insert resumes
        resumeDao.insertResume(resume1);
        resumeDao.insertResume(resume2);
        resumeDao.insertResume(resume3);
        
        // Test resumes for job1
        List<ResumeEntity> resumesForJob1 = resumeDao.getResumesForJob("job1");
        assertEquals(2, resumesForJob1.size());
        
        // Test resumes for job2
        List<ResumeEntity> resumesForJob2 = resumeDao.getResumesForJob("job2");
        assertEquals(1, resumesForJob2.size());
    }

    // StoreProfileDao Tests
    @Test
    public void testStoreProfileInsertAndRetrieve() {
        // Create test store profile
        StoreProfile store = new StoreProfile("test-store", "Test Store", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        
        // Insert store profile
        storeProfileDao.insertStore(store);
        
        // Retrieve store profile
        StoreProfile retrievedStore = storeProfileDao.getStoreById("test-store");
        
        // Verify
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
        // Create and insert test store profile
        StoreProfile store = new StoreProfile("test-store", "Old Store Name", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        storeProfileDao.insertStore(store);
        
        // Update store profile
        store.setStoreName("Updated Store Name");
        store.setStoreAddress("456 Updated St");
        storeProfileDao.updateStore(store);
        
        // Retrieve updated store profile
        StoreProfile updatedStore = storeProfileDao.getStoreById("test-store");
        
        // Verify updates
        assertEquals("Updated Store Name", updatedStore.getStoreName());
        assertEquals("456 Updated St", updatedStore.getStoreAddress());
    }

    @Test
    public void testStoreProfileDelete() {
        // Create and insert test store profile
        StoreProfile store = new StoreProfile("delete-store", "Test Store", "123 Test St", "Test City", "TS", "12345", "555-1234", "test@email.com", "Test description");
        storeProfileDao.insertStore(store);
        
        // Verify store profile exists
        assertNotNull(storeProfileDao.getStoreById("delete-store"));
        
        // Delete store profile
        storeProfileDao.deleteStoreById("delete-store");
        
        // Verify store profile is deleted
        assertNull(storeProfileDao.getStoreById("delete-store"));
    }
} 