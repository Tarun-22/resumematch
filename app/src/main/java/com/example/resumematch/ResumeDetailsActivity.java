package com.example.resumematch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResumeDetailsActivity extends AppCompatActivity {

    private TextView textResumeId, textResumeDate, textJobTitle, textMatchScore;
    private TextView textResumeContent, textMatchedKeywords, textMissingKeywords;
    private Button buttonBack, buttonContact, buttonSchedule;
    private ImageView backButton;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_details);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Get resume details from intent
        String resumeId = getIntent().getStringExtra("resumeId");
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String resumeDate = getIntent().getStringExtra("resumeDate");
        String matchScore = getIntent().getStringExtra("matchScore");
        String resumeContent = getIntent().getStringExtra("resumeContent");
        String[] matchedKeywords = getIntent().getStringArrayExtra("matchedKeywords");
        String[] missingKeywords = getIntent().getStringArrayExtra("missingKeywords");

        // Initialize views
        backButton = findViewById(R.id.backButton);
        textResumeId = findViewById(R.id.textResumeId);
        textResumeDate = findViewById(R.id.textResumeDate);
        textJobTitle = findViewById(R.id.textJobTitle);
        textMatchScore = findViewById(R.id.textMatchScore);
        textResumeContent = findViewById(R.id.textResumeContent);
        textMatchedKeywords = findViewById(R.id.textMatchedKeywords);
        textMissingKeywords = findViewById(R.id.textMissingKeywords);
        buttonBack = findViewById(R.id.buttonBack);
        buttonContact = findViewById(R.id.buttonContact);
        buttonSchedule = findViewById(R.id.buttonSchedule);

        // Set up data
        textResumeId.setText("Resume ID: " + resumeId);
        textResumeDate.setText("Date: " + resumeDate);
        textJobTitle.setText("Applied for: " + jobTitle);
        textMatchScore.setText("Match Score: " + matchScore);
        textResumeContent.setText("Resume Content:\n\n" + resumeContent);

        // Set matched keywords
        if (matchedKeywords != null && matchedKeywords.length > 0) {
            StringBuilder matched = new StringBuilder("Matched Keywords:\n");
            for (String keyword : matchedKeywords) {
                matched.append("• ").append(keyword).append("\n");
            }
            textMatchedKeywords.setText(matched.toString());
        } else {
            textMatchedKeywords.setText("No keywords matched");
        }

        // Set missing keywords
        if (missingKeywords != null && missingKeywords.length > 0) {
            StringBuilder missing = new StringBuilder("Missing Keywords:\n");
            for (String keyword : missingKeywords) {
                missing.append("• ").append(keyword).append("\n");
            }
            textMissingKeywords.setText(missing.toString());
        } else {
            textMissingKeywords.setText("All keywords found!");
        }

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());
        buttonBack.setOnClickListener(v -> finish());
        
        buttonContact.setOnClickListener(v -> {
            // TODO: Implement contact functionality
            android.widget.Toast.makeText(this, "Contact feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        buttonSchedule.setOnClickListener(v -> {
            // TODO: Implement interview scheduling
            android.widget.Toast.makeText(this, "Interview scheduling coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 