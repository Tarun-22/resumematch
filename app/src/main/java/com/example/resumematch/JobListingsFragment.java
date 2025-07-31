package com.example.resumematch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class JobListingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button buttonCreateJob, buttonDeleteSelected;
    private JobPostAdapter jobAdapter;
    private TextView textEmptyState;
    private ProgressBar progressBar;
    private DataRepository dataRepository;
    private List<JobEntity> jobEntities = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_listings, container, false);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(requireContext());

        recyclerView = view.findViewById(R.id.recyclerJobPosts);
        buttonCreateJob = view.findViewById(R.id.buttonCreateJob);
        buttonDeleteSelected = view.findViewById(R.id.buttonDeleteSelected);
        textEmptyState = view.findViewById(R.id.textEmptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Create adapter with empty list initially
        jobAdapter = new JobPostAdapter(new ArrayList<>());
        recyclerView.setAdapter(jobAdapter);

        // Load jobs from database
        loadJobsFromDatabase();

        buttonCreateJob.setOnClickListener(v -> {
            // Navigate to job creation
            Intent intent = new Intent(requireContext(), CreateJobActivity.class);
            startActivity(intent);
        });

        buttonDeleteSelected.setOnClickListener(v -> {
            // Show delete confirmation dialog
            showDeleteConfirmationDialog();
        });
        
        return view;
    }

    private void loadJobsFromDatabase() {
        try {
            Log.d("JobListingsFragment", "Starting to load jobs from database");
            progressBar.setVisibility(View.VISIBLE);
            
            dataRepository.getAllJobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
                @Override
                public void onResult(List<JobEntity> jobs) {
                    Log.d("JobListingsFragment", "Database callback received, jobs: " + (jobs != null ? jobs.size() : "null"));
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                jobEntities = jobs != null ? jobs : new ArrayList<>();
                                Log.d("JobListingsFragment", "Loaded " + jobEntities.size() + " jobs from database");
                                
                                updateJobAdapter();
                                updateEmptyState();
                                progressBar.setVisibility(View.GONE);
                                
                                // Show success message
                                if (jobEntities.size() > 0) {
                                    Snackbar.make(requireView(), "Loaded " + jobEntities.size() + " jobs", Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("JobListingsFragment", "Error updating UI: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("JobListingsFragment", "Error loading jobs: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateJobAdapter() {
        try {
            // Convert JobEntity to JobPost for the adapter
            List<JobPost> jobPosts = new ArrayList<>();
            for (JobEntity jobEntity : jobEntities) {
                JobPost jobPost = new JobPost(
                    jobEntity.getId(),
                    jobEntity.getTitle(),
                    jobEntity.getDescription(),
                    new ArrayList<>(), // keywords (empty for now)
                    new ArrayList<>()  // resumes (empty for now)
                );
                jobPost.setResumeCount(jobEntity.getResumeCount());
                jobPosts.add(jobPost);
            }
            
            Log.d("JobListingsFragment", "Created " + jobPosts.size() + " JobPost objects");
            jobAdapter = new JobPostAdapter(jobPosts);
            recyclerView.setAdapter(jobAdapter);
        } catch (Exception e) {
            Log.e("JobListingsFragment", "Error updating job adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        if (jobEntities.isEmpty()) {
            textEmptyState.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            buttonDeleteSelected.setVisibility(View.GONE);
        } else {
            textEmptyState.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            buttonDeleteSelected.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Jobs")
                .setMessage("Are you sure you want to delete all jobs? This action cannot be undone.")
                .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                    deleteAllJobs();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllJobs() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            // Show progress message
            Snackbar.make(requireView(), "Deleting jobs...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteAllJobs(new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                progressBar.setVisibility(View.GONE);
                                
                                // Clear local list
                                jobEntities.clear();
                                updateJobAdapter();
                                updateEmptyState();
                                
                                // Show success messages
                                Toast.makeText(requireContext(), "All jobs deleted successfully!", Toast.LENGTH_SHORT).show();
                                Snackbar.make(requireView(), "All jobs have been deleted", Snackbar.LENGTH_LONG).show();
                                
                                Log.d("JobListingsFragment", "All jobs deleted successfully");
                            } catch (Exception e) {
                                Log.e("JobListingsFragment", "Error updating UI after delete: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("JobListingsFragment", "Error deleting jobs: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload jobs when returning to this fragment
        loadJobsFromDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 