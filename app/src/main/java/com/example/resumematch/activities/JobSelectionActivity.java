package com.example.resumematch.activities;

import  android.content.Intent;
import  android.os.Bundle;
import  android.widget.ImageView;
import  android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.adapters.JobSelectionAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;

import java.util.ArrayList;
import java.util.List;

public class JobSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backBtn;
    private TextView txt_title;
    private TextView txt_emptyState;
    private DataRepository dataRepo;
    private List<JobEntity> jobEntities = new ArrayList<>();
    private JobSelectionAdapter jobAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_selection);

        dataRepo = new DataRepository(this);

        backBtn = findViewById(R.id.backButton);
        txt_title = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.recyclerJobs);
        txt_emptyState = findViewById(R.id.emptyStateText);

        txt_title.setText("Select Job for Resume");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapt = new JobSelectionAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(jobAdapt);

        backBtn.setOnClickListener(v -> finish());

        loadJobsFromdb();
    }

    private void loadJobsFromdb() {
        dataRepo.get_all_jobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
            @Override
            public void onResult(List<JobEntity> jobs) {
                runOnUiThread(() -> {
                    jobEntities = jobs;
                    updJobAdapt();
                    updEmptState();
                });
            }
        });
    }

    private void updJobAdapt() {
        jobAdapt = new JobSelectionAdapter(jobEntities, this);
        recyclerView.setAdapter(jobAdapt);
    }

    private void updEmptState() {
        if (jobEntities.isEmpty()) {
            txt_emptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txt_emptyState.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    public void onJobSelected(JobEntity job) {
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
        loadJobsFromdb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
} 