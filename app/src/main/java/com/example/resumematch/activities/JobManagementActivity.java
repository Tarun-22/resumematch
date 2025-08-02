package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;

public class JobManagementActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView txt_title;
    private Button btnCreateJob, btnViewJobs, btnEditJobs, btnDeleteJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_management);

        backBtn = findViewById(R.id.backButton);
        txt_title = findViewById(R.id.titleText);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnViewJobs = findViewById(R.id.btnViewJobs);
        btnEditJobs = findViewById(R.id.btnEditJobs);
        btnDeleteJobs = findViewById(R.id.btnDeleteJobs);

        txt_title.setText("Job Management");

        backBtn.setOnClickListener(v -> finish());

        btnCreateJob.setOnClickListener(v -> {
            Intent intent = new Intent(JobManagementActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });

        btnViewJobs.setOnClickListener(v -> {
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
        });

        btnEditJobs.setOnClickListener(v -> {
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Select a job to edit", Toast.LENGTH_SHORT).show();
        });

        btnDeleteJobs.setOnClickListener(v -> {
            Intent intent = new Intent(JobManagementActivity.this, EmployerHomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Select a job to delete", Toast.LENGTH_SHORT).show();
        });
    }
} 