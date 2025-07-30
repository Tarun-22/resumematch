package com.example.resumematch;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScanResumeActivity extends AppCompatActivity {

    //here we are creating the varibables
    ImageView backButton;
    Button buttonCamera, buttonUpload;
    TextView textOCRPreview;
    String jobId;
    String jobTitle;
    String jobDescription;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resume);

        // Get job details from intent
        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            jobTitle = intent.getStringExtra("jobTitle");
            jobDescription = intent.getStringExtra("jobDescription");
            Log.d("ScanResume", "Job ID: " + jobId + ", Title: " + jobTitle);
        }

        // here we are declaring the variables for button, camera, and the text for preview
        backButton = findViewById(R.id.backButton);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonUpload = findViewById(R.id.buttonUpload);
        textOCRPreview = findViewById(R.id.textOCRPreview);

        backButton.setOnClickListener(v -> finish());

        //creating the launcher to pick the image
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        runOCR(selectedImageUri);
                    }
                });

        //setting the onclick listener for this button which helps to upload the image
        buttonUpload.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent2);
        });

        textOCRPreview.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        // as of now we didnt connected camera, so we just set the demo text in preview
        buttonCamera.setOnClickListener(v ->
                textOCRPreview.setText("Camera scanning coming soon..."));
    }

    //From gpt, we got this function to run the OCR by connecting ML Kit
    private void runOCR(Uri imageUri) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            //here we are setting the text preview with text "Scanning"
            textOCRPreview.setText("Scanning...");
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String resultText = visionText.getText();
                        textOCRPreview.setText(resultText.isEmpty() ? "No text found." : resultText);
                        Log.d("OCR_RESULT", resultText);
                        
                        // Process the OCR result and calculate match score
                        processOCRResult(resultText);
                    })
                    .addOnFailureListener(e -> {
                        textOCRPreview.setText("OCR failed: " + e.getMessage());
                        Toast.makeText(this, "OCR failed", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            //if anything wrong happens, we are displaying the error message
            textOCRPreview.setText("Error loading image: " + e.getMessage());
        }
    }

    private void processOCRResult(String resumeText) {
        if (resumeText.isEmpty()) {
            Toast.makeText(this, "No text found in the image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Calculate match score
            MatchResult matchResult = calculateMatchScore(jobDescription, resumeText);
            
            // Create and save resume
            String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String currentDate = java.time.LocalDate.now().toString();
            String matchScore = matchResult.getMatchScore() + "% Match";
            
            Resume newResume = new Resume(resumeId, currentDate, matchScore, jobId);
            JobStorage.addResume(newResume);
            
            // Update job resume count
            updateJobResumeCount();
            
            Log.d("ScanResume", "Resume saved successfully: " + resumeId + " with score: " + matchScore);
            
            // Navigate to match score screen
            Intent intent = new Intent(ScanResumeActivity.this, MatchScoreActivity.class);
            intent.putExtra("resumeId", resumeId);
            intent.putExtra("matchScore", matchResult.getMatchScore());
            intent.putExtra("matchedKeywords", matchResult.getMatchedKeywords().toArray(new String[0]));
            intent.putExtra("missingKeywords", matchResult.getMissingKeywords().toArray(new String[0]));
            intent.putExtra("resumeText", resumeText);
            startActivity(intent);
            finish();
            
        } catch (Exception e) {
            Log.e("ScanResume", "Error processing OCR result: " + e.getMessage());
            Toast.makeText(this, "Error processing resume: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private MatchResult calculateMatchScore(String jobDescription, String resumeText) {
        // Extract keywords from job description
        List<String> jobKeywords = extractKeywords(jobDescription.toLowerCase());
        List<String> resumeKeywords = extractKeywords(resumeText.toLowerCase());
        
        Log.d("KeywordMatching", "Job keywords: " + jobKeywords);
        Log.d("KeywordMatching", "Resume keywords: " + resumeKeywords);
        
        // Find matched and missing keywords
        List<String> matchedKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();
        
        for (String keyword : jobKeywords) {
            if (resumeKeywords.contains(keyword)) {
                matchedKeywords.add(keyword);
            } else {
                missingKeywords.add(keyword);
            }
        }
        
        Log.d("KeywordMatching", "Matched keywords: " + matchedKeywords);
        Log.d("KeywordMatching", "Missing keywords: " + missingKeywords);
        
        // Calculate match percentage
        double matchPercentage = jobKeywords.isEmpty() ? 0 : 
            (double) matchedKeywords.size() / jobKeywords.size() * 100;
        
        Log.d("KeywordMatching", "Match percentage: " + matchPercentage + "%");
        
        return new MatchResult((int) matchPercentage, matchedKeywords, missingKeywords);
    }

    private List<String> extractKeywords(String text) {
        // Common job-related keywords to look for
        List<String> commonKeywords = Arrays.asList(
            "java", "python", "javascript", "react", "angular", "vue", "node.js", "spring",
            "android", "ios", "swift", "kotlin", "sql", "mongodb", "mysql", "postgresql",
            "aws", "azure", "docker", "kubernetes", "jenkins", "git", "agile", "scrum",
            "rest", "api", "microservices", "machine learning", "ai", "data science",
            "frontend", "backend", "full stack", "devops", "ui", "ux", "design",
            "project management", "leadership", "communication", "team", "collaboration"
        );
        
        List<String> foundKeywords = new ArrayList<>();
        for (String keyword : commonKeywords) {
            if (text.contains(keyword)) {
                foundKeywords.add(keyword);
            }
        }
        
        Log.d("KeywordExtraction", "Text: " + text.substring(0, Math.min(100, text.length())) + "...");
        Log.d("KeywordExtraction", "Found keywords: " + foundKeywords);
        
        return foundKeywords;
    }

    private void updateJobResumeCount() {
        if (jobId != null) {
            List<Resume> jobResumes = JobStorage.getResumesForJob(jobId);
            // Find the job and update its resume count
            for (JobPost job : JobStorage.jobList) {
                if (job.getId().equals(jobId)) {
                    job.setResumeCount(jobResumes.size());
                    break;
                }
            }
        }
    }

    // MatchResult class to hold match data
    public static class MatchResult {
        private int matchScore;
        private List<String> matchedKeywords;
        private List<String> missingKeywords;

        public MatchResult(int matchScore, List<String> matchedKeywords, List<String> missingKeywords) {
            this.matchScore = matchScore;
            this.matchedKeywords = matchedKeywords;
            this.missingKeywords = missingKeywords;
        }

        public int getMatchScore() { return matchScore; }
        public List<String> getMatchedKeywords() { return matchedKeywords; }
        public List<String> getMissingKeywords() { return missingKeywords; }
    }
}
