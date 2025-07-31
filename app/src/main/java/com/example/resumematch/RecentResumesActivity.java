package com.example.resumematch;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecentResumesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private TextView titleText;
    private TextView emptyStateText;
    private DataRepository dataRepository;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_resumes);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.recyclerResumes);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set title
        titleText.setText("Recent Resumes");

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resumeAdapter = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapter);

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        // Load resumes from database
        loadResumesFromDatabase();
    }

    private void loadResumesFromDatabase() {
        dataRepository.getAllResumes(new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
            @Override
            public void onResult(List<ResumeEntity> resumes) {
                runOnUiThread(() -> {
                    resumeEntities = resumes;
                    updateResumeAdapter();
                    updateEmptyState();
                });
            }
        });
    }

    private void updateResumeAdapter() {
        resumeAdapter = new RecentResumeAdapter(resumeEntities);
        recyclerView.setAdapter(resumeAdapter);
    }

    private void updateEmptyState() {
        if (resumeEntities.isEmpty()) {
            emptyStateText.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyStateText.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload resumes when returning to this activity
        loadResumesFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 