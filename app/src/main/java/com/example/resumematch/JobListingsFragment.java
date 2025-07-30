package com.example.resumematch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class JobListingsFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private Button btnAddJob, btnEditJob, btnDeleteJob, btnRefresh, btnSearch;
    private EditText etSearch;
    private ArrayAdapter<String> adapter;
    private List<String> jobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_listings, container, false);
        
        // Initialize views
        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);
        btnAddJob = view.findViewById(R.id.btnAddJob);
        btnEditJob = view.findViewById(R.id.btnEditJob);
        btnDeleteJob = view.findViewById(R.id.btnDeleteJob);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnSearch = view.findViewById(R.id.btnSearch);
        etSearch = view.findViewById(R.id.etSearch);
        
        jobList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, jobList);
        listView.setAdapter(adapter);
        
        // Set click listeners
        btnAddJob.setOnClickListener(v -> {
            showSnackbar("Opening job creation...");
            navigateToFragment(new CreateJobFragment());
        });
        
        btnEditJob.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedJob = jobList.get(listView.getCheckedItemPosition());
                showSnackbar("Editing: " + selectedJob);
                showCustomDialog("Edit Job", 
                    "Edit details for: " + selectedJob + "\n\n" +
                    "You can modify:\n" +
                    "• Job title and description\n" +
                    "• Required skills\n" +
                    "• Working hours\n" +
                    "• Availability days\n" +
                    "• Salary and location");
            } else {
                Toast.makeText(getContext(), "Please select a job to edit", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnDeleteJob.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedJob = jobList.get(listView.getCheckedItemPosition());
                showDeleteConfirmationDialog(selectedJob);
            } else {
                Toast.makeText(getContext(), "Please select a job to delete", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRefresh.setOnClickListener(v -> {
            showSnackbar("Refreshing job listings...");
            new LoadJobsTask().execute("refresh");
        });
        
        btnSearch.setOnClickListener(v -> {
            String searchTerm = etSearch.getText().toString();
            if (!searchTerm.isEmpty()) {
                showSnackbar("Searching for: " + searchTerm);
                new LoadJobsTask().execute("search", searchTerm);
            } else {
                Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });
        
        // ListView item click
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedJob = jobList.get(position);
            showCustomDialog("Job Details", 
                "Selected job: " + selectedJob + "\n\n" +
                "This job listing is active and accepting applications.\n" +
                "You can edit details, view applications, or manage the listing.\n\n" +
                "Actions available:\n" +
                "• Edit job details\n" +
                "• View applications\n" +
                "• Deactivate listing\n" +
                "• Delete listing");
        });
        
        // Load initial data
        new LoadJobsTask().execute("load");
        
        return view;
    }
    
    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void showDeleteConfirmationDialog(String jobTitle) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete Job Listing")
               .setMessage("Are you sure you want to delete: " + jobTitle + "?\n\nThis action cannot be undone.")
               .setPositiveButton("Delete", (dialog, which) -> {
                   jobList.remove(listView.getCheckedItemPosition());
                   adapter.notifyDataSetChanged();
                   showSnackbar("Deleted: " + jobTitle);
                   Toast.makeText(getContext(), "Job listing deleted successfully", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
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
    
    // AsyncTask to simulate data loading
    private class LoadJobsTask extends AsyncTask<String, Integer, List<String>> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        
        @Override
        protected List<String> doInBackground(String... params) {
            String action = params[0];
            List<String> result = new ArrayList<>();
            
            // Simulate loading process
            for (int i = 0; i <= 100; i += 25) {
                publishProgress(i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            switch (action) {
                case "load":
                case "refresh":
                    result.add("Software Engineer - Mon-Fri 9AM-5PM - Java, Python");
                    result.add("Sales Associate - Mon-Sat 10AM-6PM - Communication");
                    result.add("Data Analyst - Mon-Fri 8AM-4PM - SQL, Excel");
                    result.add("Marketing Manager - Mon-Fri 9AM-6PM - Digital Marketing");
                    result.add("Customer Service - Mon-Sun 8AM-8PM - Communication");
                    break;
                case "add":
                    result.add("New Job Listing - Flexible Hours - Various Skills");
                    break;
                case "search":
                    String searchTerm = params[1];
                    if (searchTerm.toLowerCase().contains("engineer")) {
                        result.add("Software Engineer - Mon-Fri 9AM-5PM - Java, Python");
                    } else if (searchTerm.toLowerCase().contains("sales")) {
                        result.add("Sales Associate - Mon-Sat 10AM-6PM - Communication");
                    } else if (searchTerm.toLowerCase().contains("marketing")) {
                        result.add("Marketing Manager - Mon-Fri 9AM-6PM - Digital Marketing");
                    }
                    break;
            }
            
            return result;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(List<String> result) {
            progressBar.setVisibility(View.GONE);
            
            if (!result.isEmpty()) {
                jobList.clear();
                jobList.addAll(result);
                adapter.notifyDataSetChanged();
                showSnackbar("Loaded " + result.size() + " job listings");
            }
        }
    }
} 