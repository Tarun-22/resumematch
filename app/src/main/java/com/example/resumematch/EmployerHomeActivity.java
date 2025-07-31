package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button buttonCreateJob;
    private JobPostAdapter jobAdapter;
    private TextView textEmptyState;
    private DataRepository dataRepository;
    private List<JobEntity> jobEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        recyclerView = findViewById(R.id.recyclerJobPosts);
        buttonCreateJob = findViewById(R.id.buttonCreateJob);
        textEmptyState = findViewById(R.id.textEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Create adapter with empty list initially
        jobAdapter = new JobPostAdapter(new ArrayList<>());
        recyclerView.setAdapter(jobAdapter);

        // Load jobs from database
        loadJobsFromDatabase();

        buttonCreateJob.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerHomeActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });
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
        // Convert JobEntity to JobPost for the adapter
        List<JobPost> jobPosts = new ArrayList<>();
        for (JobEntity jobEntity : jobEntities) {
            JobPost jobPost = new JobPost(
                jobEntity.getId(),
                jobEntity.getTitle(),
                jobEntity.getDescription(), // Pass the description
                new ArrayList<>(), // keywords (empty for now)
                new ArrayList<>()  // resumes (empty for now)
            );
            jobPost.setResumeCount(jobEntity.getResumeCount());
            jobPosts.add(jobPost);
        }
        
        jobAdapter = new JobPostAdapter(jobPosts);
        recyclerView.setAdapter(jobAdapter);
    }

    private void updateEmptyState() {
        if (jobEntities.isEmpty()) {
            textEmptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            textEmptyState.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
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
