package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView ivJobManagement, ivResumeScanning, ivApplications, ivProfileSettings;
    private Button btnPostedJobs, btnRecentResumes, btnCreateJob, btnScanResume;
    private TextView tvJobManagement, tvResumeScanning, tvApplications, tvProfileSettings;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(this);
        
        // Initialize navigation icons
        ivJobManagement = findViewById(R.id.ivJobManagement);
        ivResumeScanning = findViewById(R.id.ivResumeScanning);
        ivApplications = findViewById(R.id.ivApplications);
        ivProfileSettings = findViewById(R.id.ivProfileSettings);
        
        // Initialize navigation text labels
        tvJobManagement = findViewById(R.id.tvJobManagement);
        tvResumeScanning = findViewById(R.id.tvResumeScanning);
        tvApplications = findViewById(R.id.tvApplications);
        tvProfileSettings = findViewById(R.id.tvProfileSettings);
        
        // Initialize main page section buttons
        btnPostedJobs = findViewById(R.id.btnPostedJobs);
        btnRecentResumes = findViewById(R.id.btnRecentResumes);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnScanResume = findViewById(R.id.btnScanResume);
        
        // Set up navigation icon click listeners
        setupNavigationIcons();
        
        // Set up main page section button click listeners
        setupMainPageSections();
        
        // Load counts from database
        loadCountsFromDatabase();
    }
    
    private void loadCountsFromDatabase() {
        // Load job count
        dataRepository.getJobCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer jobCount) {
                runOnUiThread(() -> {
                    btnPostedJobs.setText("Posted Jobs (" + jobCount + " active)");
                });
            }
        });
        
        // Load resume count
        dataRepository.getResumeCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer resumeCount) {
                runOnUiThread(() -> {
                    btnRecentResumes.setText("Recent Resumes (" + resumeCount + " scanned)");
                });
            }
        });
    }
    
    private void setupNavigationIcons() {
        // Job Management
        ivJobManagement.setOnClickListener(v -> {
            highlightNavigationIcon(ivJobManagement, tvJobManagement);
            // Navigate to job management section
            Intent intent = new Intent(MainActivity.this, JobManagementActivity.class);
            startActivity(intent);
        });
        
        // Resume Scanning
        ivResumeScanning.setOnClickListener(v -> {
            highlightNavigationIcon(ivResumeScanning, tvResumeScanning);
            // Navigate to job selection for resume scanning
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
            startActivity(intent);
        });
        
        // Applications
        ivApplications.setOnClickListener(v -> {
            highlightNavigationIcon(ivApplications, tvApplications);
            // Navigate to applications section (ResumeListActivity for now)
            Intent intent = new Intent(MainActivity.this, ResumeListActivity.class);
            startActivity(intent);
        });
        
        // Profile & Settings
        ivProfileSettings.setOnClickListener(v -> {
            highlightNavigationIcon(ivProfileSettings, tvProfileSettings);
            // Navigate to profile section
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupMainPageSections() {
        // Posted Jobs
        btnPostedJobs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
        });
        
        // Recent Resumes
        btnRecentResumes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecentResumesActivity.class);
            startActivity(intent);
        });
        
        // Create New Job
        btnCreateJob.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });
        
        // Scan New Resume
        btnScanResume.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    private void highlightNavigationIcon(ImageView selectedIcon, TextView selectedText) {
        // Reset all icons to default state
        resetNavigationIcons();
        
        // Highlight selected icon and text
        selectedIcon.setAlpha(1.0f);
        selectedText.setTextColor(getResources().getColor(R.color.primary_blue));
    }
    
    private void resetNavigationIcons() {
        // Reset all icons to default state
        ivJobManagement.setAlpha(0.6f);
        ivResumeScanning.setAlpha(0.6f);
        ivApplications.setAlpha(0.6f);
        ivProfileSettings.setAlpha(0.6f);
        
        // Reset all text colors
        tvJobManagement.setTextColor(getResources().getColor(android.R.color.black));
        tvResumeScanning.setTextColor(getResources().getColor(android.R.color.black));
        tvApplications.setTextColor(getResources().getColor(android.R.color.black));
        tvProfileSettings.setTextColor(getResources().getColor(android.R.color.black));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload counts when returning to main activity
        loadCountsFromDatabase();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
}