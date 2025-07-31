package com.example.resumematch;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class EnhancedMatchScoreActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView textOverallScore, textRecommendation;
    private TextView textSkillsScore, textExperienceScore, textAvailabilityScore, textEducationScore;
    private LinearLayout matchedContainer, missingContainer;
    private LinearLayout extractedDataContainer;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_match_score);

        // Initialize views
        backArrow = findViewById(R.id.backArrow);
        textOverallScore = findViewById(R.id.textOverallScore);
        textRecommendation = findViewById(R.id.textRecommendation);
        textSkillsScore = findViewById(R.id.textSkillsScore);
        textExperienceScore = findViewById(R.id.textExperienceScore);
        textAvailabilityScore = findViewById(R.id.textAvailabilityScore);
        textEducationScore = findViewById(R.id.textEducationScore);
        matchedContainer = findViewById(R.id.matchedSkillsContainer);
        missingContainer = findViewById(R.id.missingSkillsContainer);
        extractedDataContainer = findViewById(R.id.extractedDataContainer);
        buttonBack = findViewById(R.id.buttonBack);

        // Set up click listeners
        backArrow.setOnClickListener(v -> finish());
        buttonBack.setOnClickListener(v -> finish());

        // Get enhanced match data from intent
        Intent intent = getIntent();
        if (intent != null) {
            int overallScore = intent.getIntExtra("overallScore", 0);
            int skillsScore = intent.getIntExtra("skillsScore", 0);
            int experienceScore = intent.getIntExtra("experienceScore", 0);
            int availabilityScore = intent.getIntExtra("availabilityScore", 0);
            int educationScore = intent.getIntExtra("educationScore", 0);
            String[] matchedSkills = intent.getStringArrayExtra("matchedSkills");
            String[] missingSkills = intent.getStringArrayExtra("missingSkills");
            String recommendation = intent.getStringExtra("recommendation");
            ResumeDataExtractor.ExtractedData extractedData = 
                (ResumeDataExtractor.ExtractedData) intent.getSerializableExtra("extractedData");
            
            Log.d("EnhancedMatchScore", "Overall Score: " + overallScore + 
                   ", Skills: " + skillsScore + ", Experience: " + experienceScore);
            
            // Display overall score
            textOverallScore.setText(overallScore + "%");
            setScoreColor(textOverallScore, overallScore);
            
            // Display recommendation
            textRecommendation.setText(recommendation);
            
            // Display individual scores
            textSkillsScore.setText("Skills: " + skillsScore + "%");
            setScoreColor(textSkillsScore, skillsScore);
            
            textExperienceScore.setText("Experience: " + experienceScore + "%");
            setScoreColor(textExperienceScore, experienceScore);
            
            textAvailabilityScore.setText("Availability: " + availabilityScore + "%");
            setScoreColor(textAvailabilityScore, availabilityScore);
            
            textEducationScore.setText("Education: " + educationScore + "%");
            setScoreColor(textEducationScore, educationScore);
            
            // Display matched skills
            if (matchedSkills != null && matchedSkills.length > 0) {
                settingkeywordchips(matchedContainer, matchedSkills, true);
            } else {
                TextView noMatchText = new TextView(this);
                noMatchText.setText("No skills matched");
                noMatchText.setTextColor(Color.parseColor("#999999"));
                noMatchText.setTextSize(14);
                matchedContainer.addView(noMatchText);
            }
            
            // Display missing skills
            if (missingSkills != null && missingSkills.length > 0) {
                settingkeywordchips(missingContainer, missingSkills, false);
            } else {
                TextView allFoundText = new TextView(this);
                allFoundText.setText("All required skills found!");
                allFoundText.setTextColor(Color.parseColor("#4CAF50"));
                allFoundText.setTextSize(14);
                missingContainer.addView(allFoundText);
            }
            
            // Display extracted data
            if (extractedData != null) {
                displayExtractedData(extractedData);
            }
        }
    }

    private void setScoreColor(TextView textView, int score) {
        if (score >= 80) {
            textView.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (score >= 60) {
            textView.setTextColor(Color.parseColor("#FF9800")); // Orange
        } else {
            textView.setTextColor(Color.parseColor("#F44336")); // Red
        }
    }

    private void settingkeywordchips(LinearLayout container, String[] keywords, boolean matched) {
        container.removeAllViews();
        
        // Create a horizontal LinearLayout for each row
        LinearLayout currentRow = new LinearLayout(this);
        currentRow.setOrientation(LinearLayout.HORIZONTAL);
        currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            
            // Create chip TextView
            TextView chip = new TextView(this);
            chip.setText(keyword);
            chip.setTextSize(14);
            chip.setPadding(24, 12, 24, 12);
            chip.setTextColor(matched ? Color.WHITE : Color.parseColor("#999999"));
            chip.setBackgroundResource(matched ? R.drawable.chip_matched : R.drawable.chip_missing);
            
            // Set layout parameters with margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            chip.setLayoutParams(params);
            
            // Add chip to current row
            currentRow.addView(chip);
            
            // Start a new row every 3 chips or at the end
            if ((i + 1) % 3 == 0 || i == keywords.length - 1) {
                container.addView(currentRow);
                if (i < keywords.length - 1) {
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                }
            }
        }
    }

    private void displayExtractedData(ResumeDataExtractor.ExtractedData data) {
        // Clear existing views
        extractedDataContainer.removeAllViews();
        
        // Add extracted data fields
        addDataField("Name", data.getName());
        addDataField("Email", data.getEmail());
        addDataField("Phone", data.getPhone());
        addDataField("Location", data.getLocation());
        addDataField("Current Title", data.getCurrentTitle());
        addDataField("Years of Experience", String.valueOf(data.getYearsOfExperience()));
        addDataField("Education", data.getEducation());
        addDataField("Expected Salary", data.getExpectedSalary());
        
        // Add skills
        if (!data.getSkills().isEmpty()) {
            addDataField("Skills", String.join(", ", data.getSkills()));
        }
        
        // Add availability
        if (!data.getAvailability().isEmpty()) {
            addDataField("Availability", String.join(", ", data.getAvailability()));
        }
    }

    private void addDataField(String label, String value) {
        if (value != null && !value.isEmpty() && !value.equals("Unknown")) {
            TextView fieldView = new TextView(this);
            fieldView.setText(label + ": " + value);
            fieldView.setTextSize(14);
            fieldView.setTextColor(Color.parseColor("#333333"));
            fieldView.setPadding(0, 4, 0, 4);
            extractedDataContainer.addView(fieldView);
        }
    }
} 