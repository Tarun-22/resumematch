package com.example.resumematch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewParent;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class MatchScoreActivity extends AppCompatActivity {

    //creating the variables for button,score and containers
    ImageView backArrow;
    TextView textScore;
    LinearLayout matchedContainer, missingContainer;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_score);

        //connecting the variables to ui elements from xml using id's
        backArrow = findViewById(R.id.backArrow);
        textScore = findViewById(R.id.textScore);
        matchedContainer = findViewById(R.id.matchedKeywordsContainer);
        missingContainer = findViewById(R.id.missingKeywordsContainer);
        buttonBack = findViewById(R.id.buttonBack);

        //setting the onclick events with a function to these buttons
        backArrow.setOnClickListener(v -> finish());
        buttonBack.setOnClickListener(v -> finish());

        // Get match data from intent
        Intent intent = getIntent();
        if (intent != null) {
            int matchScore = intent.getIntExtra("matchScore", 0);
            String[] matchedKeywords = intent.getStringArrayExtra("matchedKeywords");
            String[] missingKeywords = intent.getStringArrayExtra("missingKeywords");
            String resumeText = intent.getStringExtra("resumeText");
            
            // Get extracted candidate data
            String candidateName = intent.getStringExtra("candidateName");
            String candidateEmail = intent.getStringExtra("candidateEmail");
            String candidatePhone = intent.getStringExtra("candidatePhone");
            String candidateAddress = intent.getStringExtra("candidateAddress");
            String candidateCity = intent.getStringExtra("candidateCity");
            String candidateState = intent.getStringExtra("candidateState");
            String candidateZipCode = intent.getStringExtra("candidateZipCode");
            String candidateTitle = intent.getStringExtra("candidateTitle");
            int experienceYears = intent.getIntExtra("experienceYears", 0);
            String education = intent.getStringExtra("education");
            String availability = intent.getStringExtra("availability");
            String availabilityDetails = intent.getStringExtra("availabilityDetails");
            String transportation = intent.getStringExtra("transportation");
            String expectedSalary = intent.getStringExtra("expectedSalary");
            String startDate = intent.getStringExtra("startDate");
            String workAuthorization = intent.getStringExtra("workAuthorization");
            String emergencyContact = intent.getStringExtra("emergencyContact");
            String emergencyPhone = intent.getStringExtra("emergencyPhone");
            String references = intent.getStringExtra("references");
            String previousRetailExperience = intent.getStringExtra("previousRetailExperience");
            String languages = intent.getStringExtra("languages");
            String certifications = intent.getStringExtra("certifications");
            
            // Get category scores
            int skillScore = intent.getIntExtra("skillScore", 0);
            int experienceScore = intent.getIntExtra("experienceScore", 0);
            int availabilityScore = intent.getIntExtra("availabilityScore", 0);
            int educationScore = intent.getIntExtra("educationScore", 0);
            
            // Get recommendations
            String[] recommendations = intent.getStringArrayExtra("recommendations");
            
            Log.d("MatchScore", "Score: " + matchScore + ", Matched: " + 
                   (matchedKeywords != null ? matchedKeywords.length : 0) + 
                   ", Missing: " + (missingKeywords != null ? missingKeywords.length : 0));
            
            // Display the actual match score
            textScore.setText(matchScore + "%");
            
            // Display candidate information
            displayCandidateInfo(candidateName, candidateEmail, candidatePhone, candidateAddress, candidateCity, candidateState, candidateZipCode, candidateTitle, experienceYears, education, availability, availabilityDetails, transportation, expectedSalary, startDate, workAuthorization, emergencyContact, emergencyPhone, references, previousRetailExperience, languages, certifications);
            
            // Display category scores
            displayCategoryScores(skillScore, experienceScore, availabilityScore, educationScore);
            
            // Display matched keywords
            if (matchedKeywords != null && matchedKeywords.length > 0) {
                settingkeywordchips(matchedContainer, matchedKeywords, true);
            } else {
                TextView noMatchText = new TextView(this);
                noMatchText.setText("No keywords matched");
                noMatchText.setTextColor(Color.parseColor("#999999"));
                noMatchText.setTextSize(14);
                matchedContainer.addView(noMatchText);
            }
            
            // Display missing keywords
            if (missingKeywords != null && missingKeywords.length > 0) {
                settingkeywordchips(missingContainer, missingKeywords, false);
            } else {
                TextView allFoundText = new TextView(this);
                allFoundText.setText("All keywords found!");
                allFoundText.setTextColor(Color.parseColor("#4CAF50"));
                allFoundText.setTextSize(14);
                missingContainer.addView(allFoundText);
            }
            
            // Display recommendations
            displayRecommendations(recommendations);
        }
    }
    
    private void displayCandidateInfo(String name, String email, String phone, String address, String city, String state, String zipCode, String title, int experience, String education, 
                                    String availability, String availabilityDetails, String transportation, String expectedSalary, String startDate, String workAuthorization, String emergencyContact, String emergencyPhone, String references, String previousRetailExperience, String languages, String certifications) {
        // Create candidate info section dynamically
        LinearLayout candidateContainer = new LinearLayout(this);
        candidateContainer.setOrientation(LinearLayout.VERTICAL);
        candidateContainer.setPadding(16, 16, 16, 16);
        candidateContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        // Add title
        TextView titleView = new TextView(this);
        titleView.setText("Candidate Information");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#1976D2"));
        titleView.setPadding(0, 0, 0, 8);
        candidateContainer.addView(titleView);
        
        // Add candidate info
        addInfoRow(candidateContainer, "Name", name);
        addInfoRow(candidateContainer, "Email", email);
        addInfoRow(candidateContainer, "Phone", phone);
        addInfoRow(candidateContainer, "Address", address);
        addInfoRow(candidateContainer, "City", city);
        addInfoRow(candidateContainer, "State", state);
        addInfoRow(candidateContainer, "Zip Code", zipCode);
        addInfoRow(candidateContainer, "Current Title", title);
        addInfoRow(candidateContainer, "Experience", experience + " years");
        addInfoRow(candidateContainer, "Education", education);
        addInfoRow(candidateContainer, "Availability", availability);
        if (availabilityDetails != null && !availabilityDetails.isEmpty()) {
            addInfoRow(candidateContainer, "Availability Details", availabilityDetails);
        }
        if (transportation != null && !transportation.isEmpty()) {
            addInfoRow(candidateContainer, "Transportation", transportation);
        }
        if (expectedSalary != null && !expectedSalary.isEmpty()) {
            addInfoRow(candidateContainer, "Expected Salary", expectedSalary);
        }
        if (startDate != null && !startDate.isEmpty()) {
            addInfoRow(candidateContainer, "Start Date", startDate);
        }
        if (workAuthorization != null && !workAuthorization.isEmpty()) {
            addInfoRow(candidateContainer, "Work Authorization", workAuthorization);
        }
        if (emergencyContact != null && !emergencyContact.isEmpty()) {
            addInfoRow(candidateContainer, "Emergency Contact", emergencyContact);
        }
        if (emergencyPhone != null && !emergencyPhone.isEmpty()) {
            addInfoRow(candidateContainer, "Emergency Phone", emergencyPhone);
        }
        if (references != null && !references.isEmpty()) {
            addInfoRow(candidateContainer, "References", references);
        }
        if (previousRetailExperience != null && !previousRetailExperience.isEmpty()) {
            addInfoRow(candidateContainer, "Previous Retail Experience", previousRetailExperience);
        }
        if (languages != null && !languages.isEmpty()) {
            addInfoRow(candidateContainer, "Languages", languages);
        }
        if (certifications != null && !certifications.isEmpty()) {
            addInfoRow(candidateContainer, "Certifications", certifications);
        }
        
        // Add to the main layout
        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(candidateContainer, 1); // Add after header
        }
    }
    
    private void addInfoRow(LinearLayout container, String label, String value) {
        if (value != null && !value.isEmpty() && !value.equals("Unknown")) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 4, 0, 4);
            
            TextView labelView = new TextView(this);
            labelView.setText(label + ": ");
            labelView.setTypeface(null, android.graphics.Typeface.BOLD);
            labelView.setTextSize(14);
            labelView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            
            TextView valueView = new TextView(this);
            valueView.setText(value);
            valueView.setTextSize(14);
            valueView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            
            row.addView(labelView);
            row.addView(valueView);
            container.addView(row);
        }
    }
    
    private void displayCategoryScores(int skillScore, int experienceScore, int availabilityScore, int educationScore) {
        // Create category scores section dynamically
        LinearLayout categoryContainer = new LinearLayout(this);
        categoryContainer.setOrientation(LinearLayout.VERTICAL);
        categoryContainer.setPadding(16, 16, 16, 16);
        categoryContainer.setBackgroundColor(Color.parseColor("#E8F5E8"));
        
        // Add title
        TextView titleView = new TextView(this);
        titleView.setText("Category Scores");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#2E7D32"));
        titleView.setPadding(0, 0, 0, 8);
        categoryContainer.addView(titleView);
        
        // Add category scores
        addCategoryScore(categoryContainer, "Skills Match", skillScore);
        addCategoryScore(categoryContainer, "Experience", experienceScore);
        addCategoryScore(categoryContainer, "Availability", availabilityScore);
        addCategoryScore(categoryContainer, "Education", educationScore);
        
        // Add to the main layout
        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(categoryContainer, 2); // Add after candidate info
        }
    }
    
    private void addCategoryScore(LinearLayout container, String category, int score) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);
        
        TextView categoryView = new TextView(this);
        categoryView.setText(category + ": ");
        categoryView.setTypeface(null, android.graphics.Typeface.BOLD);
        categoryView.setTextSize(14);
        categoryView.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        ));
        
        TextView scoreView = new TextView(this);
        scoreView.setText(score + "%");
        scoreView.setTextSize(14);
        scoreView.setTextColor(getScoreColor(score));
        scoreView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        row.addView(categoryView);
        row.addView(scoreView);
        container.addView(row);
    }
    
    private int getScoreColor(int score) {
        if (score >= 80) return Color.parseColor("#4CAF50"); // Green
        else if (score >= 60) return Color.parseColor("#FF9800"); // Orange
        else return Color.parseColor("#F44336"); // Red
    }
    
    private void displayRecommendations(String[] recommendations) {
        if (recommendations == null || recommendations.length == 0) return;
        
        // Create recommendations section dynamically
        LinearLayout recommendationsContainer = new LinearLayout(this);
        recommendationsContainer.setOrientation(LinearLayout.VERTICAL);
        recommendationsContainer.setPadding(16, 16, 16, 16);
        recommendationsContainer.setBackgroundColor(Color.parseColor("#FFF3E0"));
        
        // Add recommendations title
        TextView titleView = new TextView(this);
        titleView.setText("Recommendations");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#E65100"));
        titleView.setPadding(0, 0, 0, 8);
        recommendationsContainer.addView(titleView);
        
        // Add each recommendation
        for (String recommendation : recommendations) {
            TextView recView = new TextView(this);
            recView.setText("• " + recommendation);
            recView.setTextSize(14);
            recView.setPadding(0, 4, 0, 4);
            recommendationsContainer.addView(recView);
        }
        
        // Add to the main layout
        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(recommendationsContainer, 3); // Add after category scores
        }
    }

    // Updated function to work with LinearLayout instead of FlowLayout
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
}
