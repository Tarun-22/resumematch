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

    private ImageView imgviewresume;
    private TextView txt_viewresumetxt;
    private Button btonback, btnshareimg;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_image);

        imgviewresume = findViewById(R.id.imageViewResume);
        txt_viewresumetxt = findViewById(R.id.textViewResumeText);
        btonback = findViewById(R.id.buttonBack);
        btnshareimg = findViewById(R.id.buttonShareImage);

        btonback.setOnClickListener(v -> finish());

        btnshareimg.setOnClickListener(v -> shareresumeimg());

        Intent intent = getIntent();
        if (intent != null) {
            photoPath = intent.getStringExtra("photoPath");
            String resumeText = intent.getStringExtra("resumeText");

            if (photoPath != null && !photoPath.isEmpty()) {
                displayresumeimg(photoPath);
            } else {
                imgviewresume.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            if (resumeText != null && !resumeText.isEmpty()) {
                txt_viewresumetxt.setText("Extracted Text:\n\n" + resumeText);
            } else {
                txt_viewresumetxt.setText("No text extracted from resume");
            }
        }
    }

    private void displayresumeimg(String photoPath) {
        try {
            File imageFile = new File(photoPath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    imgviewresume.setImageBitmap(bitmap);
                } else {
                    imgviewresume.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                imgviewresume.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } catch (Exception e) {
            imgviewresume.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void shareresumeimg() {
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