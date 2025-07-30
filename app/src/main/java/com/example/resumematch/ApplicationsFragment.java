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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private Button btnViewDetails, btnAccept, btnReject, btnRefresh, btnFilter;
    private EditText etFilter;
    private ArrayAdapter<String> adapter;
    private List<String> applicationsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);
        
        // Initialize views
        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);
        btnViewDetails = view.findViewById(R.id.btnViewDetails);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnReject = view.findViewById(R.id.btnReject);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnFilter = view.findViewById(R.id.btnFilter);
        etFilter = view.findViewById(R.id.etFilter);
        
        applicationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, applicationsList);
        listView.setAdapter(adapter);
        
        // Set click listeners
        btnViewDetails.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedApp = applicationsList.get(listView.getCheckedItemPosition());
                showSnackbar("Viewing details for: " + selectedApp);
                showApplicationDetailsDialog(selectedApp);
            } else {
                Toast.makeText(getContext(), "Please select an application to view", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAccept.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedApp = applicationsList.get(listView.getCheckedItemPosition());
                showAcceptConfirmationDialog(selectedApp);
            } else {
                Toast.makeText(getContext(), "Please select an application to accept", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnReject.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedApp = applicationsList.get(listView.getCheckedItemPosition());
                showRejectConfirmationDialog(selectedApp);
            } else {
                Toast.makeText(getContext(), "Please select an application to reject", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRefresh.setOnClickListener(v -> {
            showSnackbar("Refreshing applications...");
            new LoadApplicationsTask().execute("refresh");
        });
        
        btnFilter.setOnClickListener(v -> {
            String filterTerm = etFilter.getText().toString();
            if (!filterTerm.isEmpty()) {
                showSnackbar("Filtering by: " + filterTerm);
                new LoadApplicationsTask().execute("filter", filterTerm);
            } else {
                Toast.makeText(getContext(), "Please enter a filter term", Toast.LENGTH_SHORT).show();
            }
        });
        
        // ListView item click
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedApp = applicationsList.get(position);
            showCustomDialog("Quick View", 
                "Application: " + selectedApp + "\n\n" +
                "This application has been processed and scored.\n" +
                "Click 'View Details' for complete information.\n\n" +
                "Available actions:\n" +
                "• View full details\n" +
                "• Accept application\n" +
                "• Reject application\n" +
                "• Schedule interview");
        });
        
        // Load initial data
        new LoadApplicationsTask().execute("load");
        
        return view;
    }
    
    private void showApplicationDetailsDialog(String applicantName) {
        String details = "Applicant: " + applicantName + "\n\n" +
                        "Match Score: 85%\n" +
                        "Skills: Java, Python, React, AWS, Docker\n" +
                        "Experience: 5 years\n" +
                        "Education: Bachelor's in Computer Science\n" +
                        "Current Position: Senior Software Engineer\n" +
                        "Location: New York, NY\n" +
                        "Expected Salary: $120k - $140k\n" +
                        "Availability: Immediate\n" +
                        "Status: Pending Review\n\n" +
                        "Recommendation: Strong candidate for interview\n" +
                        "Missing Skills: Kubernetes, Microservices\n" +
                        "Strengths: Team leadership, Project management";
        
        showCustomDialog("Application Details", details);
    }
    
    private void showAcceptConfirmationDialog(String applicantName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Accept Application")
               .setMessage("Are you sure you want to accept " + applicantName + "?\n\n" +
                          "This will:\n" +
                          "• Move the application to 'Accepted' status\n" +
                          "• Send an acceptance notification\n" +
                          "• Schedule next steps")
               .setPositiveButton("Accept", (dialog, which) -> {
                   showSnackbar("Accepted: " + applicantName);
                   Toast.makeText(getContext(), "Application accepted successfully", Toast.LENGTH_SHORT).show();
                   // Remove from list or mark as accepted
                   applicationsList.remove(listView.getCheckedItemPosition());
                   adapter.notifyDataSetChanged();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void showRejectConfirmationDialog(String applicantName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Reject Application")
               .setMessage("Are you sure you want to reject " + applicantName + "?\n\n" +
                          "This will:\n" +
                          "• Move the application to 'Rejected' status\n" +
                          "• Send a rejection notification\n" +
                          "• Remove from active applications")
               .setPositiveButton("Reject", (dialog, which) -> {
                   showSnackbar("Rejected: " + applicantName);
                   Toast.makeText(getContext(), "Application rejected", Toast.LENGTH_SHORT).show();
                   // Remove from list or mark as rejected
                   applicationsList.remove(listView.getCheckedItemPosition());
                   adapter.notifyDataSetChanged();
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
    private class LoadApplicationsTask extends AsyncTask<String, Integer, List<String>> {
        
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
                    result.add("John Doe - Software Engineer - 85% Match");
                    result.add("Jane Smith - Sales Associate - 92% Match");
                    result.add("Mike Johnson - Data Analyst - 78% Match");
                    result.add("Sarah Wilson - Marketing Manager - 88% Match");
                    result.add("David Brown - Customer Service - 76% Match");
                    break;
                case "filter":
                    String filterTerm = params[1];
                    if (filterTerm.toLowerCase().contains("engineer")) {
                        result.add("John Doe - Software Engineer - 85% Match");
                    } else if (filterTerm.toLowerCase().contains("sales")) {
                        result.add("Jane Smith - Sales Associate - 92% Match");
                    } else if (filterTerm.toLowerCase().contains("marketing")) {
                        result.add("Sarah Wilson - Marketing Manager - 88% Match");
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
                applicationsList.clear();
                applicationsList.addAll(result);
                adapter.notifyDataSetChanged();
                showSnackbar("Loaded " + result.size() + " applications");
            }
        }
    }
} 