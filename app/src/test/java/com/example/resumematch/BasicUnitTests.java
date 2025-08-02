package com.example.resumematch;

import android.content.Context;

import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.models.StoreProfile;
import com.example.resumematch.utils.Config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class BasicUnitTests {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    // JobEntity Tests
    @Test
    public void testJobEntityCreation() {
        JobEntity job = new JobEntity("job1", "Software Engineer", "Develop Android apps", "java,android", 5, System.currentTimeMillis());
        assertEquals("job1", job.getId());
        assertEquals("Software Engineer", job.getTitle());
        assertEquals("Develop Android apps", job.getDescription());
        assertEquals("java,android", job.getKeywords());
        assertEquals(5, job.getResumeCount());
        assertTrue(job.getCreatedAt() > 0);
    }

    @Test
    public void testJobEntitySetters() {
        JobEntity job = new JobEntity("job1", "Old Title", "Old Description", "", 0, 0);
        job.setTitle("New Title");
        job.setDescription("New Description");
        job.setKeywords("new,keywords");
        job.setResumeCount(10);
        job.setCreatedAt(1234567890L);
        
        assertEquals("New Title", job.getTitle());
        assertEquals("New Description", job.getDescription());
        assertEquals("new,keywords", job.getKeywords());
        assertEquals(10, job.getResumeCount());
        assertEquals(1234567890L, job.getCreatedAt());
    }

    // ResumeEntity Tests
    @Test
    public void testResumeEntityCreation() {
        ResumeEntity resume = new ResumeEntity("resume1", "job1", "Software Engineer", "2024-01-15", "85", "Resume text content", System.currentTimeMillis());
        assertEquals("resume1", resume.getId());
        assertEquals("job1", resume.getJobId());
        assertEquals("Software Engineer", resume.getJobTitle());
        assertEquals("2024-01-15", resume.getDate());
        assertEquals("85", resume.getMatchScore());
        assertEquals("Resume text content", resume.getResumeText());
        assertTrue(resume.getCreatedAt() > 0);
    }

    @Test
    public void testResumeEntitySetters() {
        ResumeEntity resume = new ResumeEntity("resume1", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
        resume.setPhotoPath("/storage/app/resume1.jpg");
        resume.setExtractedDataJson("{\"name\":\"John Doe\",\"email\":\"john@email.com\"}");
        resume.setMatchScore("90");
        resume.setResumeText("Updated content");
        
        assertEquals("/storage/app/resume1.jpg", resume.getPhotoPath());
        assertEquals("{\"name\":\"John Doe\",\"email\":\"john@email.com\"}", resume.getExtractedDataJson());
        assertEquals("90", resume.getMatchScore());
        assertEquals("Updated content", resume.getResumeText());
    }

    // StoreProfile Tests
    @Test
    public void testStoreProfileCreation() {
        StoreProfile store = new StoreProfile("store1", "My Store", "123 Main St", "New York", "NY", "10001", "555-1234", "store@email.com", "Retail store");
        assertEquals("store1", store.getId());
        assertEquals("My Store", store.getStoreName());
        assertEquals("123 Main St", store.getStoreAddress());
        assertEquals("New York", store.getStoreCity());
        assertEquals("NY", store.getStoreState());
        assertEquals("10001", store.getStoreZipCode());
        assertEquals("555-1234", store.getPhone());
        assertEquals("store@email.com", store.getEmail());
        assertEquals("Retail store", store.getDescription());
    }

    @Test
    public void testStoreProfileSetters() {
        StoreProfile store = new StoreProfile("store1", "Old Name", "Old Address", "Old City", "Old State", "Old Zip", "Old Phone", "Old Email", "Old Description");
        store.setStoreName("New Name");
        store.setStoreAddress("New Address");
        store.setStoreCity("New City");
        store.setStoreState("New State");
        store.setStoreZipCode("New Zip");
        store.setPhone("New Phone");
        store.setEmail("New Email");
        store.setDescription("New Description");
        
        assertEquals("New Name", store.getStoreName());
        assertEquals("New Address", store.getStoreAddress());
        assertEquals("New City", store.getStoreCity());
        assertEquals("New State", store.getStoreState());
        assertEquals("New Zip", store.getStoreZipCode());
        assertEquals("New Phone", store.getPhone());
        assertEquals("New Email", store.getEmail());
        assertEquals("New Description", store.getDescription());
    }

    // Config Tests
    @Test
    public void testConfigInitialization() {
        Config.init(context);
        // Verify initialization without errors
        assertNotNull(Config.getOpenAIApiKey());
        assertNotNull(Config.getGoogleMapsApiKey());
    }

    @Test
    public void testApiKeyRetrieval() {
        Config.init(context);
        
        String openaiKey = Config.getOpenAIApiKey();
        String mapsKey = Config.getGoogleMapsApiKey();
        
        assertNotNull(openaiKey);
        assertNotNull(mapsKey);
        assertFalse(openaiKey.isEmpty());
        assertFalse(mapsKey.isEmpty());
    }

    // Simple Validation Tests
    @Test
    public void testJobEntityValidation() {
        JobEntity validJob = new JobEntity("job1", "Valid Job", "Valid Description", "keywords", 0, System.currentTimeMillis());
        assertNotNull(validJob.getId());
        assertFalse(validJob.getTitle().isEmpty());
        assertFalse(validJob.getDescription().isEmpty());
    }

    @Test
    public void testResumeEntityValidation() {
        ResumeEntity validResume = new ResumeEntity("resume1", "job1", "Valid Job", "2024-01-15", "85", "Valid content", System.currentTimeMillis());
        assertNotNull(validResume.getId());
        assertNotNull(validResume.getJobId());
        assertFalse(validResume.getJobTitle().isEmpty());
        assertFalse(validResume.getDate().isEmpty());
        assertFalse(validResume.getMatchScore().isEmpty());
    }

    @Test
    public void testStoreProfileValidation() {
        StoreProfile validStore = new StoreProfile("store1", "Valid Store", "Valid Address", "Valid City", "Valid State", "Valid Zip", "Valid Phone", "Valid Email", "Valid Description");
        assertNotNull(validStore.getId());
        assertFalse(validStore.getStoreName().isEmpty());
        assertFalse(validStore.getStoreAddress().isEmpty());
        assertFalse(validStore.getStoreCity().isEmpty());
        assertFalse(validStore.getStoreState().isEmpty());
        assertFalse(validStore.getStoreZipCode().isEmpty());
    }
} 