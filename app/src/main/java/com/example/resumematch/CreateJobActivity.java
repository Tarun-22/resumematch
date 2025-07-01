package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

public class CreateJobActivity extends AppCompatActivity {


    //here we creating the variables for button,camera, and  text for preview
    EditText jobTitleInput, jobDescriptionInput;
    Button createJobButton, cancelButton;
    ImageView backArrowbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        //connecting variables to ui elements
        jobTitleInput = findViewById(R.id.editTextJobTitle);
        jobDescriptionInput = findViewById(R.id.editTextJobDescription);
        createJobButton = findViewById(R.id.buttonCreateJob);
        cancelButton = findViewById(R.id.buttonCancel);
        backArrowbutton = findViewById(R.id.backArrow);

        //setting events
        backArrowbutton.setImageResource(R.drawable.back_arrow_black);
        backArrowbutton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        createJobButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            // will adding newJob to JobStorage
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
