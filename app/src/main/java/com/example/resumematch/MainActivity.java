package com.example.resumematch;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new EmployerDashboardFragment());
        }
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employer_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_job_listings) {
            loadFragment(new JobListingsFragment());
            return true;
        } else if (id == R.id.action_create_job) {
            loadFragment(new CreateJobFragment());
            return true;
        } else if (id == R.id.action_scan_resume) {
            loadFragment(new ScanResumeFragment());
            return true;
        } else if (id == R.id.action_applications) {
            loadFragment(new ApplicationsFragment());
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ResumeMatch - Employer Help")
               .setMessage("Author: Your Name\n" +
                          "Version: 1.0\n\n" +
                          "How to use:\n" +
                          "1. Create job listings with availability and skills\n" +
                          "2. Scan physical resumes using camera or upload\n" +
                          "3. View match scores and applicant details\n" +
                          "4. Manage your job postings and applications\n\n" +
                          "The app uses OCR technology to extract text from resumes and matches them with your job requirements.")
               .setPositiveButton("OK", null)
               .show();
    }
}