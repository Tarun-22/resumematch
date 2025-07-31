package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView ivJobManagement, ivResumeScanning, ivApplications, ivProfileSettings;
    TextView tvJobManagement, tvResumeScanning, tvApplications, tvProfileSettings;
    Button btnPostedJobs, btnRecentResumes, btnCreateJob, btnScanResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivJobManagement = findViewById(R.id.ivJobManagement);
        ivResumeScanning = findViewById(R.id.ivResumeScanning);
        ivApplications = findViewById(R.id.ivApplications);
        ivProfileSettings = findViewById(R.id.ivProfileSettings);

        tvJobManagement = findViewById(R.id.tvJobManagement);
        tvResumeScanning = findViewById(R.id.tvResumeScanning);
        tvApplications = findViewById(R.id.tvApplications);
        tvProfileSettings = findViewById(R.id.tvProfileSettings);

        btnPostedJobs = findViewById(R.id.btnPostedJobs);
        btnRecentResumes = findViewById(R.id.btnRecentResumes);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnScanResume = findViewById(R.id.btnScanResume);

        setupNavigationIcons();
        setupMainButtons();
    }

    private void setupNavigationIcons() {
        ivJobManagement.setOnClickListener(v -> {
            updateActiveNavigation(ivJobManagement, tvJobManagement);
            startActivity(new Intent(this, JobManagementActivity.class));
        });

        ivResumeScanning.setOnClickListener(v -> {
            updateActiveNavigation(ivResumeScanning, tvResumeScanning);
            startActivity(new Intent(this, ScanResumeActivity.class));
        });

        ivApplications.setOnClickListener(v -> {
            updateActiveNavigation(ivApplications, tvApplications);
            startActivity(new Intent(this, ResumeListActivity.class));
        });

        ivProfileSettings.setOnClickListener(v -> {
            updateActiveNavigation(ivProfileSettings, tvProfileSettings);
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void setupMainButtons() {
        btnPostedJobs.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerHomeActivity.class));
        });

        btnRecentResumes.setOnClickListener(v -> {
            startActivity(new Intent(this, ResumeListActivity.class));
        });

        btnCreateJob.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateJobActivity.class));
        });

        btnScanResume.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanResumeActivity.class));
        });
    }

    private void updateActiveNavigation(ImageView selectedIcon, TextView selectedText) {
        resetNavigationUI();
        selectedIcon.setAlpha(1.0f);
        selectedText.setTextColor(getResources().getColor(R.color.primary_blue));
    }

    private void resetNavigationUI() {
        ivJobManagement.setAlpha(0.6f);
        ivResumeScanning.setAlpha(0.6f);
        ivApplications.setAlpha(0.6f);
        ivProfileSettings.setAlpha(0.6f);

        int defaultColor = getResources().getColor(android.R.color.black);
        tvJobManagement.setTextColor(defaultColor);
        tvResumeScanning.setTextColor(defaultColor);
        tvApplications.setTextColor(defaultColor);
        tvProfileSettings.setTextColor(defaultColor);
    }
}
