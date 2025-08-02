package com.example.resumematch.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import com.example.resumematch.R;
import com.example.resumematch.adapters.JobPostAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.models.JobPost;
import com.example.resumematch.activities.CreateJobActivity;
import com.example.resumematch.activities.JobApplicationsActivity;
import com.example.resumematch.activities.MainActivity;

public class JobListingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button buttonCreateJob, buttonDeleteSelected;
    private ImageView backButton;
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
        backButton = view.findViewById(R.id.backButton);
        textEmptyState = view.findViewById(R.id.textEmptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Create adapter with empty list initially
        jobAdapter = new JobPostAdapter(new ArrayList<>());
        recyclerView.setAdapter(jobAdapter);

        // Set up delete listener
        jobAdapter.setOnJobDeleteListener((job, position) -> {
            showIndividualDeleteConfirmationDialog(job, position);
        });

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

        backButton.setOnClickListener(v -> {
            // Navigate back to main content
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        return view;
    }

    private void loadJobsFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        
        dataRepository.getAllJobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
            @Override
            public void onResult(List<JobEntity> jobs) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Add delay before updating UI and hiding progress bar
                        new android.os.Handler().postDelayed(() -> {
                            jobEntities = jobs;
                            updateJobAdapter();
                            updateEmptyState();
                            progressBar.setVisibility(View.GONE);
                        }, 2000);
                    });
                }
            }
        });
    }

    private void updateJobAdapter() {
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
        jobAdapter = new JobPostAdapter(jobPosts);
        
        // Set up delete listener
        jobAdapter.setOnJobDeleteListener((job, position) -> {
            showIndividualDeleteConfirmationDialog(job, position);
        });
        
        recyclerView.setAdapter(jobAdapter);
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
                                // Add delay before updating UI and hiding progress bar
                                new android.os.Handler().postDelayed(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    
                                    // Clear local list
                                    jobEntities.clear();
                                    updateJobAdapter();
                                    updateEmptyState();
                                    
                                    // Refresh MainActivity counts
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).refreshCounts();
                                    }
                                    
                                    // Show success messages
                                    Toast.makeText(requireContext(), "All jobs deleted successfully!", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(requireView(), "All jobs have been deleted", Snackbar.LENGTH_LONG).show();
                                    
                                    Log.d("JobListingsFragment", "All jobs deleted successfully");
                                }, 2000);
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

    private void showIndividualDeleteConfirmationDialog(JobPost job, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    deleteJob(job.getId(), position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteJob(String jobId, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            // Show progress message
            Snackbar.make(requireView(), "Deleting job...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteJob(jobId, new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                progressBar.setVisibility(View.GONE);
                                
                                // Remove from local list
                                jobEntities.remove(position);
                                updateJobAdapter();
                                updateEmptyState();
                                
                                // Refresh MainActivity counts
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).refreshCounts();
                                }
                                
                                // Show success message
                                Toast.makeText(requireContext(), "Job deleted successfully!", Toast.LENGTH_SHORT).show();
                                Snackbar.make(requireView(), "Job has been deleted", Snackbar.LENGTH_LONG).show();
                                
                                Log.d("JobListingsFragment", "Job with ID " + jobId + " deleted successfully");
                            } catch (Exception e) {
                                Log.e("JobListingsFragment", "Error updating UI after individual delete: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("JobListingsFragment", "Error deleting job: " + e.getMessage());
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