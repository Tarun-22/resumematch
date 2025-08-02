package com.example.resumematch.activities;

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
import java.util.List;

import com.example.resumematch.R;
import com.example.resumematch.adapters.RecentResumeAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;

public class JobApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtEmptyState;
    private Toolbar tool_bar;
    private DataRepository dataRepo;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapt;
    private String jobId;
    private String job_title;
    private String currentSortBy = "score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applications);

        jobId = getIntent().getStringExtra("jobId");
        job_title = getIntent().getStringExtra("jobTitle");

        dataRepo = new DataRepository(this);

        tool_bar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerApplications);
        txtEmptyState = findViewById(R.id.textEmptyState);

        setSupportActionBar(tool_bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Applications for " + job_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resumeAdapt = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapt);

        loadResumesForJob();
    }

    private void loadResumesForJob() {
        if (jobId == null) {
            Log.e("JobApplications", "Job ID is null");
            return;
        }

        dataRepo.getResumesForJob(jobId, new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
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
                Collections.sort(resumeEntities, (r1, r2) -> {
                    int score1 = Integer.parseInt(r1.getMatchScore().replace("%", ""));
                    int score2 = Integer.parseInt(r2.getMatchScore().replace("%", ""));
                    return Integer.compare(score2, score1); // Descending order
                });
                break;
                
            case "distance":

                Collections.sort(resumeEntities, (r1, r2) -> {
                    int score1 = Integer.parseInt(r1.getMatchScore().replace("%", ""));
                    int score2 = Integer.parseInt(r2.getMatchScore().replace("%", ""));
                    return Integer.compare(score1, score2); // Ascending order for distance proxy
                });
                break;
                
            case "date":
                Collections.sort(resumeEntities, (r1, r2) -> {
                    return Long.compare(r2.getCreatedAt(), r1.getCreatedAt()); // Descending order
                });
                break;
        }
    }

    private void updateResumeAdapter() {
        resumeAdapt.updateResumeList(resumeEntities);
    }

    private void updateEmptyState() {
        if (resumeEntities.isEmpty()) {
            txtEmptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txtEmptyState.setVisibility(TextView.GONE);
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
        loadResumesForJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
} 