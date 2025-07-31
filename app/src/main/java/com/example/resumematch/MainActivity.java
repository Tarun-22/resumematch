package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPostedJobs, ivRecentResumes, ivCreateJob, ivScanResume;
    private Button btnPostedJobs, btnRecentResumes, btnCreateJob, btnScanResume;
    private TextView tvPostedJobs, tvRecentResumes, tvCreateJob, tvScanResume;
    private ProgressBar progressBar;
    private ImageView helpButton;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(this);
        
        // Initialize navigation icons
        ivPostedJobs = findViewById(R.id.ivPostedJobs);
        ivRecentResumes = findViewById(R.id.ivRecentResumes);
        ivCreateJob = findViewById(R.id.ivCreateJob);
        ivScanResume = findViewById(R.id.ivScanResume);
        
        // Initialize navigation text labels
        tvPostedJobs = findViewById(R.id.tvPostedJobs);
        tvRecentResumes = findViewById(R.id.tvRecentResumes);
        tvCreateJob = findViewById(R.id.tvCreateJob);
        tvScanResume = findViewById(R.id.tvScanResume);
        
        // Initialize main page section buttons
        btnPostedJobs = findViewById(R.id.btnPostedJobs);
        btnRecentResumes = findViewById(R.id.btnRecentResumes);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnScanResume = findViewById(R.id.btnScanResume);
        
        // Initialize progress bar and help button
        progressBar = findViewById(R.id.progressBar);
        helpButton = findViewById(R.id.helpButton);
        
        // Set up navigation icon click listeners
        setupNavigationIcons();
        
        // Set up main page section button click listeners
        setupMainPageSections();
        
        // Set up help button
        helpButton.setOnClickListener(v -> showHelpDialog());
        
        // Load counts from database
        loadCountsFromDatabase();
    }
    
    private void loadCountsFromDatabase() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        
        // Load job count
        dataRepository.getJobCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer jobCount) {
                runOnUiThread(() -> {
                    btnPostedJobs.setText("Posted Jobs (" + jobCount + " active)");
                    hideProgressBar();
                });
            }
        });
        
        // Load resume count
        dataRepository.getResumeCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer resumeCount) {
                runOnUiThread(() -> {
                    btnRecentResumes.setText("Recent Resumes (" + resumeCount + " scanned)");
                    hideProgressBar();
                });
            }
        });
    }
    
    private void hideProgressBar() {
        // Hide progress bar after both counts are loaded
        progressBar.setVisibility(View.GONE);
    }
    
    private void setupNavigationIcons() {
        // Posted Jobs
        ivPostedJobs.setOnClickListener(v -> {
            highlightNavigationIcon(ivPostedJobs, tvPostedJobs);
            Intent intent = new Intent(MainActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
        });
        
        // Recent Resumes
        ivRecentResumes.setOnClickListener(v -> {
            highlightNavigationIcon(ivRecentResumes, tvRecentResumes);
            Intent intent = new Intent(MainActivity.this, RecentResumesActivity.class);
            startActivity(intent);
        });
        
        // Create Job
        ivCreateJob.setOnClickListener(v -> {
            highlightNavigationIcon(ivCreateJob, tvCreateJob);
            Intent intent = new Intent(MainActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });
        
        // Scan Resume
        ivScanResume.setOnClickListener(v -> {
            highlightNavigationIcon(ivScanResume, tvScanResume);
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
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
        ivPostedJobs.setAlpha(0.6f);
        ivRecentResumes.setAlpha(0.6f);
        ivCreateJob.setAlpha(0.6f);
        ivScanResume.setAlpha(0.6f);
        
        // Reset all text colors
        tvPostedJobs.setTextColor(getResources().getColor(android.R.color.black));
        tvRecentResumes.setTextColor(getResources().getColor(android.R.color.black));
        tvCreateJob.setTextColor(getResources().getColor(android.R.color.black));
        tvScanResume.setTextColor(getResources().getColor(android.R.color.black));
    }
    
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ResumeMatch - Help")
                .setMessage("Author: Kumar Sashank\n" +
                        "Version: 1.0\n\n" +
                        "How to use ResumeMatch:\n\n" +
                        "1. Create Jobs: Use 'Create Job' to add new job postings\n" +
                        "2. Scan Resumes: Use 'Scan Resume' to upload and analyze resumes\n" +
                        "3. View Posted Jobs: See all your created job postings\n" +
                        "4. View Recent Resumes: See all scanned resumes with match scores\n\n" +
                        "Features:\n" +
                        "• OCR text extraction from resume images\n" +
                        "• Automatic keyword matching and scoring\n" +
                        "• Local database storage\n" +
                        "• Real-time job and resume counts")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Dialog dismissed
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
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