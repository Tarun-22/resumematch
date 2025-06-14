package com.example.resumematch;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_list);

        backArrow = findViewById(R.id.backArrow);
        buttonAddResume = findViewById(R.id.buttonAddResume);
        textJobTitle = findViewById(R.id.textJobTitle);
        recyclerView = findViewById(R.id.recyclerResumes);

        backArrow.setOnClickListener(v -> finish());

        resumeList = new ArrayList<>();
        resumeList.add(new Resume("RES-001", "2024-01-15", "85% Match"));
        resumeList.add(new Resume("RES-002", "2024-01-14", "72% Match"));
        resumeList.add(new Resume("RES-003", "2024-01-13", "91% Match"));

        resumeAdapter = new ResumeAdapter(resumeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resumeAdapter);
    }
}
