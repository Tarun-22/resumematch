package com.example.resumematch;

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

    private EditText jobTitleInput, jobDescriptionInput;
    private Button createJobButton, cancelButton, clearButton, saveTemplateButton;
    private ImageView backArrowbutton;
    private ProgressBar progressBar;
    private TextView statusText;
    private DataRepository dataRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_job, container, false);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(requireContext());

        // Connect variables to UI elements
        jobTitleInput = view.findViewById(R.id.editTextJobTitle);
        jobDescriptionInput = view.findViewById(R.id.editTextJobDescription);
        createJobButton = view.findViewById(R.id.buttonCreateJob);
        cancelButton = view.findViewById(R.id.buttonCancel);
        clearButton = view.findViewById(R.id.buttonClear);
        saveTemplateButton = view.findViewById(R.id.buttonSaveTemplate);
        backArrowbutton = view.findViewById(R.id.backArrow);
        progressBar = view.findViewById(R.id.progressBar);
        statusText = view.findViewById(R.id.statusText);

        // Check if template data is provided
        Bundle args = getArguments();
        if (args != null) {
            String templateTitle = args.getString("title");
            String templateDescription = args.getString("description");
            
            if (templateTitle != null && templateDescription != null) {
                // Pre-fill the form with template data
                jobTitleInput.setText(templateTitle);
                jobDescriptionInput.setText(templateDescription);
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
            // Show confirmation dialog
            showCancelConfirmationDialog();
        });

        clearButton.setOnClickListener(v -> {
            // Clear all fields
            jobTitleInput.setText("");
            jobDescriptionInput.setText("");
            Toast.makeText(requireContext(), "Form cleared", Toast.LENGTH_SHORT).show();
        });

        saveTemplateButton.setOnClickListener(v -> {
            // Save as template (simulated)
            Toast.makeText(requireContext(), "Template saved!", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Job template saved for future use", Snackbar.LENGTH_LONG).show();
        });

        createJobButton.setOnClickListener(v -> {
            String title = jobTitleInput.getText().toString().trim();
            String desc = jobDescriptionInput.getText().toString().trim();

            if (!title.isEmpty()) {
                // Show progress
                progressBar.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("Creating job...");
                
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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                try {
                                    // Hide progress
                                    progressBar.setVisibility(View.GONE);
                                    statusText.setVisibility(View.GONE);
                                    
                                    // Show success Toast
                                    Toast.makeText(requireContext(), "Job created successfully!", Toast.LENGTH_SHORT).show();
                                    
                                    // Show success Snackbar
                                    Snackbar.make(createJobButton, "Job '" + title + "' has been created!", Snackbar.LENGTH_LONG).show();
                                    
                                    // Clear form
                                    jobTitleInput.setText("");
                                    jobDescriptionInput.setText("");
                                    
                                    Log.d("CreateJobFragment", "Job created successfully: " + title);
                                } catch (Exception e) {
                                    Log.e("CreateJobFragment", "Error updating UI: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });
            } else {
                // Show error Toast
                Toast.makeText(requireContext(), "Job title is required!", Toast.LENGTH_SHORT).show();
                jobTitleInput.setError("Job title required");
            }
        });
        
        return view;
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Job Creation")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    // Navigate back to main content
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton("Continue Editing", (dialog, which) -> {
                    // Do nothing, stay on the form
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