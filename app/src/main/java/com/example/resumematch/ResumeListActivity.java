package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResumeListActivity extends AppCompatActivity {

    ImageView backArrow;
    Button buttonAddResume;
    TextView textJobTitle;
    RecyclerView recyclerView;
    ResumeAdapter resumeAdapter;
    ArrayList<Resume> resumeList;
    String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_list);

        backArrow = findViewById(R.id.backArrow);
        buttonAddResume = findViewById(R.id.buttonAddResume);
        textJobTitle = findViewById(R.id.textJobTitle);
        recyclerView = findViewById(R.id.recyclerResumes);

        // Get job details from intent
        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            String jobTitle = intent.getStringExtra("jobTitle");
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
            startActivity(scanIntent);
        });

        resumeList = new ArrayList<>();
        resumeList.add(new Resume("RES-001", "2024-01-15", "85% Match", jobId != null ? jobId : "60078"));
        resumeList.add(new Resume("RES-002", "2024-01-14", "72% Match", jobId != null ? jobId : "60054"));
        resumeList.add(new Resume("RES-003", "2024-01-13", "91% Match", jobId != null ? jobId : "60091"));

        Log.d("ResumeListActivity", "Created " + resumeList.size() + " sample resumes");

        resumeAdapter = new ResumeAdapter(resumeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resumeAdapter);
        
        Log.d("ResumeListActivity", "RecyclerView setup complete");
    }
}
