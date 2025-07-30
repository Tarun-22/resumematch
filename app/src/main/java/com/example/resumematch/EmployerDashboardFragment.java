package com.example.resumematch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;

public class EmployerDashboardFragment extends Fragment {

    private ProgressBar progressBar;
    private Button btnCreateJob, btnScanResume, btnViewApplications;
    private EditText etSearchJobs;
    private TextView tvTotalJobs, tvActiveJobs, tvTotalApplications, tvPendingReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_dashboard, container, false);
        
        // Initialize views
        progressBar = view.findViewById(R.id.progressBar);
        btnCreateJob = view.findViewById(R.id.btnCreateJob);
        btnScanResume = view.findViewById(R.id.btnScanResume);
        btnViewApplications = view.findViewById(R.id.btnViewApplications);
        etSearchJobs = view.findViewById(R.id.etSearchJobs);
        tvTotalJobs = view.findViewById(R.id.tvTotalJobs);
        tvActiveJobs = view.findViewById(R.id.tvActiveJobs);
        tvTotalApplications = view.findViewById(R.id.tvTotalApplications);
        tvPendingReviews = view.findViewById(R.id.tvPendingReviews);
        
        // Set click listeners
        btnCreateJob.setOnClickListener(v -> {
            showSnackbar("Opening job creation...");
            navigateToFragment(new CreateJobFragment());
        });
        
        btnScanResume.setOnClickListener(v -> {
            showSnackbar("Opening resume scanner...");
            navigateToFragment(new ScanResumeFragment());
        });
        
        btnViewApplications.setOnClickListener(v -> {
            showSnackbar("Loading applications...");
            navigateToFragment(new ApplicationsFragment());
        });
        
        // Search functionality
        etSearchJobs.setOnClickListener(v -> {
            showSnackbar("Opening job listings...");
            navigateToFragment(new JobListingsFragment());
        });
        
        // Load dashboard data
        new LoadDashboardTask().execute("load_dashboard");
        
        return view;
    }
    
    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    private void showCustomDialog(String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("OK", null)
               .show();
    }
    
    // AsyncTask to simulate dashboard loading
    private class LoadDashboardTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        
        @Override
        protected String doInBackground(String... params) {
            String action = params[0];
            
            // Simulate loading process
            for (int i = 0; i <= 100; i += 20) {
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return action;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            
            switch (result) {
                case "load_dashboard":
                    tvTotalJobs.setText("12");
                    tvActiveJobs.setText("8");
                    tvTotalApplications.setText("45");
                    tvPendingReviews.setText("6");
                    showSnackbar("Dashboard loaded successfully");
                    break;
            }
        }
    }
} 