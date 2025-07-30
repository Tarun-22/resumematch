package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.UUID;

public class EmployerHomeActivity extends AppCompatActivity {

    //creating the variables for recyclerview, button and adapter
    private RecyclerView recyclerView;
    private Button buttonCreateJob;
    private JobPostAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        //connecting variables to ui elements
        recyclerView = findViewById(R.id.recyclerJobPosts);
        buttonCreateJob = findViewById(R.id.buttonCreateJob);

        // Add some sample data if the list is empty
        if (JobStorage.jobList.isEmpty()) {
            JobPost sampleJob1 = new JobPost(
                UUID.randomUUID().toString(),
                "Senior Software Engineer",
                "We are looking for a senior software engineer with experience in Java, Spring Boot, React, and REST APIs. The ideal candidate should have knowledge of microservices architecture, Docker, and AWS. Experience with Agile methodologies and Git is required.",
                new ArrayList<>(),
                new ArrayList<>()
            );
            JobPost sampleJob2 = new JobPost(
                UUID.randomUUID().toString(),
                "Product Manager",
                "We are seeking a product manager with strong leadership skills and experience in project management. The candidate should have excellent communication skills and experience working with cross-functional teams. Knowledge of Agile and Scrum methodologies is essential.",
                new ArrayList<>(),
                new ArrayList<>()
            );
            JobStorage.addJob(sampleJob1);
            JobStorage.addJob(sampleJob2);
        }

        Log.d("EmployerHome", "Job list size: " + JobStorage.jobList.size());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new JobPostAdapter(JobStorage.jobList);
        recyclerView.setAdapter(jobAdapter);

        //setting the onclick event for this button to open
        buttonCreateJob.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerHomeActivity.this, CreateJobActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("EmployerHome", "onResume - Job list size: " + JobStorage.jobList.size());
        jobAdapter.notifyDataSetChanged();
    }
}
