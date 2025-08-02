package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.activities.StoreProfileActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView titleText;
    private Button btnEditProfile, btnSettings, btnHelp, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSettings = findViewById(R.id.btnSettings);
        btnHelp = findViewById(R.id.btnHelp);
        btnAbout = findViewById(R.id.btnAbout);

        // Set title
        titleText.setText("Profile & Settings");

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            // Edit profile functionality
            Toast.makeText(this, "Edit Profile functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            // Open settings functionality
            Toast.makeText(this, "Settings functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnHelp.setOnClickListener(v -> {
            // Show help functionality
            Toast.makeText(this, "Help functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnAbout.setOnClickListener(v -> {
            // Show about functionality
            Toast.makeText(this, "About functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 