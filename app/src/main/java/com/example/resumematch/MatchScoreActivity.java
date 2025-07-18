package com.example.resumematch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;

public class MatchScoreActivity extends AppCompatActivity {

    //creating the variables for button,score and containers
    ImageView backArrow;
    TextView textScore;
    FlowLayout matchedContainer, missingContainer;
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

        //added some demo keywords to get the matching score
        settingkeywordchips(matchedContainer, new String[]{"React", "TypeScript", "Node.js", "REST APIs", "Agile"}, true);
        settingkeywordchips(missingContainer, new String[]{"Docker", "Kubernetes", "AWS"}, false);
    }

    // Got this function from gpt on how to set the chips
    private void settingkeywordchips(FlowLayout container, String[] keywords, boolean matched) {
        container.removeAllViews();
        for (String keyword : keywords) {
            TextView chip = new TextView(this);
            chip.setText(keyword);
            chip.setTextSize(14);
            chip.setPadding(24, 12, 24, 12);
            chip.setTextColor(matched ? Color.WHITE : Color.parseColor("#999999"));
            chip.setBackgroundResource(matched ? R.drawable.chip_matched : R.drawable.chip_missing);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((ViewGroup.MarginLayoutParams) params).setMargins(8, 8, 8, 8);
            chip.setLayoutParams(params);

            container.addView(chip);
        }
    }
}
