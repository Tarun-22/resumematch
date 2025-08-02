package com.example.resumematch.fragments;

import android.content.Intent;
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
import com.example.resumematch.activities.MainActivity;

public class JobListingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button create_job, delete_button;
    private ImageView back;
    private JobPostAdapter jobAdapter;
    private TextView empty_state;
    private ProgressBar progressBar;
    private DataRepository dataRepository;
    private List<JobEntity> jobEntities = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_listings, container, false);
        
        dataRepository = new DataRepository(requireContext());

        recyclerView = view.findViewById(R.id.recyclerJobPosts);
        create_job = view.findViewById(R.id.buttonCreateJob);
        delete_button = view.findViewById(R.id.buttonDeleteSelected);
        back = view.findViewById(R.id.backButton);
        empty_state = view.findViewById(R.id.textEmptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        jobAdapter = new JobPostAdapter(new ArrayList<>());
        recyclerView.setAdapter(jobAdapter);

        jobAdapter.setOnJobDeleteListener((job, position) -> {
            show_delete_confirmation_individual(job, position);
        });

        loadjobs();

        create_job.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateJobActivity.class);
            startActivity(intent);
        });

        delete_button.setOnClickListener(v -> {
            show_delete_confirmation();
        });

        back.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        return view;
    }

    private void loadjobs() {
        progressBar.setVisibility(View.VISIBLE);
        
        dataRepository.getAllJobs(new DataRepository.DatabaseCallback<List<JobEntity>>() {
            @Override
            public void onResult(List<JobEntity> jobs) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        new android.os.Handler().postDelayed(() -> {
                            jobEntities = jobs;
                            update_job_adapter();
                            update_emmpty_state();
                            progressBar.setVisibility(View.GONE);
                        }, 2000);
                    });
                }
            }
        });
    }

    private void update_job_adapter() {
        List<JobPost> jobPosts = new ArrayList<>();
        for (JobEntity jobEntity : jobEntities) {
            JobPost jobPost = new JobPost(
                jobEntity.getId(),
                jobEntity.getTitle(),
                jobEntity.getDescription(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            jobPost.setResumeCount(jobEntity.getResumeCount());
            jobPosts.add(jobPost);
        }
        jobAdapter = new JobPostAdapter(jobPosts);
        
        jobAdapter.setOnJobDeleteListener((job, position) -> {
            show_delete_confirmation_individual(job, position);
        });
        
        recyclerView.setAdapter(jobAdapter);
    }

    private void update_emmpty_state() {
        if (jobEntities.isEmpty()) {
            empty_state.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            delete_button.setVisibility(View.GONE);
        } else {
            empty_state.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            delete_button.setVisibility(View.VISIBLE);
        }
    }

    private void show_delete_confirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Jobs")
                .setMessage("Are you sure you want to delete all jobs? This action cannot be undone.")
                .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                    delete_all();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void delete_all() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            Snackbar.make(requireView(), "Deleting jobs...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteAllJobs(new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                new android.os.Handler().postDelayed(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    
                                    jobEntities.clear();
                                    update_job_adapter();
                                    update_emmpty_state();
                                    
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).refreshCounts();
                                    }
                                    
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

    private void show_delete_confirmation_individual(JobPost job, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    deleteJob(job.getId(), position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteJob(String jobId, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            Snackbar.make(requireView(), "Deleting job...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteJob(jobId, new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                progressBar.setVisibility(View.GONE);
                                
                                jobEntities.remove(position);
                                update_job_adapter();
                                update_emmpty_state();
                                
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).refreshCounts();
                                }
                                
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
        loadjobs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 