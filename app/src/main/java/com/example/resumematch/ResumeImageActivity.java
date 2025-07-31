package com.example.resumematch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ResumeImageActivity extends AppCompatActivity {

    private ImageView imageViewResume;
    private TextView textViewResumeText;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_image);

        // Initialize views
        imageViewResume = findViewById(R.id.imageViewResume);
        textViewResumeText = findViewById(R.id.textViewResumeText);
        buttonBack = findViewById(R.id.buttonBack);

        // Set up back button
        buttonBack.setOnClickListener(v -> finish());

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String photoPath = intent.getStringExtra("photoPath");
            String resumeText = intent.getStringExtra("resumeText");

            // Display resume image
            if (photoPath != null && !photoPath.isEmpty()) {
                displayResumeImage(photoPath);
            } else {
                imageViewResume.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Display resume text
            if (resumeText != null && !resumeText.isEmpty()) {
                textViewResumeText.setText("Extracted Text:\n\n" + resumeText);
            } else {
                textViewResumeText.setText("No text extracted from resume");
            }
        }
    }

    private void displayResumeImage(String photoPath) {
        try {
            File imageFile = new File(photoPath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    imageViewResume.setImageBitmap(bitmap);
                } else {
                    imageViewResume.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                imageViewResume.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } catch (Exception e) {
            imageViewResume.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
} 