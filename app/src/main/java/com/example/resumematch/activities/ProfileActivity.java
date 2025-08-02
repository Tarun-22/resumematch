package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView txt_title;
    private Button btnEditProfile, btnSettings, btnHelp, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backBtn = findViewById(R.id.backButton);
        txt_title = findViewById(R.id.titleText);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSettings = findViewById(R.id.btnSettings);
        btnHelp = findViewById(R.id.btnHelp);
        btnAbout = findViewById(R.id.btnAbout);

        txt_title.setText("Profile & Settings");

        backBtn.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Help functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnAbout.setOnClickListener(v -> {
            Toast.makeText(this, "About functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 