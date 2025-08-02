package com.example.resumematch.activities;

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

import com.example.resumematch.R;

public class ResumePhotoViewerActivity extends AppCompatActivity {
    
    private ImageView imgviewresume;
    private TextView txt_viewresumeId;
    private TextView txt_viewjobTitle;
    private TextView txt_viewdate;
    private TextView txt_viewmatchscore;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_photo_viewer);
        
        imgviewresume = findViewById(R.id.imageViewResume);
        txt_viewresumeId = findViewById(R.id.textViewResumeId);
        txt_viewjobTitle = findViewById(R.id.textViewJobTitle);
        txt_viewdate = findViewById(R.id.textViewDate);
        txt_viewmatchscore = findViewById(R.id.textViewMatchScore);
        
        String resumeId = getIntent().getStringExtra("resumeId");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String date = getIntent().getStringExtra("date");
        String matchScore = getIntent().getStringExtra("matchScore");
        photoPath = getIntent().getStringExtra("photoPath");
        
        if (resumeId != null) txt_viewresumeId.setText("Resume ID: " + resumeId);
        if (jobTitle != null) txt_viewjobTitle.setText("Job: " + jobTitle);
        if (date != null) txt_viewdate.setText("Date: " + date);
        if (matchScore != null) txt_viewmatchscore.setText("Match Score: " + matchScore);
        
        if (photoPath != null && !photoPath.isEmpty()) {
            loadResumePhoto(photoPath);
        } else {
            txt_viewresumeId.setText("No photo available");
        }
        findViewById(R.id.buttonShareResumeImage).setOnClickListener(v -> shareResumeImage());
    }
    
    private void loadResumePhoto(String photoPath) {
        try {
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (bitmap != null) {
                    imgviewresume.setImageBitmap(bitmap);
                } else {
                    txt_viewresumeId.setText("Error loading photo");
                }
            } else {
                txt_viewresumeId.setText("Photo file not found");
            }
        } catch (Exception e) {
            txt_viewresumeId.setText("Error: " + e.getMessage());
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