package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;

public class ResumeDetailsActivity extends AppCompatActivity {

    private TextView txt_resumeId, txt_resumeDate, txt_jobtitle, txt_matchscore;
    private TextView txt_resumeCont, txt_matchedkeywords, txt_missingkeywords;
    private Button btnBack, btnContact, btnSchedule;
    private ImageView backBtn;
    private DataRepository dataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_details);

        dataRepo = new DataRepository(this);

        String resumeId = getIntent().getStringExtra("resumeId");
        String jobtitle = getIntent().getStringExtra("jobtitle");
        String resumedate = getIntent().getStringExtra("resumedate");
        String matchscore = getIntent().getStringExtra("matchscore");
        String resumeCont = getIntent().getStringExtra("resumeCont");
        String[] matchedkeywords = getIntent().getStringArrayExtra("matchedkeywords");
        String[] missingkeywords = getIntent().getStringArrayExtra("missingkeywords");

        backBtn = findViewById(R.id.backButton);
        txt_resumeId = findViewById(R.id.textResumeId);
        txt_resumeDate = findViewById(R.id.textResumeDate);
        txt_jobtitle = findViewById(R.id.textJobTitle);
        txt_matchscore = findViewById(R.id.textMatchScore);
        txt_resumeCont = findViewById(R.id.textResumeContent);
        txt_matchedkeywords = findViewById(R.id.textMatchedKeywords);
        txt_missingkeywords = findViewById(R.id.textMissingKeywords);
        btnBack = findViewById(R.id.buttonBack);
        btnContact = findViewById(R.id.buttonContact);
        btnSchedule = findViewById(R.id.buttonSchedule);

        txt_resumeId.setText("Resume ID: " + resumeId);
        txt_resumeDate.setText("Date: " + resumedate);
        txt_jobtitle.setText("Applied for: " + jobtitle);
        txt_matchscore.setText("Match Score: " + matchscore);
        txt_resumeCont.setText("Resume Content:\n\n" + resumeCont);

        if (matchedkeywords != null && matchedkeywords.length > 0) {
            StringBuilder matched = new StringBuilder("Matched Keywords:\n");
            for (String keyword : matchedkeywords) {
                matched.append("• ").append(keyword).append("\n");
            }
            txt_matchedkeywords.setText(matched.toString());
        } else {
            txt_matchedkeywords.setText("No keywords matched");
        }

        if (missingkeywords != null && missingkeywords.length > 0) {
            StringBuilder missing = new StringBuilder("Missing Keywords:\n");
            for (String keyword : missingkeywords) {
                missing.append("• ").append(keyword).append("\n");
            }
            txt_missingkeywords.setText(missing.toString());
        } else {
            txt_missingkeywords.setText("All keywords found!");
        }

        backBtn.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
        
        btnContact.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Contact feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        btnSchedule.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Interview scheduling coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
} 