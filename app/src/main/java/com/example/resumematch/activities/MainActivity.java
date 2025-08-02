package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;
import android.os.Handler;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.fragments.JobListingsFragment;
import com.example.resumematch.fragments.ResumeListFragment;
import com.example.resumematch.fragments.CreateJobFragment;
import com.example.resumematch.utils.Config;
import com.example.resumematch.activities.JobSelectionActivity;
import com.example.resumematch.activities.JobTemplateActivity;
import com.example.resumematch.activities.StoreProfileActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPostedJobs, ivRecentResumes, ivCreateJob, ivScanResume;
    private Button btnPostedJobs, btnRecentResumes, btnCreateJob, btnScanResume;
    private TextView tvPostedJobs, tvRecentResumes, tvCreateJob, tvScanResume;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private DataRepository dataRepository;
    private View fragmentContainer;
    private View activityListSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize Config for API keys
        Config.init(this);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(this);
        
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
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
        
        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar);
        
        // Initialize fragment container and activity list section
        fragmentContainer = findViewById(R.id.fragment_container);
        activityListSection = findViewById(R.id.activity_list_section);
        
        // Set up navigation icon click listeners
        setupNavigationIcons();
        
        // Set up main page section button click listeners
        setupMainPageSections();
        
        // Load counts from database
        loadCountsFromDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings coming soon...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_store_profile) {
            Intent intent = new Intent(MainActivity.this, StoreProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    // Add delay before hiding progress bar
                    new Handler().postDelayed(() -> hideProgressBar(), 2000);
                });
            }
        });
        
        // Load resume count
        dataRepository.getResumeCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer resumeCount) {
                runOnUiThread(() -> {
                    btnRecentResumes.setText("Recent Resumes (" + resumeCount + " scanned)");
                    // Add delay before hiding progress bar
                    new Handler().postDelayed(() -> hideProgressBar(), 2000);
                });
            }
        });
    }
    
    private void hideProgressBar() {
        // Hide progress bar after both counts are loaded
        progressBar.setVisibility(View.GONE);
    }
    
    // Add method to refresh counts
    public void refreshCounts() {
        loadCountsFromDatabase();
    }
    
    private void setupNavigationIcons() {
        // Posted Jobs
        ivPostedJobs.setOnClickListener(v -> {
            highlightNavigationIcon(ivPostedJobs, tvPostedJobs);
            loadFragment(new JobListingsFragment());
        });
        
        // Recent Resumes
        ivRecentResumes.setOnClickListener(v -> {
            highlightNavigationIcon(ivRecentResumes, tvRecentResumes);
            loadFragment(new ResumeListFragment());
        });
        
        // Create Job
        ivCreateJob.setOnClickListener(v -> {
            highlightNavigationIcon(ivCreateJob, tvCreateJob);
            loadFragment(new CreateJobFragment());
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
            loadFragment(new JobListingsFragment());
        });
        
        // Recent Resumes
        btnRecentResumes.setOnClickListener(v -> {
            loadFragment(new ResumeListFragment());
        });
        
        // Create New Job
        btnCreateJob.setOnClickListener(v -> {
            loadFragment(new CreateJobFragment());
        });
        
        // Scan New Resume
        btnScanResume.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadFragment(Fragment fragment) {
        // Show fragment container and hide activity list section
        fragmentContainer.setVisibility(View.VISIBLE);
        activityListSection.setVisibility(View.GONE);
        
        // Load the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void showMainContent() {
        // Show activity list section and hide fragment container
        fragmentContainer.setVisibility(View.GONE);
        activityListSection.setVisibility(View.VISIBLE);
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
    
    private void showJobCreationOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Job")
                .setItems(new String[]{"Use Template", "Create Custom Job"}, (dialog, which) -> {
                    if (which == 0) {
                        // Use template
                        Intent intent = new Intent(MainActivity.this, JobTemplateActivity.class);
                        startActivity(intent);
                    } else {
                        // Create custom job
                        Intent intent = new Intent(MainActivity.this, CreateJobActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_menu_edit)
                .show();
    }
    
    private void showHelpDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("ResumeMatch - Help")
                .setMessage("Author: ResumeMatch Team\n" +
                        "Version: 1.0\n\n" +
                        "How to use:\n" +
                        "1. Set up your store profile first\n" +
                        "2. Create job postings for your store\n" +
                        "3. Scan physical resumes using camera/gallery\n" +
                        "4. View match scores and candidate details\n" +
                        "5. Review all scanned resumes in Recent Resumes\n\n" +
                        "Features:\n" +
                        "• Distance calculation from store to candidate\n" +
                        "• AI-powered resume data extraction\n" +
                        "• Comprehensive scoring system\n" +
                        "• Resume photo storage\n" +
                        "• Manual data editing capabilities")
                .setPositiveButton("Setup Store Profile", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, StoreProfileActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Close", (dialog, which) -> {})
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    
    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the fragment and show main content
            getSupportFragmentManager().popBackStack();
            showMainContent();
            resetNavigationIcons();
        } else {
            super.onBackPressed();
        }
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