package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JobManagementActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView titleText;
    private Button btnCreateJob, btnViewJobs, btnEditJobs, btnDeleteJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_management);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnViewJobs = findViewById(R.id.btnViewJobs);
        btnEditJobs = findViewById(R.id.btnEditJobs);
        btnDeleteJobs = findViewById(R.id.btnDeleteJobs);

        // Set title
        titleText.setText("Job Management");

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        btnCreateJob.setOnClickListener(v -> {
            // Navigate to create job activity
            Intent intent = new Intent(JobManagementActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });

        btnViewJobs.setOnClickListener(v -> {
            // Navigate to view posted jobs
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
        });

        btnEditJobs.setOnClickListener(v -> {
            // Navigate to edit jobs (same as view for now)
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Select a job to edit", Toast.LENGTH_SHORT).show();
        });

        btnDeleteJobs.setOnClickListener(v -> {
            // Navigate to delete jobs (same as view for now)
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Select a job to delete", Toast.LENGTH_SHORT).show();
        });
    }
} 