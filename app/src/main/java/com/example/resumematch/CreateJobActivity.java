package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class CreateJobActivity extends AppCompatActivity {

    private EditText jobTitleInput, jobDescriptionInput;
    private Button createJobButton, cancelButton;
    private ImageView backArrowbutton;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Connect variables to UI elements
        jobTitleInput = findViewById(R.id.editTextJobTitle);
        jobDescriptionInput = findViewById(R.id.editTextJobDescription);
        createJobButton = findViewById(R.id.buttonCreateJob);
        cancelButton = findViewById(R.id.buttonCancel);
        backArrowbutton = findViewById(R.id.backArrow);

        // Set up click listeners
        backArrowbutton.setImageResource(R.drawable.back_arrow_black);
        backArrowbutton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        createJobButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            if (!title.isEmpty()) {
                // Create JobEntity and save to database
                String jobId = UUID.randomUUID().toString();
                JobEntity newJob = new JobEntity(
                    jobId,
                    title,
                    desc,
                    "", // keywords (empty for now)
                    0,  // resumeCount
                    System.currentTimeMillis() // createdAt
                );

                dataRepository.insertJob(newJob, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        runOnUiThread(() -> {
                            Toast.makeText(CreateJobActivity.this, "Job created successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            } else {
                jobTitleInput.setError("Job title required");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
}