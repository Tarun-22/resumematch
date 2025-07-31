package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textEmptyState;
    private Toolbar toolbar;
    private DataRepository dataRepository;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapter;
    private String jobId;
    private String jobTitle;
    private String currentSortBy = "score"; // "score", "distance", or "date"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applications);

        // Get job details from intent
        jobId = getIntent().getStringExtra("jobId");
        jobTitle = getIntent().getStringExtra("jobTitle");

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerApplications);
        textEmptyState = findViewById(R.id.textEmptyState);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Applications for " + jobTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resumeAdapter = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapter);

        // Load resumes for this specific job
        loadResumesForJob();
    }

    private void loadResumesForJob() {
        if (jobId == null) {
            Log.e("JobApplications", "Job ID is null");
            return;
        }

        dataRepository.getResumesForJob(jobId, new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
            @Override
            public void onResult(List<ResumeEntity> resumes) {
                runOnUiThread(() -> {
                    resumeEntities = resumes != null ? resumes : new ArrayList<>();
                    sortResumes();
                    updateResumeAdapter();
                    updateEmptyState();
                });
            }
        });
    }

    private void sortResumes() {
        if (resumeEntities.isEmpty()) return;

        switch (currentSortBy) {
            case "score":
                // Sort by score (highest first)
                Collections.sort(resumeEntities, (r1, r2) -> {
                    int score1 = Integer.parseInt(r1.getMatchScore().replace("%", ""));
                    int score2 = Integer.parseInt(r2.getMatchScore().replace("%", ""));
                    return Integer.compare(score2, score1); // Descending order
                });
                break;
                
            case "distance":
                // Sort by distance (closest first)
                // For now, we'll sort by score as a proxy since distance isn't stored separately
                // In a real app, you'd store distance in the ResumeEntity
                Collections.sort(resumeEntities, (r1, r2) -> {
                    int score1 = Integer.parseInt(r1.getMatchScore().replace("%", ""));
                    int score2 = Integer.parseInt(r2.getMatchScore().replace("%", ""));
                    return Integer.compare(score1, score2); // Ascending order for distance proxy
                });
                break;
                
            case "date":
                // Sort by date added (newest first)
                Collections.sort(resumeEntities, (r1, r2) -> {
                    return Long.compare(r2.getCreatedAt(), r1.getCreatedAt()); // Descending order
                });
                break;
        }
    }

    private void updateResumeAdapter() {
        resumeAdapter.updateResumeList(resumeEntities);
    }

    private void updateEmptyState() {
        if (resumeEntities.isEmpty()) {
            textEmptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            textEmptyState.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.job_applications_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_score) {
            currentSortBy = "score";
            sortResumes();
            updateResumeAdapter();
            Snackbar.make(findViewById(android.R.id.content), "Sorted by Score", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_sort_distance) {
            currentSortBy = "distance";
            sortResumes();
            updateResumeAdapter();
            Snackbar.make(findViewById(android.R.id.content), "Sorted by Distance", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_sort_date) {
            currentSortBy = "date";
            sortResumes();
            updateResumeAdapter();
            Snackbar.make(findViewById(android.R.id.content), "Sorted by Date Added", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload resumes when returning to this activity
        loadResumesForJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 