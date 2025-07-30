package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResumeListActivity extends AppCompatActivity {

    ImageView backArrow;
    Button buttonAddResume;
    TextView textJobTitle;
    RecyclerView recyclerView;
    LinearLayout emptyStateLayout;
    ResumeAdapter resumeAdapter;
    ArrayList<Resume> resumeList;
    String jobId;
    String jobDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_list);

        backArrow = findViewById(R.id.backArrow);
        buttonAddResume = findViewById(R.id.buttonAddResume);
        textJobTitle = findViewById(R.id.textJobTitle);
        recyclerView = findViewById(R.id.recyclerResumes);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Get job details from intent
        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            String jobTitle = intent.getStringExtra("jobTitle");
            jobDescription = intent.getStringExtra("jobDescription");
            Log.d("ResumeListActivity", "Received jobId: " + jobId + ", jobTitle: " + jobTitle);
            if (jobTitle != null) {
                textJobTitle.setText("  " + jobTitle);
            }
        }

        backArrow.setOnClickListener(v -> finish());

        // Add resume button click listener
        buttonAddResume.setOnClickListener(v -> {
            Intent scanIntent = new Intent(ResumeListActivity.this, ScanResumeActivity.class);
            scanIntent.putExtra("jobId", jobId);
            scanIntent.putExtra("jobTitle", textJobTitle.getText().toString().trim());
            scanIntent.putExtra("jobDescription", jobDescription);
            startActivity(scanIntent);
        });

        // Load resumes from storage
        loadResumesForJob();
    }

    private void loadResumesForJob() {
        if (jobId != null) {
            List<Resume> jobResumes = JobStorage.getResumesForJob(jobId);
            resumeList = new ArrayList<>(jobResumes);
            Log.d("ResumeListActivity", "Loaded " + resumeList.size() + " resumes for job: " + jobId);
            
            // Log each resume for debugging
            for (Resume resume : resumeList) {
                Log.d("ResumeListActivity", "Resume: " + resume.getId() + " - " + resume.getMatch() + " - " + resume.getDate());
            }
        } else {
            resumeList = new ArrayList<>();
            Log.d("ResumeListActivity", "No jobId provided, using empty list");
        }

        resumeAdapter = new ResumeAdapter(resumeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resumeAdapter);

        // Show/hide empty state
        updateEmptyState();
        
        Log.d("ResumeListActivity", "RecyclerView setup complete with " + resumeList.size() + " resumes");
    }

    private void updateEmptyState() {
        Log.d("ResumeListActivity", "Updating empty state. Resume count: " + resumeList.size());
        if (resumeList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
                Log.d("ResumeListActivity", "Showing empty state");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
                Log.d("ResumeListActivity", "Hiding empty state");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload resumes when returning from scan activity
        loadResumesForJob();
    }
}
