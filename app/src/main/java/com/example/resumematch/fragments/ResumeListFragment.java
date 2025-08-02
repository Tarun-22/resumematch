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
import android.os.Handler;

import com.example.resumematch.R;
import com.example.resumematch.adapters.RecentResumeAdapter;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.activities.JobSelectionActivity;
import com.example.resumematch.activities.MainActivity;

public class ResumeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView backbtn;
    private TextView title;
    private TextView empty_state;
    private ProgressBar progressBar;
    private Button scan_button, delete_all;
    private DataRepository dataRepository;
    private List<ResumeEntity> resumeEntities = new ArrayList<>();
    private RecentResumeAdapter resumeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume_list, container, false);
        
        dataRepository = new DataRepository(requireContext());

        backbtn = view.findViewById(R.id.backButton);
        title = view.findViewById(R.id.titleText);
        recyclerView = view.findViewById(R.id.recyclerResumes);
        empty_state = view.findViewById(R.id.emptyStateText);
        progressBar = view.findViewById(R.id.progressBar);
        scan_button = view.findViewById(R.id.buttonScanResume);
        delete_all = view.findViewById(R.id.buttonDeleteAll);

        title.setText("Recent Resumes");

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        resumeAdapter = new RecentResumeAdapter(new ArrayList<>());
        recyclerView.setAdapter(resumeAdapter);

        resumeAdapter.setOnResumeDeleteListener((resume, position) -> {
            show_delete_individual(resume, position);
        });

        backbtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        scan_button.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), JobSelectionActivity.class);
            startActivity(intent);
        });

        delete_all.setOnClickListener(v -> {
            show_delete_confirmation();
        });

        load_resumes();
        
        return view;
    }

    private void load_resumes() {
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
                                new Handler().postDelayed(() -> {
                                    resumeEntities = resumes != null ? resumes : new ArrayList<>();
                                    Log.d("ResumeListFragment", "Loaded " + resumeEntities.size() + " resumes from database");
                                    
                                    update_resume_adapter();
                                    update_empty();
                                    progressBar.setVisibility(View.GONE);
                                    
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

    private void update_resume_adapter() {
        try {
            resumeAdapter = new RecentResumeAdapter(resumeEntities);
            
            resumeAdapter.setOnResumeDeleteListener((resume, position) -> {
                show_delete_individual(resume, position);
            });
            
            recyclerView.setAdapter(resumeAdapter);
            Log.d("ResumeListFragment", "Updated resume adapter with " + resumeEntities.size() + " resumes");
        } catch (Exception e) {
            Log.e("ResumeListFragment", "Error updating resume adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void update_empty() {
        if (resumeEntities.isEmpty()) {
            empty_state.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            delete_all.setVisibility(View.GONE);
        } else {
            empty_state.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            delete_all.setVisibility(View.VISIBLE);
        }
    }

    private void show_delete_confirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete All Resumes")
                .setMessage("Are you sure you want to delete all resumes? This action cannot be undone.")
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
            
            Snackbar.make(requireView(), "Deleting resumes...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteAllResumes(new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                new Handler().postDelayed(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    
                                    resumeEntities.clear();
                                    update_resume_adapter();
                                    update_empty();
                                    
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).refreshCounts();
                                    }
                                    
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

    private void show_delete_individual(ResumeEntity resume, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Resume")
                .setMessage("Are you sure you want to delete this resume?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    deleteResume(resume, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteResume(ResumeEntity resume, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            Snackbar.make(requireView(), "Deleting resume...", Snackbar.LENGTH_SHORT).show();
            
            dataRepository.deleteResume(resume.getId(), new DataRepository.DatabaseCallback<Void>() {
                @Override
                public void onResult(Void result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                progressBar.setVisibility(View.GONE);
                                
                                resumeEntities.remove(position);
                                update_resume_adapter();
                                update_empty();
                                
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).refreshCounts();
                                }
                                
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
        load_resumes();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 