package com.example.resumematch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class CreateJobFragment extends Fragment {

    private EditText editTextJobTitle, editTextJobDescription;
    private Button createJobButton, cancelButton, clearButton, saveTemplateButton;
    private ImageView backArrowbutton;
    private ProgressBar progressBar;
    private TextView statusText;
    private DataRepository dataRepository;

    // AsyncTask for job creation
    private class CreateJobAsyncTask extends AsyncTask<String, Void, Boolean> {
        private String jobTitle;
        private String jobDescription;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.VISIBLE);
            statusText.setText("Creating job...");
            Snackbar.make(createJobButton, "Creating job...", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                jobTitle = params[0];
                jobDescription = params[1];
                
                // Simulate some processing time
                Thread.sleep(1000);
                
                // Create job entity
                JobEntity jobEntity = new JobEntity(
                    UUID.randomUUID().toString(),
                    jobTitle,
                    jobDescription,
                    "", // keywords (empty for now)
                    0,  // resumeCount
                    System.currentTimeMillis() // createdAt
                );
                
                // Save to database
                dataRepository.insertJob(jobEntity, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        // This will be called on the main thread
                    }
                });
                
                return true;
            } catch (Exception e) {
                Log.e("CreateJobAsyncTask", "Error creating job: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            statusText.setVisibility(View.GONE);
            
            if (success) {
                Toast.makeText(requireContext(), "Job created successfully!", Toast.LENGTH_SHORT).show();
                Snackbar.make(createJobButton, "Job '" + jobTitle + "' has been created!", Snackbar.LENGTH_LONG).show();
                
                // Clear form
                editTextJobTitle.setText("");
                editTextJobDescription.setText("");
                
                // Navigate back
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to create job", Toast.LENGTH_SHORT).show();
                Snackbar.make(createJobButton, "Error creating job", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_job, container, false);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(requireContext());

        // Initialize views
        editTextJobTitle = view.findViewById(R.id.editTextJobTitle);
        editTextJobDescription = view.findViewById(R.id.editTextJobDescription);
        createJobButton = view.findViewById(R.id.buttonCreateJob);
        cancelButton = view.findViewById(R.id.buttonCancel);
        clearButton = view.findViewById(R.id.buttonClear);
        saveTemplateButton = view.findViewById(R.id.buttonSaveTemplate);
        backArrowbutton = view.findViewById(R.id.backArrow);
        progressBar = view.findViewById(R.id.progressBar);
        statusText = view.findViewById(R.id.statusText);

        // Check if template data is provided
        if (getArguments() != null) {
            String templateTitle = getArguments().getString("title");
            String templateDescription = getArguments().getString("description");
            
            if (templateTitle != null && templateDescription != null) {
                editTextJobTitle.setText(templateTitle);
                editTextJobDescription.setText(templateDescription);
            }
        }

        // Set up click listeners
        backArrowbutton.setImageResource(R.drawable.back_arrow_black);
        backArrowbutton.setOnClickListener(v -> {
            // Navigate back to main content
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cancelButton.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        clearButton.setOnClickListener(v -> {
            editTextJobTitle.setText("");
            editTextJobDescription.setText("");
            Toast.makeText(requireContext(), "Form cleared", Toast.LENGTH_SHORT).show();
        });

        saveTemplateButton.setOnClickListener(v -> {
            String title = editTextJobTitle.getText().toString().trim();
            String description = editTextJobDescription.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Job title is required to save template!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save as template (simulated)
            Toast.makeText(requireContext(), "Template saved!", Toast.LENGTH_SHORT).show();
            Snackbar.make(saveTemplateButton, "Job template saved", Snackbar.LENGTH_LONG).show();
        });

        createJobButton.setOnClickListener(v -> {
            String title = editTextJobTitle.getText().toString().trim();
            String description = editTextJobDescription.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Job title is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Use AsyncTask for job creation
            new CreateJobAsyncTask().execute(title, description);
        });
        
        return view;
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Job Creation")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Job creation cancelled", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton("Continue Editing", (dialog, which) -> {
                    // Do nothing, continue editing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 