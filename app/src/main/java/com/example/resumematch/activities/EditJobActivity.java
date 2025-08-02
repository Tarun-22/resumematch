package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import android.widget.ImageView;

public class EditJobActivity extends AppCompatActivity {

    private EditText jobTitleInput, jobDescriptionInput;
    private Button updateJobButton, deleteJobButton, cancelButton;
    private ImageView backArrowbutton;
    private DataRepository dataRepository;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Get job details from intent
        jobId = getIntent().getStringExtra("jobId");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobDescription = getIntent().getStringExtra("jobDescription");

        if (jobId == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Connect variables to UI elements
        jobTitleInput = findViewById(R.id.editTextJobTitle);
        jobDescriptionInput = findViewById(R.id.editTextJobDescription);
        updateJobButton = findViewById(R.id.buttonUpdateJob);
        deleteJobButton = findViewById(R.id.buttonDeleteJob);
        cancelButton = findViewById(R.id.buttonCancel);
        backArrowbutton = findViewById(R.id.backArrow);

        // Pre-fill the form with existing data
        jobTitleInput.setText(jobTitle);
        jobDescriptionInput.setText(jobDescription);

        // Set up click listeners
        backArrowbutton.setImageResource(R.drawable.back_arrow_black);
        backArrowbutton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        updateJobButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            if (!title.isEmpty()) {
                // Show progress with Snackbar
                Snackbar.make(updateJobButton, "Updating job...", Snackbar.LENGTH_SHORT).show();
                
                // Get the job from database and update it
                dataRepository.getJobById(jobId, new DataRepository.DatabaseCallback<JobEntity>() {
                    @Override
                    public void onResult(JobEntity jobEntity) {
                        if (jobEntity != null) {
                            jobEntity.setTitle(title);
                            jobEntity.setDescription(desc);
                            
                            dataRepository.updateJob(jobEntity, new DataRepository.DatabaseCallback<Void>() {
                                @Override
                                public void onResult(Void result) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(EditJobActivity.this, "Job updated successfully!", Toast.LENGTH_SHORT).show();
                                        Snackbar.make(updateJobButton, "Job '" + title + "' has been updated!", Snackbar.LENGTH_LONG).show();
                                        finish();
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Job title is required!", Toast.LENGTH_SHORT).show();
                jobTitleInput.setError("Job title required");
            }
        });

        deleteJobButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Get the job and delete it
                    dataRepository.getJobById(jobId, new DataRepository.DatabaseCallback<JobEntity>() {
                        @Override
                        public void onResult(JobEntity jobEntity) {
                            if (jobEntity != null) {
                                dataRepository.deleteJob(jobEntity, new DataRepository.DatabaseCallback<Void>() {
                                    @Override
                                    public void onResult(Void result) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(EditJobActivity.this, "Job deleted successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                                    }
                                });
                            }
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
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