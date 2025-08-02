package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.adapters.JobPostAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.JobPost;
import com.example.resumematch.activities.CreateJobActivity;
import com.example.resumematch.activities.JobApplicationsActivity;

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

        try {
            // Initialize DataRepository
            dataRepository = new DataRepository(this);

            recyclerView = findViewById(R.id.recyclerJobPosts);
            buttonCreateJob = findViewById(R.id.buttonCreateJob);
            textEmptyState = findViewById(R.id.textEmptyState);
            // backButton = findViewById(R.id.backButton); // This line was removed from the new_code

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            
            // Create adapter with empty list initially
            jobAdapter = new JobPostAdapter(new ArrayList<>());
            recyclerView.setAdapter(jobAdapter);

            // Set up back button
            // backButton.setOnClickListener(v -> finish()); // This line was removed from the new_code

            // Load jobs from database
            loadJobsFromDatabase();

            buttonCreateJob.setOnClickListener(v -> {
                Intent intent = new Intent(EmployerHomeActivity.this, CreateJobActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e("EmployerHome", "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            // Show empty state if there's an error
            if (textEmptyState != null) {
                textEmptyState.setVisibility(TextView.VISIBLE);
                textEmptyState.setText("Error loading jobs. Please try again.");
            }
        }
    }

    private void loadJobsFromDatabase() {
        try {
            Log.d("EmployerHome", "Starting to load jobs from database");
            dataRepository.getAllJobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
                @Override
                public void onResult(List<JobEntity> jobs) {
                    Log.d("EmployerHome", "Database callback received, jobs: " + (jobs != null ? jobs.size() : "null"));
                    runOnUiThread(() -> {
                        try {
                            jobEntities = jobs != null ? jobs : new ArrayList<>();
                            Log.d("EmployerHome", "Loaded " + jobEntities.size() + " jobs from database");
                            
                            // If no jobs, add a test job to see if the UI works
                            if (jobEntities.isEmpty()) {
                                Log.d("EmployerHome", "No jobs found, adding test job");
                                JobEntity testJob = new JobEntity(
                                    "test-1",
                                    "Test Job",
                                    "This is a test job description",
                                    "",
                                    0,
                                    System.currentTimeMillis()
                                );
                                jobEntities.add(testJob);
                            }
                            
                            updateJobAdapter();
                            updateEmptyState();
                        } catch (Exception e) {
                            Log.e("EmployerHome", "Error updating UI: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("EmployerHome", "Error loading jobs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateJobAdapter() {
        try {
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
            
            Log.d("EmployerHome", "Created " + jobPosts.size() + " JobPost objects");
            jobAdapter = new JobPostAdapter(jobPosts);
            recyclerView.setAdapter(jobAdapter);
        } catch (Exception e) {
            Log.e("EmployerHome", "Error updating job adapter: " + e.getMessage());
            e.printStackTrace();
        }
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
