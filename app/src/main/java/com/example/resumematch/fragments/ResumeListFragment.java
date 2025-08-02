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
import java.util.Collections;
import java.util.List;
import android.os.Handler;

import com.example.resumematch.R;
import com.example.resumematch.adapters.RecentResumeAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.activities.JobSelectionActivity;
import com.example.resumematch.activities.MatchScoreActivity;
import com.example.resumematch.activities.MainActivity;

public class ResumeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private TextView titleText;
    private TextView emptyStateText;
    private ProgressBar progressBar;
    private Button buttonScanResume, buttonDeleteAll;
    private DataRepository dataRepository;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume_list, container, false);
        
        // Initialize DataRepository
        dataRepository = new DataRepository(requireContext());

        // Initialize views
        backButton = view.findViewById(R.id.backButton);
        titleText = view.findViewById(R.id.titleText);
        recyclerView = view.findViewById(R.id.recyclerResumes);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        progressBar = view.findViewById(R.id.progressBar);
        buttonScanResume = view.findViewById(R.id.buttonScanResume);
        buttonDeleteAll = view.findViewById(R.id.buttonDeleteAll);

        // Set title
        titleText.setText("Recent Resumes");

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        resumeAdapter = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapter);

        // Set up delete listener
        resumeAdapter.setOnResumeDeleteListener((resume, position) -> {
            showIndividualDeleteConfirmationDialog(resume, position);
        });

        // Set up click listeners
        backButton.setOnClickListener(v -> {
            // Navigate back to main content
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        buttonScanResume.setOnClickListener(v -> {
            // Navigate to job selection for scanning
            Intent intent = new Intent(requireContext(), JobSelectionActivity.class);
            startActivity(intent);
        });

        buttonDeleteAll.setOnClickListener(v -> {
            // Show delete confirmation dialog
            showDeleteConfirmationDialog();
        });

        // Load resumes from database
        loadResumesFromDatabase();
        
        return view;
    }

    private void loadResumesFromDatabase() {
        try {
            Log.d("ResumeListFragment", "Starting to load resumes from database");
            progressBar.setVisibility(View.VISIBLE);
            
            dataRepository.getAllResumes(new DataRepository.DatabaseCallback<List<ResumeEntity>>() {
                @Override
                public void onResult(List<ResumeEntity> resumes) {
                    Log.d("ResumeListFragment", "Database callback received, resumes: " + (resumes != null ? resumes.size() : "null"));
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                // Add delay before updating UI and hiding progress bar
                                new Handler().postDelayed(() -> {
                                    resumeEntities = resumes != null ? resumes : new ArrayList<>();
                                    Log.d("ResumeListFragment", "Loaded " + resumeEntities.size() + " resumes from database");
                                    
                                    updateResumeAdapter();
                                    updateEmptyState();
                                    progressBar.setVisibility(View.GONE);
                                    
                                    // Show success message
                                    if (resumeEntities.size() > 0) {
                                        Snackbar.make(requireView(), "Loaded " + resumeEntities.size() + " resumes", Snackbar.LENGTH_SHORT).show();
                                    }
                                }, 2000);
                            } catch (Exception e) {
                                Log.e("ResumeListFragment", "Error updating UI: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ResumeListFragment", "Error loading resumes: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateResumeAdapter() {
        try {
            resumeAdapter = new RecentResumeAdapter(resumeEntities);
            
            // Set up delete listener
            resumeAdapter.setOnResumeDeleteListener((resume, position) -> {
                showIndividualDeleteConfirmationDialog(resume, position);
            });
            
            recyclerView.setAdapter(resumeAdapter);
            Log.d("ResumeListFragment", "Updated resume adapter with " + resumeEntities.size() + " resumes");
        } catch (Exception e) {
            Log.e("ResumeListFragment", "Error updating resume adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        if (resumeEntities.isEmpty()) {
            emptyStateText.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            buttonDeleteAll.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            buttonDeleteAll.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete All Resumes")
                .setMessage("Are you sure you want to delete all resumes? This action cannot be undone.")
                .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                    deleteAllResumes();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllResumes() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            // Show progress message
            Snackbar.make(requireView(), "Deleting resumes...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteAllResumes(new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                // Add delay before updating UI and hiding progress bar
                                new Handler().postDelayed(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    
                                    // Clear local list
                                    resumeEntities.clear();
                                    updateResumeAdapter();
                                    updateEmptyState();
                                    
                                    // Refresh MainActivity counts
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).refreshCounts();
                                    }
                                    
                                    // Show success messages
                                    Toast.makeText(requireContext(), "All resumes deleted successfully!", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(requireView(), "All resumes have been deleted", Snackbar.LENGTH_LONG).show();
                                    
                                    Log.d("ResumeListFragment", "All resumes deleted successfully");
                                }, 2000);
                            } catch (Exception e) {
                                Log.e("ResumeListFragment", "Error updating UI after delete: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ResumeListFragment", "Error deleting resumes: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showIndividualDeleteConfirmationDialog(ResumeEntity resume, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Resume")
                .setMessage("Are you sure you want to delete this resume?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    deleteResume(resume, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteResume(ResumeEntity resume, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            // Show progress message
            Snackbar.make(requireView(), "Deleting resume...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteResume(resume.getId(), new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                progressBar.setVisibility(View.GONE);
                                
                                // Remove from local list
                                resumeEntities.remove(position);
                                updateResumeAdapter();
                                updateEmptyState();
                                
                                // Refresh MainActivity counts
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).refreshCounts();
                                }
                                
                                // Show success message
                                Toast.makeText(requireContext(), "Resume deleted successfully!", Toast.LENGTH_SHORT).show();
                                Snackbar.make(requireView(), "Resume deleted", Snackbar.LENGTH_LONG).show();
                                
                                Log.d("ResumeListFragment", "Resume deleted successfully");
                            } catch (Exception e) {
                                Log.e("ResumeListFragment", "Error updating UI after delete: " + e.getMessage());
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ResumeListFragment", "Error deleting resume: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload resumes when returning to this fragment
        loadResumesFromDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 