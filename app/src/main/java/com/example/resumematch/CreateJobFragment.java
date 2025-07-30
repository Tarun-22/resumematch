package com.example.resumematch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;

public class CreateJobFragment extends Fragment {

    private EditText etJobTitle, etJobDescription, etSalary, etLocation, etSkills, etStartTime, etEndTime;
    private Button btnCreateJob, btnClear, btnSave, btnPreview;
    private ProgressBar progressBar;
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_job, container, false);
        
        // Initialize views
        etJobTitle = view.findViewById(R.id.etJobTitle);
        etJobDescription = view.findViewById(R.id.etJobDescription);
        etSalary = view.findViewById(R.id.etSalary);
        etLocation = view.findViewById(R.id.etLocation);
        etSkills = view.findViewById(R.id.etSkills);
        etStartTime = view.findViewById(R.id.etStartTime);
        etEndTime = view.findViewById(R.id.etEndTime);
        btnCreateJob = view.findViewById(R.id.btnCreateJob);
        btnClear = view.findViewById(R.id.btnClear);
        btnSave = view.findViewById(R.id.btnSave);
        btnPreview = view.findViewById(R.id.btnPreview);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Initialize checkboxes
        cbMonday = view.findViewById(R.id.cbMonday);
        cbTuesday = view.findViewById(R.id.cbTuesday);
        cbWednesday = view.findViewById(R.id.cbWednesday);
        cbThursday = view.findViewById(R.id.cbThursday);
        cbFriday = view.findViewById(R.id.cbFriday);
        cbSaturday = view.findViewById(R.id.cbSaturday);
        cbSunday = view.findViewById(R.id.cbSunday);
        
        // Set click listeners
        btnCreateJob.setOnClickListener(v -> {
            if (validateForm()) {
                showSnackbar("Creating job listing...");
                new CreateJobTask().execute();
            } else {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnClear.setOnClickListener(v -> {
            showClearConfirmationDialog();
        });
        
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                showSnackbar("Saving draft...");
                new SaveDraftTask().execute();
            } else {
                Toast.makeText(getContext(), "Please fill all required fields to save", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnPreview.setOnClickListener(v -> {
            if (validateForm()) {
                showCustomDialog("Job Preview", 
                    "Title: " + etJobTitle.getText().toString() + "\n" +
                    "Description: " + etJobDescription.getText().toString() + "\n" +
                    "Skills: " + etSkills.getText().toString() + "\n" +
                    "Salary: " + etSalary.getText().toString() + "\n" +
                    "Location: " + etLocation.getText().toString() + "\n" +
                    "Timing: " + etStartTime.getText().toString() + " - " + etEndTime.getText().toString() + "\n" +
                    "Availability: " + getSelectedDays());
            } else {
                Toast.makeText(getContext(), "Please fill all fields to preview", Toast.LENGTH_SHORT).show();
            }
        });
        
        return view;
    }
    
    private boolean validateForm() {
        boolean isValid = !etJobTitle.getText().toString().isEmpty() &&
               !etJobDescription.getText().toString().isEmpty() &&
               !etSalary.getText().toString().isEmpty() &&
               !etLocation.getText().toString().isEmpty() &&
               !etSkills.getText().toString().isEmpty() &&
               !etStartTime.getText().toString().isEmpty() &&
               !etEndTime.getText().toString().isEmpty() &&
               (cbMonday.isChecked() || cbTuesday.isChecked() || cbWednesday.isChecked() || 
                cbThursday.isChecked() || cbFriday.isChecked() || cbSaturday.isChecked() || cbSunday.isChecked());
        
        if (!isValid) {
            showSnackbar("Please fill all required fields and select at least one availability day");
        }
        
        return isValid;
    }
    
    private String getSelectedDays() {
        StringBuilder days = new StringBuilder();
        if (cbMonday.isChecked()) days.append("Mon ");
        if (cbTuesday.isChecked()) days.append("Tue ");
        if (cbWednesday.isChecked()) days.append("Wed ");
        if (cbThursday.isChecked()) days.append("Thu ");
        if (cbFriday.isChecked()) days.append("Fri ");
        if (cbSaturday.isChecked()) days.append("Sat ");
        if (cbSunday.isChecked()) days.append("Sun ");
        return days.toString().trim();
    }
    
    private void showClearConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Clear Form")
               .setMessage("Are you sure you want to clear all fields? This action cannot be undone.")
               .setPositiveButton("Clear", (dialog, which) -> {
                   clearForm();
                   showSnackbar("Form cleared");
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void clearForm() {
        etJobTitle.setText("");
        etJobDescription.setText("");
        etSalary.setText("");
        etLocation.setText("");
        etSkills.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        cbMonday.setChecked(false);
        cbTuesday.setChecked(false);
        cbWednesday.setChecked(false);
        cbThursday.setChecked(false);
        cbFriday.setChecked(false);
        cbSaturday.setChecked(false);
        cbSunday.setChecked(false);
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
    
    // AsyncTask to simulate job creation
    private class CreateJobTask extends AsyncTask<Void, Integer, Boolean> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        
        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate job creation process
            for (int i = 0; i <= 100; i += 20) {
                publishProgress(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            
            if (success) {
                showSnackbar("Job listing created successfully!");
                Toast.makeText(getContext(), "Job posting created and published", Toast.LENGTH_SHORT).show();
                clearForm();
                // Navigate back to job listings
                navigateToFragment(new JobListingsFragment());
            }
        }
    }
    
    // AsyncTask to simulate saving draft
    private class SaveDraftTask extends AsyncTask<Void, Integer, Boolean> {
        
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        
        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate saving process
            for (int i = 0; i <= 100; i += 25) {
                publishProgress(i);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            
            if (success) {
                showSnackbar("Draft saved successfully!");
                Toast.makeText(getContext(), "Job draft saved", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 