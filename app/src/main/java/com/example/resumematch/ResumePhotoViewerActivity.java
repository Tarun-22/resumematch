package com.example.resumematch;

import android.content.Intent;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;

public class ResumePhotoViewerActivity extends AppCompatActivity {
    
    private ImageView imageViewResume;
    private TextView textViewResumeId;
    private TextView textViewJobTitle;
    private TextView textViewDate;
    private TextView textViewMatchScore;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_photo_viewer);
        
        // Initialize views
        imageViewResume = findViewById(R.id.imageViewResume);
        textViewResumeId = findViewById(R.id.textViewResumeId);
        textViewJobTitle = findViewById(R.id.textViewJobTitle);
        textViewDate = findViewById(R.id.textViewDate);
        textViewMatchScore = findViewById(R.id.textViewMatchScore);
        
        // Get data from intent
        String resumeId = getIntent().getStringExtra("resumeId");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String date = getIntent().getStringExtra("date");
        String matchScore = getIntent().getStringExtra("matchScore");
        photoPath = getIntent().getStringExtra("photoPath");
        
        // Display resume information
        if (resumeId != null) textViewResumeId.setText("Resume ID: " + resumeId);
        if (jobTitle != null) textViewJobTitle.setText("Job: " + jobTitle);
        if (date != null) textViewDate.setText("Date: " + date);
        if (matchScore != null) textViewMatchScore.setText("Match Score: " + matchScore);
        
        // Load and display the resume photo
        if (photoPath != null && !photoPath.isEmpty()) {
            loadResumePhoto(photoPath);
        } else {
            textViewResumeId.setText("No photo available");
        }
        findViewById(R.id.buttonShareResumeImage).setOnClickListener(v -> shareResumeImage());
    }
    
    private void loadResumePhoto(String photoPath) {
        try {
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (bitmap != null) {
                    imageViewResume.setImageBitmap(bitmap);
                } else {
                    textViewResumeId.setText("Error loading photo");
                }
            } else {
                textViewResumeId.setText("Photo file not found");
            }
        } catch (Exception e) {
            textViewResumeId.setText("Error: " + e.getMessage());
        }
    }

    private void shareResumeImage() {
        if (photoPath == null || photoPath.isEmpty()) {
            return;
        }
        File photoFile = new File(photoPath);
        if (!photoFile.exists()) {
            return;
        }
        Uri contentUri = FileProvider.getUriForFile(
            this,
            "com.example.resumematch.fileprovider",
            photoFile
        );
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Resume Image"));
    }
} 