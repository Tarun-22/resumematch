package com.example.resumematch.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.ViewParent;

import androidx.core.content.FileProvider;

import java.io.File;

import com.example.resumematch.R;

public class MatchScoreActivity extends AppCompatActivity {

    ImageView backArrow;
    TextView txt_Score;
    LinearLayout matchedCont, missingCont;
    Button btnBack, btnViewImg, btnShareImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_score);

        backArrow = findViewById(R.id.backArrow);
        txt_Score = findViewById(R.id.textScore);
        matchedCont = findViewById(R.id.matchedKeywordsContainer);
        missingCont = findViewById(R.id.missingKeywordsContainer);
        btnBack = findViewById(R.id.buttonBack);
        btnViewImg = findViewById(R.id.buttonViewImage);
        btnShareImg = findViewById(R.id.buttonShareImage);

        backArrow.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
        btnViewImg.setOnClickListener(v -> {
            String photoPath = getIntent().getStringExtra("photoPath");
            String resumeText = getIntent().getStringExtra("resumeText");

            Intent intent = new Intent(MatchScoreActivity.this, ResumeImageActivity.class);
            intent.putExtra("photoPath", photoPath);
            intent.putExtra("resumeText", resumeText);
            startActivity(intent);
        });

        btnShareImg.setOnClickListener(v -> {
            String photoPath = getIntent().getStringExtra("photoPath");
            shareResumeImage(photoPath);
        });

        Intent intent = getIntent();
        if (intent != null) {
            int matchScore = intent.getIntExtra("matchScore", 0);
            String[] matchedKeywords = intent.getStringArrayExtra("matchedKeywords");
            String[] missingKeywords = intent.getStringArrayExtra("missingKeywords");
            String resumeText = intent.getStringExtra("resumeText");

            String candidate_Name = intent.getStringExtra("candidate_Name");
            String candidate_Email = intent.getStringExtra("candidate_Email");
            String candidate_Phone = intent.getStringExtra("candidate_Phone");
            String candidAddress = intent.getStringExtra("candidAddress");
            String candidCity = intent.getStringExtra("candidCity");
            String candidState = intent.getStringExtra("candidState");
            String candidZipCode = intent.getStringExtra("candidZipCode");
            String candidateTitle = intent.getStringExtra("candidateTitle");
            int experienceYears = intent.getIntExtra("experienceYears", 0);
            String education = intent.getStringExtra("education");
            String avail = intent.getStringExtra("avail");
            String availDetails = intent.getStringExtra("availDetails");
            String transportation = intent.getStringExtra("transportation");
            String startDate = intent.getStringExtra("startDate");
            String workAuthorization = intent.getStringExtra("workAuthorization");
            String emergencyContact = intent.getStringExtra("emergencyContact");
            String emergencyPhone = intent.getStringExtra("emergencyPhone");
            String refer = intent.getStringExtra("refer");
            String prevRetailExp = intent.getStringExtra("prevRetailExp");
            String lang = intent.getStringExtra("lang");
            String cert = intent.getStringExtra("cert");

            int skillScore = intent.getIntExtra("skillScore", 0);
            int experienceScore = intent.getIntExtra("experienceScore", 0);
            int availabilityScore = intent.getIntExtra("availabilityScore", 0);
            int educationScore = intent.getIntExtra("educationScore", 0);
            int distanceScore = intent.getIntExtra("distanceScore", 0);
            double distanceMiles = intent.getDoubleExtra("distanceMiles", 0.0);
            String distanceDescription = intent.getStringExtra("distanceDescription");

            String feedback = intent.getStringExtra("feedback");
            String recommendations = intent.getStringExtra("recommendations");

            String storeName = intent.getStringExtra("storeName");
            String storeAddress = intent.getStringExtra("storeAddress");

            double distanceKm = distanceMiles * 1.60934;

            Log.d("MatchScore", "Score: " + matchScore + ", Feedback: " + feedback + ", Recommendations: " + recommendations);

            txt_Score.setText(matchScore + "%");

            displayCandidInfo(candidate_Name, candidate_Email, candidate_Phone, candidAddress, candidCity, candidState, candidZipCode, candidateTitle, experienceYears, education, avail, availDetails, transportation, startDate, workAuthorization, emergencyContact, emergencyPhone, refer, prevRetailExp, lang, cert);

            displayCategoryScores(skillScore, experienceScore, availabilityScore, educationScore, distanceScore, distanceKm, distanceDescription);

            displayFeedback(feedback);

            displayGPTRecommendations(recommendations);

            if (matchedKeywords != null && matchedKeywords.length > 0) {
                settingkeywordchips(matchedCont, matchedKeywords, true);
            } else {
                TextView noMatchText = new TextView(this);
                noMatchText.setText("AI Analysis Complete");
                noMatchText.setTextColor(Color.parseColor("#4CAF50"));
                noMatchText.setTextSize(14);
                noMatchText.setTypeface(null, android.graphics.Typeface.BOLD);
                matchedCont.addView(noMatchText);
            }

            if (missingKeywords != null && missingKeywords.length > 0) {
                settingkeywordchips(missingCont, missingKeywords, false);
            } else {
                TextView allFoundText = new TextView(this);
                allFoundText.setText("Detailed analysis provided by AI");
                allFoundText.setTextColor(Color.parseColor("#1976D2"));
                allFoundText.setTextSize(14);
                missingCont.addView(allFoundText);
            }
        }
    }

    private void displayCandidInfo(String name, String email, String phone, String address, String city, String state, String zipCode, String title, int experience, String education,
                                   String availability, String availabilityDetails, String transportation, String startDate, String workAuthorization, String emergencyContact, String emergencyPhone, String references, String previousRetailExperience, String languages, String certifications) {
        LinearLayout candidateContainer = new LinearLayout(this);
        candidateContainer.setOrientation(LinearLayout.VERTICAL);
        candidateContainer.setPadding(16, 16, 16, 16);
        candidateContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));

        TextView titleView = new TextView(this);
        titleView.setText("Candidate Information");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#1976D2"));
        titleView.setPadding(0, 0, 0, 8);
        candidateContainer.addView(titleView);

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

    private void displayCategoryScores(int skillScore, int experienceScore, int availabilityScore, int educationScore, int distanceScore, double distanceKm, String distanceDescription) {
        LinearLayout categoryContainer = new LinearLayout(this);
        categoryContainer.setOrientation(LinearLayout.VERTICAL);
        categoryContainer.setPadding(16, 16, 16, 16);
        categoryContainer.setBackgroundColor(Color.parseColor("#E8F5E8"));

        TextView titleView = new TextView(this);
        titleView.setText("Category Scores");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#2E7D32"));
        titleView.setPadding(0, 0, 0, 8);
        categoryContainer.addView(titleView);

        addCategoryScore(categoryContainer, "Skills Match", skillScore);
        addCategoryScore(categoryContainer, "Experience", experienceScore);
        addCategoryScore(categoryContainer, "Availability", availabilityScore);
        addCategoryScore(categoryContainer, "Education", educationScore);
        addCategoryScore(categoryContainer, "Distance", distanceScore);

        if (distanceKm > 0) {
            LinearLayout distanceRow = new LinearLayout(this);
            distanceRow.setOrientation(LinearLayout.HORIZONTAL);
            distanceRow.setPadding(0, 8, 0, 8);

            TextView distanceLabel = new TextView(this);
            distanceLabel.setText("Distance: ");
            distanceLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            distanceLabel.setTextSize(14);
            distanceLabel.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            ));

            TextView distanceValue = new TextView(this);
            distanceValue.setText(String.format("%.1f km", distanceKm));
            distanceValue.setTextSize(14);
            distanceValue.setTextColor(Color.parseColor("#1976D2"));
            distanceValue.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            distanceRow.addView(distanceLabel);
            distanceRow.addView(distanceValue);
            categoryContainer.addView(distanceRow);

            if (distanceDescription != null && !distanceDescription.isEmpty()) {
                TextView distanceDesc = new TextView(this);
                distanceDesc.setText(distanceDescription);
                distanceDesc.setTextSize(12);
                distanceDesc.setTextColor(Color.parseColor("#666666"));
                distanceDesc.setPadding(0, 4, 0, 0);
                categoryContainer.addView(distanceDesc);
            }
        }

        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(categoryContainer, 2);
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

    private void displayFeedback(String feedback) {
        if (feedback == null || feedback.isEmpty()) return;

        LinearLayout feedbackContainer = new LinearLayout(this);
        feedbackContainer.setOrientation(LinearLayout.VERTICAL);
        feedbackContainer.setPadding(16, 16, 16, 16);
        feedbackContainer.setBackgroundColor(Color.parseColor("#E0F2F7"));

        TextView titleView = new TextView(this);
        titleView.setText("AI Feedback");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#1976D2"));
        titleView.setPadding(0, 0, 0, 8);
        feedbackContainer.addView(titleView);

        TextView feedbackText = new TextView(this);
        feedbackText.setText(feedback);
        feedbackText.setTextSize(14);
        feedbackText.setTextColor(Color.parseColor("#333333"));
        feedbackText.setPadding(0, 0, 0, 8);
        feedbackContainer.addView(feedbackText);

        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(feedbackContainer, 3);
        }
    }

    private void displayGPTRecommendations(String recommendations) {
        if (recommendations == null || recommendations.isEmpty()) return;

        LinearLayout recommendationsContainer = new LinearLayout(this);
        recommendationsContainer.setOrientation(LinearLayout.VERTICAL);
        recommendationsContainer.setPadding(16, 16, 16, 16);
        recommendationsContainer.setBackgroundColor(Color.parseColor("#FFF3E0"));

        TextView titleView = new TextView(this);
        titleView.setText("AI Recommendations");
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#E65100"));
        titleView.setPadding(0, 0, 0, 8);
        recommendationsContainer.addView(titleView);

        TextView recommendationsText = new TextView(this);
        recommendationsText.setText(recommendations);
        recommendationsText.setTextSize(14);
        recommendationsText.setTextColor(Color.parseColor("#333333"));
        recommendationsText.setPadding(0, 0, 0, 8);
        recommendationsContainer.addView(recommendationsText);

        ViewParent parent = findViewById(R.id.matchedKeywordsContainer).getParent();
        if (parent instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) parent;
            mainLayout.addView(recommendationsContainer, 4); // Add after feedback
        }
    }

    private void settingkeywordchips(LinearLayout container, String[] keywords, boolean matched) {
        container.removeAllViews();

        LinearLayout currentRow = new LinearLayout(this);
        currentRow.setOrientation(LinearLayout.HORIZONTAL);
        currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            TextView chip = new TextView(this);
            chip.setText(keyword);
            chip.setTextSize(14);
            chip.setPadding(24, 12, 24, 12);
            chip.setTextColor(matched ? Color.WHITE : Color.parseColor("#999999"));
            chip.setBackgroundResource(matched ? R.drawable.chip_matched : R.drawable.chip_missing);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            chip.setLayoutParams(params);

            currentRow.addView(chip);

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

    private void shareResumeImage(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            Log.e("MatchScore", "Photo path is empty for sharing.");
            return;
        }

        File file = new File(photoPath);
        if (!file.exists()) {
            Log.e("MatchScore", "Image file not found at: " + photoPath);
            return;
        }

        Uri contentUri = FileProvider.getUriForFile(this, "com.example.resumematch.fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Resume Match Score");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my resume match score!");

        startActivity(Intent.createChooser(shareIntent, "Share Resume Image"));
    }
}
