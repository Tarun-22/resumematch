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

public class MainActivity extends AppCompatActivity {

    private ImageView jobs, recentresumes, createjob, scanresume;
    private Button btn1, btn2, btn3, btn4;
    private TextView jobspost, text_recent, text_create, text_scan;
    private ProgressBar progress;
    private Toolbar toolbar;
    private DataRepository dataRepository;
    private View fragmentContainer;
    private View activitylistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Config.init(this);
        
        dataRepository = new DataRepository(this);
        
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        jobs = findViewById(R.id.ivPostedJobs);
        recentresumes = findViewById(R.id.ivRecentResumes);
        createjob = findViewById(R.id.ivCreateJob);
        scanresume = findViewById(R.id.ivScanResume);
        
        jobspost = findViewById(R.id.tvPostedJobs);
        text_recent = findViewById(R.id.tvRecentResumes);
        text_create = findViewById(R.id.tvCreateJob);
        text_scan = findViewById(R.id.tvScanResume);
        
        btn1 = findViewById(R.id.btnPostedJobs);
        btn2 = findViewById(R.id.btnRecentResumes);
        btn3 = findViewById(R.id.btnCreateJob);
        btn4 = findViewById(R.id.btnScanResume);
        
        progress = findViewById(R.id.progressBar);
        
        fragmentContainer = findViewById(R.id.fragment_container);
        activitylistener = findViewById(R.id.activity_list_section);
        
        setupicons();
        
        mainpagesetup();
        
        countloading();
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
    
    private void countloading() {
        progress.setVisibility(View.VISIBLE);
        
        dataRepository.getJobCount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer jobCount) {
                runOnUiThread(() -> {
                    btn1.setText("Posted Jobs (" + jobCount + " active)");
                    // Add delay before hiding progress bar
                    new Handler().postDelayed(() -> hidebar(), 2000);
                });
            }
        });
        
        dataRepository.getcount(new DataRepository.DatabaseCallback<Integer>() {
            @Override
            public void onResult(Integer resumeCount) {
                runOnUiThread(() -> {
                    btn2.setText("Recent Resumes (" + resumeCount + " scanned)");
                    new Handler().postDelayed(() -> hidebar(), 2000);
                });
            }
        });
    }
    
    private void hidebar() {
        progress.setVisibility(View.GONE);
    }
    
    public void refreshCounts() {
        countloading();
    }
    
    private void setupicons() {
        jobs.setOnClickListener(v -> {
            highlighticons(jobs, jobspost);
            loadFragment(new JobListingsFragment());
        });
        
        recentresumes.setOnClickListener(v -> {
            highlighticons(recentresumes, text_recent);
            loadFragment(new ResumeListFragment());
        });
        
        createjob.setOnClickListener(v -> {
            highlighticons(createjob, text_create);
            loadFragment(new CreateJobFragment());
        });
        
        scanresume.setOnClickListener(v -> {
            highlighticons(scanresume, text_scan);
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    private void mainpagesetup() {
        btn1.setOnClickListener(v -> {
            loadFragment(new JobListingsFragment());
        });
        
        btn2.setOnClickListener(v -> {
            loadFragment(new ResumeListFragment());
        });
        
        btn3.setOnClickListener(v -> {
            loadFragment(new CreateJobFragment());
        });
        
        btn4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JobSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadFragment(Fragment fragment) {
        // Show fragment container and hide activity list section
        fragmentContainer.setVisibility(View.VISIBLE);
        activitylistener.setVisibility(View.GONE);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void showMainContent() {
        fragmentContainer.setVisibility(View.GONE);
        activitylistener.setVisibility(View.VISIBLE);
    }
    
    private void highlighticons(ImageView selectedIcon, TextView selectedText) {
        resetNavigationIcons();
        
        selectedIcon.setAlpha(1.0f);
        selectedText.setTextColor(getResources().getColor(R.color.primary_blue));
    }
    
    private void resetNavigationIcons() {
        jobs.setAlpha(0.6f);
        recentresumes.setAlpha(0.6f);
        createjob.setAlpha(0.6f);
        scanresume.setAlpha(0.6f);
        
        jobspost.setTextColor(getResources().getColor(android.R.color.black));
        text_recent.setTextColor(getResources().getColor(android.R.color.black));
        text_create.setTextColor(getResources().getColor(android.R.color.black));
        text_scan.setTextColor(getResources().getColor(android.R.color.black));
    }
    
    private void showJobCreationOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Job")
                .setItems(new String[]{"Use Template", "Create Custom Job"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(MainActivity.this, JobTemplateActivity.class);
                        startActivity(intent);
                    } else {
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
        countloading();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
}