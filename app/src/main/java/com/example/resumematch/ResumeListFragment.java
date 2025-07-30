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

public class ResumeListFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private Button btnAddResume, btnDeleteResume, btnRefresh, btnFilter;
    private EditText etFilter;
    private ArrayAdapter<String> adapter;
    private List<String> resumeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume_list, container, false);
        
        // Initialize views
        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);
        btnAddResume = view.findViewById(R.id.btnAddResume);
        btnDeleteResume = view.findViewById(R.id.btnDeleteResume);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnFilter = view.findViewById(R.id.btnFilter);
        etFilter = view.findViewById(R.id.etFilter);
        
        resumeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, resumeList);
        listView.setAdapter(adapter);
        
        // Set click listeners
        btnAddResume.setOnClickListener(v -> {
            showSnackbar("Adding new resume...");
            new LoadResumesTask().execute("add");
        });
        
        btnDeleteResume.setOnClickListener(v -> {
            if (listView.getCheckedItemPosition() >= 0) {
                String selectedResume = resumeList.get(listView.getCheckedItemPosition());
                resumeList.remove(listView.getCheckedItemPosition());
                adapter.notifyDataSetChanged();
                showSnackbar("Deleted: " + selectedResume);
                Toast.makeText(getContext(), "Resume deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a resume to delete", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRefresh.setOnClickListener(v -> {
            showSnackbar("Refreshing resume list...");
            new LoadResumesTask().execute("refresh");
        });
        
        btnFilter.setOnClickListener(v -> {
            String filterTerm = etFilter.getText().toString();
            if (!filterTerm.isEmpty()) {
                showSnackbar("Filtering by: " + filterTerm);
                new LoadResumesTask().execute("filter", filterTerm);
            } else {
                Toast.makeText(getContext(), "Please enter a filter term", Toast.LENGTH_SHORT).show();
            }
        });
        
        // ListView item click
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedResume = resumeList.get(position);
            showCustomDialog("Resume Details", "Selected resume: " + selectedResume + "\n\nThis resume has been processed and scored.");
        });
        
        // Load initial data
        new LoadResumesTask().execute("load");
        
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
    private class LoadResumesTask extends AsyncTask<String, Integer, List<String>> {
        
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
                    result.add("Jane Smith - Product Manager - 92% Match");
                    result.add("Mike Johnson - Data Scientist - 78% Match");
                    result.add("Sarah Wilson - UI Designer - 88% Match");
                    result.add("David Brown - DevOps Engineer - 76% Match");
                    break;
                case "add":
                    result.add("New Resume - 82% Match");
                    break;
                case "filter":
                    String filterTerm = params[1];
                    if (filterTerm.toLowerCase().contains("engineer")) {
                        result.add("John Doe - Software Engineer - 85% Match");
                        result.add("David Brown - DevOps Engineer - 76% Match");
                    } else if (filterTerm.toLowerCase().contains("manager")) {
                        result.add("Jane Smith - Product Manager - 92% Match");
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
                resumeList.clear();
                resumeList.addAll(result);
                adapter.notifyDataSetChanged();
                showSnackbar("Loaded " + result.size() + " resumes");
            }
        }
    }
} 