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

public class JobListFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private Button btnAddJob, btnDeleteJob, btnRefresh, btnSearch;
    private EditText etSearch;
    private ArrayAdapter<String> adapter;
    private List<String> jobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        
        // Initialize views
        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);
        btnAddJob = view.findViewById(R.id.btnAddJob);
        btnDeleteJob = view.findViewById(R.id.btnDeleteJob);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnSearch = view.findViewById(R.id.btnSearch);
        etSearch = view.findViewById(R.id.etSearch);
        
        jobList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, jobList);
        listView.setAdapter(adapter);
        
        // Set click listeners
        btnAddJob.setOnClickListener(v -> {
            showSnackbar("Adding new job...");
            new LoadJobsTask().execute("add");
        });
        
        btnDeleteJob.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedJob = jobList.get(listView.getCheckedItemPosition());
                jobList.remove(listView.getCheckedItemPosition());
                adapter.notifyDataSetChanged();
                showSnackbar("Deleted: " + selectedJob);
                Toast.makeText(getContext(), "Job deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a job to delete", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRefresh.setOnClickListener(v -> {
            showSnackbar("Refreshing job list...");
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
            showCustomDialog("Job Details", "Selected job: " + selectedJob + "\n\nThis job has been posted and is accepting applications.");
        });
        
        // Load initial data
        new LoadJobsTask().execute("load");
        
        return view;
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
                    result.add("Senior Software Engineer - $120k");
                    result.add("Product Manager - $100k");
                    result.add("Data Scientist - $110k");
                    result.add("UI/UX Designer - $90k");
                    result.add("DevOps Engineer - $115k");
                    break;
                case "add":
                    result.add("New Job Position - $95k");
                    break;
                case "search":
                    String searchTerm = params[1];
                    if (searchTerm.toLowerCase().contains("engineer")) {
                        result.add("Senior Software Engineer - $120k");
                        result.add("DevOps Engineer - $115k");
                    } else if (searchTerm.toLowerCase().contains("manager")) {
                        result.add("Product Manager - $100k");
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
                showSnackbar("Loaded " + result.size() + " jobs");
            }
        }
    }
} 