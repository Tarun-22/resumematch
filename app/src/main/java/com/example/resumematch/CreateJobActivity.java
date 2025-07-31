package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

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

        // Check if template data is provided
        String templateTitle = getIntent().getStringExtra("title");
        String templateDescription = getIntent().getStringExtra("description");
        
        if (templateTitle != null && templateDescription != null) {
            // Pre-fill the form with template data
            jobTitleInput.setText(templateTitle);
            jobDescriptionInput.setText(templateDescription);
        }

        // Set up click listeners
        backArrowbutton.setImageResource(R.drawable.back_arrow_black);
        backArrowbutton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> {
            // Show confirmation dialog
            showCancelConfirmationDialog();
        });

        createJobButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            if (!title.isEmpty()) {
                // Show progress with Snackbar
                Snackbar.make(createJobButton, "Creating job...", Snackbar.LENGTH_SHORT).show();
                
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
                            // Show success Toast
                            Toast.makeText(CreateJobActivity.this, "Job created successfully!", Toast.LENGTH_SHORT).show();
                            
                            // Show success Snackbar
                            Snackbar.make(createJobButton, "Job '" + title + "' has been created!", Snackbar.LENGTH_LONG).show();
                            
                            finish();
                        });
                    }
                });
            } else {
                // Show error Toast
                Toast.makeText(this, "Job title is required!", Toast.LENGTH_SHORT).show();
                jobTitleInput.setError("Job title required");
            }
        });
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Job Creation")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    Toast.makeText(this, "Job creation cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Continue Editing", (dialog, which) -> {
                    // Do nothing, just dismiss dialog
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
}