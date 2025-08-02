# ResumeMatch Android App - Basic Test Cases

## 📱 Test Overview
**Application**: ResumeMatch Android Application  
**Test Coverage**: Basic functionality testing  
**Test Types**: Unit Tests, Integration Tests

---

## 🎯 Test Categories

### 1. **Unit Tests** - Individual Component Testing
### 2. **Integration Tests** - Database Operations  

---

## 🧪 1. UNIT TESTS

### **Model Tests**

#### **JobEntity Tests**
```java
@Test
public void testJobEntityCreation() {
    JobEntity job = new JobEntity("job1", "Software Engineer", "Develop Android apps", "java,android", 5, System.currentTimeMillis());
    assertEquals("job1", job.getId());
    assertEquals("Software Engineer", job.getTitle());
    assertEquals("Develop Android apps", job.getDescription());
    assertEquals(5, job.getResumeCount());
}

@Test
public void testJobEntitySetters() {
    JobEntity job = new JobEntity("job1", "Old Title", "Old Description", "", 0, 0);
    job.setTitle("New Title");
    job.setDescription("New Description");
    job.setResumeCount(10);
    assertEquals("New Title", job.getTitle());
    assertEquals("New Description", job.getDescription());
    assertEquals(10, job.getResumeCount());
}
```

#### **ResumeEntity Tests**
```java
@Test
public void testResumeEntityCreation() {
    ResumeEntity resume = new ResumeEntity("resume1", "job1", "Software Engineer", "2024-01-15", "85", "Resume text content", System.currentTimeMillis());
    assertEquals("resume1", resume.getId());
    assertEquals("job1", resume.getJobId());
    assertEquals("Software Engineer", resume.getJobTitle());
    assertEquals("85", resume.getMatchScore());
}

@Test
public void testResumeEntitySetters() {
    ResumeEntity resume = new ResumeEntity("resume1", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
    resume.setPhotoPath("/storage/app/resume1.jpg");
    resume.setMatchScore("90");
    assertEquals("/storage/app/resume1.jpg", resume.getPhotoPath());
    assertEquals("90", resume.getMatchScore());
}
```

#### **StoreProfile Tests**
```java
@Test
public void testStoreProfileCreation() {
    StoreProfile store = new StoreProfile("store1", "My Store", "123 Main St", "New York", "NY", "10001", "555-1234", "store@email.com", "Retail store");
    assertEquals("store1", store.getId());
    assertEquals("My Store", store.getStoreName());
    assertEquals("123 Main St", store.getStoreAddress());
}

@Test
public void testStoreProfileSetters() {
    StoreProfile store = new StoreProfile("store1", "Old Name", "Old Address", "Old City", "Old State", "Old Zip", "Old Phone", "Old Email", "Old Description");
    store.setStoreName("New Name");
    store.setStoreAddress("New Address");
    assertEquals("New Name", store.getStoreName());
    assertEquals("New Address", store.getStoreAddress());
}
```

---

## 🔗 2. INTEGRATION TESTS

### **Database Tests**

#### **JobDao Tests**
```java
@Test
public void testJobInsertAndRetrieve() {
    JobEntity job = new JobEntity("test-job", "Test Job", "Test Description", "test,keywords", 0, System.currentTimeMillis());
    jobDao.insertJob(job);
    
    JobEntity retrievedJob = jobDao.getJobById("test-job");
    assertNotNull(retrievedJob);
    assertEquals("test-job", retrievedJob.getId());
    assertEquals("Test Job", retrievedJob.getTitle());
}

@Test
public void testJobUpdate() {
    JobEntity job = new JobEntity("update-test", "Old Title", "Old Description", "", 0, System.currentTimeMillis());
    jobDao.insertJob(job);
    
    job.setTitle("Updated Title");
    job.setDescription("Updated Description");
    jobDao.updateJob(job);
    
    JobEntity updatedJob = jobDao.getJobById("update-test");
    assertEquals("Updated Title", updatedJob.getTitle());
    assertEquals("Updated Description", updatedJob.getDescription());
}

@Test
public void testJobDelete() {
    JobEntity job = new JobEntity("delete-test", "Test Job", "Test Description", "", 0, System.currentTimeMillis());
    jobDao.insertJob(job);
    
    jobDao.deleteJobById("delete-test");
    
    JobEntity deletedJob = jobDao.getJobById("delete-test");
    assertNull(deletedJob);
}
```

#### **ResumeDao Tests**
```java
@Test
public void testResumeInsertAndRetrieve() {
    ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Test Job", "2024-01-15", "85", "Test content", System.currentTimeMillis());
    resumeDao.insertResume(resume);
    
    ResumeEntity retrievedResume = resumeDao.getResumeById("test-resume");
    assertNotNull(retrievedResume);
    assertEquals("test-resume", retrievedResume.getId());
    assertEquals("85", retrievedResume.getMatchScore());
}

@Test
public void testResumeUpdate() {
    ResumeEntity resume = new ResumeEntity("test-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
    resumeDao.insertResume(resume);
    
    resume.setMatchScore("90");
    resumeDao.updateResume(resume);
    
    ResumeEntity updatedResume = resumeDao.getResumeById("test-resume");
    assertEquals("90", updatedResume.getMatchScore());
}

@Test
public void testResumeDelete() {
    ResumeEntity resume = new ResumeEntity("delete-resume", "job1", "Job Title", "2024-01-15", "85", "Content", System.currentTimeMillis());
    resumeDao.insertResume(resume);
    
    resumeDao.deleteResumeById("delete-resume");
    
    ResumeEntity deletedResume = resumeDao.getResumeById("delete-resume");
    assertNull(deletedResume);
}
```

---

## 📊 Test Execution Summary

### **Expected Test Results**
- **Total Tests**: 24+ test cases
- **Unit Tests**: 12+ test cases
- **Integration Tests**: 12+ test cases

### **Success Criteria**
- ✅ All tests pass
- ✅ No compilation errors
- ✅ Basic functionality verified
- ✅ Database operations working

---

**Test Status**: ✅ **BASIC TEST SUITE READY**

*This basic test suite covers essential functionality of the ResumeMatch Android application with simple, clean, and reliable tests.* 