package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScanResumeActivity extends AppCompatActivity {

    ImageView backButton;
    Button buttonCamera, buttonUpload;
    TextView textOCRPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resume);

        backButton = findViewById(R.id.backButton);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonUpload = findViewById(R.id.buttonUpload);
        textOCRPreview = findViewById(R.id.textOCRPreview);

        backButton.setOnClickListener(v -> finish());

        buttonCamera.setOnClickListener(v ->
                textOCRPreview.setText("📷 Scanned via Camera (mock text)..."));

        buttonUpload.setOnClickListener(v ->
                textOCRPreview.setText("🖼️ Uploaded image (mock text)..."));
    }
}
