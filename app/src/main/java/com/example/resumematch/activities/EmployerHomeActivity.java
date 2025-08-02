package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.adapters.job_post_adapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.JobPost;

import java.util.ArrayList;
import java.util.List;

public class EmployerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnCreateJob;
    private job_post_adapter jobAdapt;
    private TextView txt_state;
    private DataRepository dataRepo;
    private List<JobEntity> jobEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        try {
            dataRepo = new DataRepository(this);

            recyclerView = findViewById(R.id.recyclerJobPosts);
            btnCreateJob = findViewById(R.id.buttonCreateJob);
            txt_state = findViewById(R.id.textEmptyState);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            
            jobAdapt = new job_post_adapter(new ArrayList<>());
            recyclerView.setAdapter(jobAdapt);


            loadJobsFromDatabase();

            btnCreateJob.setOnClickListener(v -> {
                Intent intent = new Intent(EmployerHomeActivity.this, CreateJobActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e("EmployerHome", "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            if (txt_state != null) {
                txt_state.setVisibility(TextView.VISIBLE);
                txt_state.setText("Error loading jobs. Please try again.");
            }
        }
    }

    private void loadJobsFromDatabase() {
        try {
            Log.d("EmployerHome", "Starting to load jobs from database");
            dataRepo.get_all_jobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
                @Override
                public void onResult(List<JobEntity> jobs) {
                    Log.d("EmployerHome", "Database callback received, jobs: " + (jobs != null ? jobs.size() : "null"));
                    runOnUiThread(() -> {
                        try {
                            jobEntities = jobs != null ? jobs : new ArrayList<>();
                            Log.d("EmployerHome", "Loaded " + jobEntities.size() + " jobs from database");
                            
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
            List<JobPost> jobPosts = new ArrayList<>();
            for (JobEntity jobEntity : jobEntities) {
                JobPost jobPost = new JobPost(
                    jobEntity.getId(),
                    jobEntity.getTitle(),
                    jobEntity.getDescription(),
                    new ArrayList<>(),
                    new ArrayList<>()
                );
                jobPost.setResumeCount(jobEntity.getResumeCount());
                jobPosts.add(jobPost);
            }
            
            Log.d("EmployerHome", "Created " + jobPosts.size() + " JobPost objects");
            jobAdapt = new job_post_adapter(jobPosts);
            recyclerView.setAdapter(jobAdapt);
        } catch (Exception e) {
            Log.e("EmployerHome", "Error updating job adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        if (jobEntities.isEmpty()) {
            txt_state.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txt_state.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobsFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
}
