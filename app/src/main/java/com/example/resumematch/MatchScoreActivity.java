package com.example.resumematch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;
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
            
            Log.d("MatchScore", "Score: " + matchScore + ", Matched: " + 
                   (matchedKeywords != null ? matchedKeywords.length : 0) + 
                   ", Missing: " + (missingKeywords != null ? missingKeywords.length : 0));
            
            // Display the actual match score
            textScore.setText(matchScore + "%");
            
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
                TextView noMissingText = new TextView(this);
                noMissingText.setText("All keywords found");
                noMissingText.setTextColor(Color.parseColor("#4CAF50"));
                noMissingText.setTextSize(14);
                missingContainer.addView(noMissingText);
            }
        } else {
            // Fallback to demo data if no intent
            settingkeywordchips(matchedContainer, new String[]{"React", "TypeScript", "Node.js", "REST APIs", "Agile"}, true);
            settingkeywordchips(missingContainer, new String[]{"Docker", "Kubernetes", "AWS"}, false);
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
