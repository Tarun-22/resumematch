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

public class ScanResumeFragment extends Fragment {

    private Button btnCamera, btnUpload, btnProcess, btnClear, btnExtractFields;
    private EditText etResumeText, etJobTitle;
    private TextView tvStatus, tvExtractedFields;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_resume, container, false);
        
        // Initialize views
        btnCamera = view.findViewById(R.id.btnCamera);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnProcess = view.findViewById(R.id.btnProcess);
        btnClear = view.findViewById(R.id.btnClear);
        btnExtractFields = view.findViewById(R.id.btnExtractFields);
        etResumeText = view.findViewById(R.id.etResumeText);
        etJobTitle = view.findViewById(R.id.etJobTitle);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvExtractedFields = view.findViewById(R.id.tvExtractedFields);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Set click listeners
        btnCamera.setOnClickListener(v -> {
            showSnackbar("Opening camera to scan physical resume...");
            new ScanResumeTask().execute("camera");
        });
        
        btnUpload.setOnClickListener(v -> {
            showSnackbar("Opening gallery to upload resume photo...");
            new ScanResumeTask().execute("upload");
        });
        
        btnProcess.setOnClickListener(v -> {
            String resumeText = etResumeText.getText().toString();
            String jobTitle = etJobTitle.getText().toString();
            if (!resumeText.isEmpty() && !jobTitle.isEmpty()) {
                showSnackbar("Processing resume against job: " + jobTitle);
                new ProcessResumeTask().execute(resumeText, jobTitle);
            } else {
                if (resumeText.isEmpty()) {
                    Toast.makeText(getContext(), "Please scan or enter resume text first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please enter a job title to match against", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        btnExtractFields.setOnClickListener(v -> {
            String resumeText = etResumeText.getText().toString();
            if (!resumeText.isEmpty()) {
                showSnackbar("Extracting fields from resume...");
                new ExtractFieldsTask().execute(resumeText);
            } else {
                Toast.makeText(getContext(), "Please scan or enter resume text first", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnClear.setOnClickListener(v -> {
            showClearConfirmationDialog();
        });
        
        return view;
    }
    
    private void showClearConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Clear All Data")
               .setMessage("Are you sure you want to clear all text and extracted fields? This action cannot be undone.")
               .setPositiveButton("Clear", (dialog, which) -> {
                   etResumeText.setText("");
                   etJobTitle.setText("");
                   tvStatus.setText("Ready to scan physical resume");
                   tvExtractedFields.setText("Fields will be extracted here...");
                   showSnackbar("All data cleared");
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
    
    // AsyncTask to simulate scanning
    private class ScanResumeTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            tvStatus.setText("Scanning physical resume...");
        }
        
        @Override
        protected String doInBackground(String... params) {
            String method = params[0];
            
            // Simulate scanning process
            for (int i = 0; i <= 100; i += 20) {
                publishProgress(i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Simulate extracted text from physical resume
            return "JOHN DOE\n" +
                   "Software Engineer\n" +
                   "Email: john.doe@email.com\n" +
                   "Phone: (555) 123-4567\n" +
                   "Location: New York, NY\n\n" +
                   "EXPERIENCE\n" +
                   "Senior Software Engineer - Tech Corp (2020-2023)\n" +
                   "• Developed web applications using Java, Spring Boot, React\n" +
                   "• Led team of 5 developers on multiple projects\n" +
                   "• Improved system performance by 40%\n\n" +
                   "Software Engineer - Startup Inc (2018-2020)\n" +
                   "• Built REST APIs using Python, Django\n" +
                   "• Worked with AWS, Docker, Kubernetes\n\n" +
                   "EDUCATION\n" +
                   "Bachelor of Science in Computer Science\n" +
                   "University of Technology (2014-2018)\n\n" +
                   "SKILLS\n" +
                   "Java, Python, JavaScript, React, Spring Boot, AWS, Docker, Git, Agile";
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            etResumeText.setText(result);
            tvStatus.setText("Physical resume scanned successfully");
            showSnackbar("Resume text extracted from physical document!");
        }
    }
    
    // AsyncTask to simulate processing
    private class ProcessResumeTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            tvStatus.setText("Processing against job requirements...");
        }
        
        @Override
        protected String doInBackground(String... params) {
            String resumeText = params[0];
            String jobTitle = params[1];
            
            // Simulate processing
            for (int i = 0; i <= 100; i += 25) {
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return "Match Score: 87%\n" +
                   "Job: " + jobTitle + "\n" +
                   "Matched Skills: Java, Python, React, AWS, Docker\n" +
                   "Missing Skills: Kubernetes, Microservices\n" +
                   "Experience Level: Senior (5+ years)\n" +
                   "Recommendation: Strong candidate for interview\n\n" +
                   "Next Steps:\n" +
                   "• Schedule technical interview\n" +
                   "• Review portfolio projects\n" +
                   "• Check references";
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            tvStatus.setText("Processing complete");
            showCustomDialog("Resume Analysis Results", result);
            Toast.makeText(getContext(), "Resume processed successfully", Toast.LENGTH_SHORT).show();
        }
    }
    
    // AsyncTask to extract fields
    private class ExtractFieldsTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            tvStatus.setText("Extracting fields...");
        }
        
        @Override
        protected String doInBackground(String... params) {
            String resumeText = params[0];
            
            // Simulate field extraction
            for (int i = 0; i <= 100; i += 25) {
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return "EXTRACTED FIELDS:\n" +
                   "Name: John Doe\n" +
                   "Email: john.doe@email.com\n" +
                   "Phone: (555) 123-4567\n" +
                   "Location: New York, NY\n" +
                   "Current Title: Software Engineer\n" +
                   "Experience: 5 years\n" +
                   "Education: Bachelor's in Computer Science\n" +
                   "Key Skills: Java, Python, React, AWS, Docker, Git\n" +
                   "Expected Salary: $120k - $140k\n" +
                   "Availability: Immediate";
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            tvExtractedFields.setText(result);
            tvStatus.setText("Fields extracted successfully");
            showSnackbar("Resume fields extracted!");
        }
    }
} 