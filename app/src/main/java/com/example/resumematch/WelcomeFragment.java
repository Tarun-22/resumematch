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
import com.google.android.material.snackbar.Snackbar;

public class WelcomeFragment extends Fragment {

    private ProgressBar progressBar;
    private Button btnEnterEmployer, btnEnterJobSeeker, btnSettings;
    private EditText etName;
    private TextView tvWelcome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        
        // Initialize views
        progressBar = view.findViewById(R.id.progressBar);
        btnEnterEmployer = view.findViewById(R.id.btnEnterEmployer);
        btnEnterJobSeeker = view.findViewById(R.id.btnEnterJobSeeker);
        btnSettings = view.findViewById(R.id.btnSettings);
        etName = view.findViewById(R.id.etName);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        
        // Show welcome message
        showWelcomeMessage();
        
        // Set click listeners
        btnEnterEmployer.setOnClickListener(v -> {
            showSnackbar("Entering as Employer");
            new LoadDataTask().execute("employer");
        });
        
        btnEnterJobSeeker.setOnClickListener(v -> {
            showSnackbar("Entering as Job Seeker");
            new LoadDataTask().execute("jobseeker");
        });
        
        btnSettings.setOnClickListener(v -> {
            showCustomDialog();
        });
        
        return view;
    }
    
    private void showWelcomeMessage() {
        String name = etName.getText().toString();
        if (!name.isEmpty()) {
            tvWelcome.setText("Welcome, " + name + "!");
        } else {
            tvWelcome.setText("Welcome to ResumeMatch!");
        }
    }
    
    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    private void showCustomDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Settings")
               .setMessage("Choose your preferred language:")
               .setPositiveButton("English (US)", (dialog, which) -> {
                   Toast.makeText(getContext(), "Language set to US English", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("English (UK)", (dialog, which) -> {
                   Toast.makeText(getContext(), "Language set to UK English", Toast.LENGTH_SHORT).show();
               })
               .setNeutralButton("Cancel", null)
               .show();
    }
    
    // AsyncTask to simulate data loading
    private class LoadDataTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        
        @Override
        protected String doInBackground(String... params) {
            String userType = params[0];
            
            // Simulate loading process
            for (int i = 0; i <= 100; i += 20) {
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return userType;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            
            if ("employer".equals(result)) {
                // Navigate to employer section
                showSnackbar("Loading employer dashboard...");
                // Here you would navigate to employer fragment
            } else {
                // Navigate to job seeker section
                showSnackbar("Loading job seeker dashboard...");
                // Here you would navigate to job seeker fragment
            }
        }
    }
} 