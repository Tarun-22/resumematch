package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class CreateJobActivity extends AppCompatActivity {

    private EditText jobInput, jd;
    private Button create, cancel;
    private ImageView back;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        //connecting with ui elements
        dataRepository = new DataRepository(this);
        jobInput = findViewById(R.id.editTextJobTitle);
        jd = findViewById(R.id.editTextJobDescription);
        create = findViewById(R.id.buttonCreateJob);
        cancel = findViewById(R.id.buttonCancel);
        back = findViewById(R.id.backArrow);

        String templateTitle = getIntent().getStringExtra("title");
        String templateDescription = getIntent().getStringExtra("description");
        
        if (templateTitle != null && templateDescription != null) {
            jobInput.setText(templateTitle);
            jd.setText(templateDescription);
        }

        back.setImageResource(R.drawable.back_arrow_black);
        back.setOnClickListener(v -> finish());

        //setting all onclick listeners
        cancel.setOnClickListener(v -> {
            showcancel();
        });

        create.setOnClickListener(v -> {
            String title = jobInput.getText().toString().trim();
            String desc = jd.getText().toString().trim();

            if (!title.isEmpty()) {
                Snackbar.make(create, "Creating job...", Snackbar.LENGTH_SHORT).show();
                
                String jobId = UUID.randomUUID().toString();
                JobEntity newJob = new JobEntity(
                    jobId,
                    title,
                    desc,
                    "",
                    0,
                    System.currentTimeMillis()
                );

                dataRepository.insertJob(newJob, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        runOnUiThread(() -> {
                            Toast.makeText(CreateJobActivity.this, "Job created successfully!", Toast.LENGTH_SHORT).show();
                            
                            Snackbar.make(create, "Job '" + title + "' has been created!", Snackbar.LENGTH_LONG).show();
                            
                            finish();
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Job title is required!", Toast.LENGTH_SHORT).show();
                jobInput.setError("Job title required");
            }
        });
    }

    private void showcancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Job Creation")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    Toast.makeText(this, "Job creation cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Continue Editing", (dialog, which) -> {
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