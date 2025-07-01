package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

public class CreateJobActivity extends AppCompatActivity {

    EditText jobTitleInput, jobDescriptionInput;
    Button createButton, cancelButton;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        jobTitleInput = findViewById(R.id.editTextJobTitle);
        jobDescriptionInput = findViewById(R.id.editTextJobDescription);
        createButton = findViewById(R.id.buttonCreateJob);
        cancelButton = findViewById(R.id.buttonCancel);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setImageResource(R.drawable.back_arrow_black);
        backArrow.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        createButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            if (!title.isEmpty()) {
                JobPost newJob = new JobPost(UUID.randomUUID().toString(), title, desc, new ArrayList<String>(), new ArrayList<Resume>());
                JobStorage.addJob(newJob);
                finish();

            } else {
                jobTitleInput.setError("Job title required");
            }
        });
    }
}
