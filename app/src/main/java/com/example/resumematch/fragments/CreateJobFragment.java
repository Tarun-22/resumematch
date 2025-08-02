package com.example.resumematch.fragments;

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

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.activities.MainActivity;

import java.util.UUID;

public class CreateJobFragment extends Fragment {

    private EditText editjob, editJD;
    private Button create, cancel, clear, savetemplate;
    private ImageView back;
    private ProgressBar progress;
    private TextView status;
    private DataRepository dataRepository;

    private class CreateJobAsyncTask extends AsyncTask<String, Void, Boolean> {
        private String jobtitle;
        private String JD;

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);
            status.setText("Creating job...");
            Snackbar.make(create, "Creating job...", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                jobtitle = params[0];
                JD = params[1];
                
                Thread.sleep(3000);
                
                JobEntity jobEntity = new JobEntity(
                    UUID.randomUUID().toString(),
                        jobtitle,
                        JD,
                    "",
                    0,
                    System.currentTimeMillis()
                );
                
                dataRepository.insertJob(jobEntity, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {

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
            new android.os.Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);
                status.setVisibility(View.GONE);
                
                if (success) {
                    Toast.makeText(requireContext(), "Job created successfully!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(create, "Job '" + jobtitle + "' has been created!", Snackbar.LENGTH_LONG).show();
                    
                    editjob.setText("");
                    editJD.setText("");
                    
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).refreshCounts();
                    }
                    
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to create job", Toast.LENGTH_SHORT).show();
                    Snackbar.make(create, "Error creating job", Snackbar.LENGTH_LONG).show();
                }
            }, 3000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_job, container, false);
        
        dataRepository = new DataRepository(requireContext());

        editjob = view.findViewById(R.id.editTextJobTitle);
        editJD = view.findViewById(R.id.editTextJobDescription);
        create = view.findViewById(R.id.buttonCreateJob);
        cancel = view.findViewById(R.id.buttonCancel);
        clear = view.findViewById(R.id.buttonClear);
        savetemplate = view.findViewById(R.id.buttonSaveTemplate);
        back = view.findViewById(R.id.backArrow);
        progress = view.findViewById(R.id.progressBar);
        status = view.findViewById(R.id.statusText);

        if (getArguments() != null) {
            String templateTitle = getArguments().getString("title");
            String templateDescription = getArguments().getString("description");
            
            if (templateTitle != null && templateDescription != null) {
                editjob.setText(templateTitle);
                editJD.setText(templateDescription);
            }
        }

        back.setImageResource(R.drawable.back_arrow_black);
        back.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cancel.setOnClickListener(v -> {
            show_cancel_dialog();
        });

        clear.setOnClickListener(v -> {
            editjob.setText("");
            editJD.setText("");
            Toast.makeText(requireContext(), "Form cleared", Toast.LENGTH_SHORT).show();
        });

        savetemplate.setOnClickListener(v -> {
            String title = editjob.getText().toString().trim();
            String description = editJD.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Job title is required to save template!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Toast.makeText(requireContext(), "Template saved!", Toast.LENGTH_SHORT).show();
            Snackbar.make(savetemplate, "Job template saved", Snackbar.LENGTH_LONG).show();
        });

        create.setOnClickListener(v -> {
            String title = editjob.getText().toString().trim();
            String description = editJD.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Job title is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new CreateJobAsyncTask().execute(title, description);
        });
        
        return view;
    }

    private void show_cancel_dialog() {
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