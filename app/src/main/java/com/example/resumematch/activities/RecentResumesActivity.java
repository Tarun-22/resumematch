package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.adapters.RecentResumeAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;

import java.util.ArrayList;
import java.util.List;

public class RecentResumesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backBtn;
    private TextView txt_title;
    private TextView txt_emptyState;
    private DataRepository dataRepo;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_resumes);

        dataRepo = new DataRepository(this);

        backBtn = findViewById(R.id.backButton);
        txt_title = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.recyclerResumes);
        txt_emptyState = findViewById(R.id.emptyStateText);

        txt_title.setText("Recent Resumes");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resumeAdapt = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapt);

        backBtn.setOnClickListener(v -> finish());

        loadResumesFromdb();
    }

    private void loadResumesFromdb() {
        dataRepo.getAllResumes(new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
            @Override
            public void onResult(List<ResumeEntity> resumes) {
                runOnUiThread(() -> {
                    resumeEntities = resumes;
                    updateResumeAdapt();
                    updEmptyState();
                });
            }
        });
    }

    private void updateResumeAdapt() {
        resumeAdapt = new RecentResumeAdapter(resumeEntities);
        recyclerView.setAdapter(resumeAdapt);
    }

    private void updEmptyState() {
        if (resumeEntities.isEmpty()) {
            txt_emptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txt_emptyState.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResumesFromdb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
} 