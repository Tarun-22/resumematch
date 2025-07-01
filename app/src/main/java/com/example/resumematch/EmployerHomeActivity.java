package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        jobAdapter.notifyDataSetChanged();
    }
}
