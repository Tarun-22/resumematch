package com.example.resumematch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.resumematch.R;
import java.io.File;

public class ResumeImageActivity extends AppCompatActivity {

    private ImageView imageViewResume;
    private TextView textViewResumeText;
    private Button buttonBack, buttonShareImage;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_image);

        // Initialize views
        imageViewResume = findViewById(R.id.imageViewResume);
        textViewResumeText = findViewById(R.id.textViewResumeText);
        buttonBack = findViewById(R.id.buttonBack);
        buttonShareImage = findViewById(R.id.buttonShareImage);

        // Set up back button
        buttonBack.setOnClickListener(v -> finish());

        // Set up share button
        buttonShareImage.setOnClickListener(v -> shareResumeImage());

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            photoPath = intent.getStringExtra("photoPath");
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

    private void shareResumeImage() {
        if (photoPath == null || photoPath.isEmpty()) {
            return;
        }

        File file = new File(photoPath);
        if (!file.exists()) {
            return;
        }

        Uri contentUri = FileProvider.getUriForFile(this, "com.example.resumematch.fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Resume Image");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Resume image from ResumeMatch app");

        startActivity(Intent.createChooser(shareIntent, "Share Resume Image"));
    }
} 