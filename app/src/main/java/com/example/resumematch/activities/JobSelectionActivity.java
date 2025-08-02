package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.adapters.JobSelectionAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.activities.ScanResumeActivity;

import java.util.ArrayList;
import java.util.List;

public class JobSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private TextView titleText;
    private TextView emptyStateText;
    private DataRepository dataRepository;
    private List<JobEntity> jobEntities = new ArrayList<>();
    private JobSelectionAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_selection);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.recyclerJobs);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set title
        titleText.setText("Select Job for Resume");

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new JobSelectionAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(jobAdapter);

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        // Load jobs from database
        loadJobsFromDatabase();
    }

    private void loadJobsFromDatabase() {
        dataRepository.getAllJobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
            @Override
            public void onResult(List<JobEntity> jobs) {
                runOnUiThread(() -> {
                    jobEntities = jobs;
                    updateJobAdapter();
                    updateEmptyState();
                });
            }
        });
    }

    private void updateJobAdapter() {
        jobAdapter = new JobSelectionAdapter(jobEntities, this);
        recyclerView.setAdapter(jobAdapter);
    }

    private void updateEmptyState() {
        if (jobEntities.isEmpty()) {
            emptyStateText.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyStateText.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    public void onJobSelected(JobEntity job) {
        // Navigate to ScanResumeActivity with selected job details
        Intent intent = new Intent(JobSelectionActivity.this, ScanResumeActivity.class);
        intent.putExtra("jobId", job.getId());
        intent.putExtra("jobTitle", job.getTitle());
        intent.putExtra("jobDescription", job.getDescription());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload jobs when returning to this activity
        loadJobsFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 