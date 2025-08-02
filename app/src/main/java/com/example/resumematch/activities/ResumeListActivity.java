package com.example.resumematch.activities;

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

import com.example.resumematch.R;
import com.example.resumematch.adapters.ResumeAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;

public class ResumeListActivity extends AppCompatActivity {

    ImageView backarrow;
    Button btnaddresume;
    TextView txt_jobtitle;
    RecyclerView recyclerView;
    LinearLayout emptyStateLayout;
    ResumeAdapter resumeAdapt;
    ArrayList<ResumeEntity> resumeList;
    String jobId;
    String jobDescription;
    DataRepository dataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_list);

        resumeList = new ArrayList<>();
        dataRepo = new DataRepository(this);

        backarrow = findViewById(R.id.backArrow);
        btnaddresume = findViewById(R.id.buttonAddResume);
        txt_jobtitle = findViewById(R.id.textJobTitle);
        recyclerView = findViewById(R.id.recyclerResumes);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            String jobTitle = intent.getStringExtra("jobTitle");
            jobDescription = intent.getStringExtra("jobDescription");
            Log.d("ResumeListActivity", "Received jobId: " + jobId + ", jobTitle: " + jobTitle);
            if (jobTitle != null) {
                txt_jobtitle.setText("  " + jobTitle);
            }
        }

        backarrow.setOnClickListener(v -> finish());

        btnaddresume.setOnClickListener(v -> {
            Intent scanIntent = new Intent(ResumeListActivity.this, ScanResumeActivity.class);
            scanIntent.putExtra("jobId", jobId);
            scanIntent.putExtra("jobTitle", txt_jobtitle.getText().toString().trim());
            scanIntent.putExtra("jobDescription", jobDescription);
            startActivity(scanIntent);
        });

        loadresumesforjob();
    }

    private void loadresumesforjob() {
        if (jobId != null) {
            dataRepo.getResumesForJob(jobId, new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
                @Override
                public void onResult(List<ResumeEntity> resumeEntities) {
                    runOnUiThread(() -> {
                        resumeList = new ArrayList<>(resumeEntities);
                        
                        Log.d("ResumeListActivity", "Loaded " + resumeList.size() + " resumes for job: " + jobId);
                        
                        for (ResumeEntity entity : resumeList) {
                            Log.d("ResumeListActivity", "Resume: " + entity.getId() + " - " + entity.getMatchScore() + " - " + entity.getDate());
                        }

                        resumeAdapt = new ResumeAdapter(resumeEntities);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ResumeListActivity.this));
                        recyclerView.setAdapter(resumeAdapt);

                        updateEmptyState();
                        
                        Log.d("ResumeListActivity", "RecyclerView setup complete with " + resumeList.size() + " resumes");
                    });
                }
            });
        } else {
            resumeList = new ArrayList<>();
            Log.d("ResumeListActivity", "No jobId provided, using empty list");
            
            resumeAdapt = new ResumeAdapter(resumeList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(resumeAdapt);
            updateEmptyState();
        }
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
        loadresumesforjob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
}
