package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import android.widget.ImageView;

public class EditJobActivity extends AppCompatActivity {

    //declaring the variables here
    private EditText job, jd;
    private Button update, deleteJobButton, cancelButton;
    private ImageView back;
    private DataRepository dataRepository;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        //created data repo
        dataRepository = new DataRepository(this);

        jobId = getIntent().getStringExtra("jobId");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobDescription = getIntent().getStringExtra("jobDescription");

        if (jobId == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //connecting variables with UI elements
        job = findViewById(R.id.editTextJobTitle);
        jd = findViewById(R.id.editTextJobDescription);
        update = findViewById(R.id.buttonUpdateJob);
        deleteJobButton = findViewById(R.id.buttonDeleteJob);
        cancelButton = findViewById(R.id.buttonCancel);
        back = findViewById(R.id.backArrow);

        job.setText(jobTitle);
        jd.setText(jobDescription);

        back.setImageResource(R.drawable.back_arrow_black);
        back.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        update.setOnClickListener(v -> {
            String title = job.getText().toString().trim();
            String desc = jd.getText().toString().trim();

            if (!title.isEmpty()) {
                Snackbar.make(update, "Updating job...", Snackbar.LENGTH_SHORT).show();
                
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
                                        Snackbar.make(update, "Job '" + title + "' has been updated!", Snackbar.LENGTH_LONG).show();
                                        finish();
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(EditJobActivity.this, "Job not found in database", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Job title is required!", Toast.LENGTH_SHORT).show();
                job.setError("Job title required");
            }
        });

        deleteJobButton.setOnClickListener(v -> {
            delete_dia();
        });
    }

    private void delete_dia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
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