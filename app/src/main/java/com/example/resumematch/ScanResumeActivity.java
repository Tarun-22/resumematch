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
    DataRepository dataRepository;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    ActivityResultLauncher<Intent> cameraLauncher;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resume);

        // Initialize DataRepository
        dataRepository = new DataRepository(this);

        // Get job details from intent
        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            jobTitle = intent.getStringExtra("jobTitle");
            jobDescription = intent.getStringExtra("jobDescription");
            Log.d("ScanResume", "Job ID: " + jobId + ", Title: " + jobTitle);
        }

        // Check if job details are provided
        if (jobId == null || jobTitle == null || jobDescription == null) {
            Toast.makeText(this, "No job selected. Please select a job first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // here we are declaring the variables for button, camera, and the text for preview
        backButton = findViewById(R.id.backButton);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonUpload = findViewById(R.id.buttonUpload);
        textOCRPreview = findViewById(R.id.textOCRPreview);
        TextView textJobTitle = findViewById(R.id.textJobTitle);

        // Display the selected job title
        textJobTitle.setText("For: " + jobTitle);

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

        //creating the launcher for camera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getParcelableExtra("data");
                        if (selectedImageUri != null) {
                            runOCR(selectedImageUri);
                        }
                    }
                });

        //setting the onclick listener for this button which helps to upload the image
        buttonUpload.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent2);
        });

        textOCRPreview.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        // Set up camera functionality
        buttonCamera.setOnClickListener(v -> {
            // Check for camera permission
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
            } else {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required to scan resumes", Toast.LENGTH_SHORT).show();
            }
        }
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

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String resumeText = visionText.getText();
                        Log.d("OCR", "Extracted text: " + resumeText.substring(0, Math.min(100, resumeText.length())) + "...");
                        processOCRResult(resumeText);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OCR", "Error processing image: " + e.getMessage());
                        Toast.makeText(ScanResumeActivity.this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("OCR", "Error loading image: " + e.getMessage());
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void processOCRResult(String resumeText) {
        if (resumeText.isEmpty()) {
            Toast.makeText(this, "No text found in the image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Extracting candidate data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ResumeDataExtractor.extractDataWithMLKit(this, resumeText, extractedData -> {
            try {
                // Calculate enhanced match score
                EnhancedScoringSystem.ScoringResult scoringResult = EnhancedScoringSystem.calculateEnhancedScore(jobDescription, extractedData);

                // Create and save resume to database
                String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                String matchScore = scoringResult.getOverallScore() + "%";

                ResumeEntity newResume = new ResumeEntity(
                    resumeId,
                    jobId,
                    jobTitle,
                    currentDate,
                    matchScore,
                    resumeText,
                    System.currentTimeMillis()
                );

                // Save to database
                dataRepository.insertResume(newResume, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        runOnUiThread(() -> {
                            // Update job resume count in database
                            updateJobResumeCount();

                            Log.d("ScanResume", "Resume saved to database: " + resumeId + " with score: " + matchScore);

                            // Navigate to match score screen with enhanced data
                            Intent intent = new Intent(ScanResumeActivity.this, MatchScoreActivity.class);
                            intent.putExtra("resumeId", resumeId);
                            intent.putExtra("matchScore", scoringResult.getOverallScore());
                            intent.putExtra("matchedKeywords", scoringResult.getMatchedSkills().toArray(new String[0]));
                            intent.putExtra("missingKeywords", scoringResult.getMissingSkills().toArray(new String[0]));
                            intent.putExtra("resumeText", resumeText);

                            // Add extracted data
                            intent.putExtra("candidateName", extractedData.getName());
                            intent.putExtra("candidateEmail", extractedData.getEmail());
                            intent.putExtra("candidatePhone", extractedData.getPhone());
                            intent.putExtra("candidateAddress", extractedData.getAddress());
                            intent.putExtra("candidateCity", extractedData.getCity());
                            intent.putExtra("candidateState", extractedData.getState());
                            intent.putExtra("candidateZipCode", extractedData.getZipCode());
                            intent.putExtra("candidateTitle", extractedData.getCurrentTitle());
                            intent.putExtra("experienceYears", extractedData.getExperienceYears());
                            intent.putExtra("education", extractedData.getEducation());
                            intent.putExtra("availability", extractedData.getAvailability());
                            intent.putExtra("availabilityDetails", extractedData.getAvailabilityDetails());
                            intent.putExtra("transportation", extractedData.getTransportation());
                            intent.putExtra("expectedSalary", extractedData.getExpectedSalary());
                            intent.putExtra("startDate", extractedData.getStartDate());
                            intent.putExtra("workAuthorization", extractedData.getWorkAuthorization());
                            intent.putExtra("emergencyContact", extractedData.getEmergencyContact());
                            intent.putExtra("emergencyPhone", extractedData.getEmergencyPhone());
                            intent.putExtra("references", extractedData.getReferences());
                            intent.putExtra("previousRetailExperience", extractedData.getPreviousRetailExperience());
                            intent.putExtra("languages", extractedData.getLanguages());
                            intent.putExtra("certifications", extractedData.getCertifications());

                            // Add category scores
                            intent.putExtra("skillScore", scoringResult.getSkillMatchScore());
                            intent.putExtra("experienceScore", scoringResult.getExperienceScore());
                            intent.putExtra("availabilityScore", scoringResult.getAvailabilityScore());
                            intent.putExtra("educationScore", scoringResult.getEducationScore());

                            // Add recommendations
                            intent.putExtra("recommendations", scoringResult.getRecommendations().toArray(new String[0]));

                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        });
                    }
                });
            } catch (Exception e) {
                Log.e("ScanResume", "Error processing OCR result: " + e.getMessage());
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error processing resume: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
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
            // Get current resume count for this job
            dataRepository.getResumeCountForJob(jobId, new DataRepository.DatabaseCallback<Integer>() {
                @Override
                public void onResult(Integer resumeCount) {
                    // Update the job's resume count in database
                    dataRepository.getJobById(jobId, new DataRepository.DatabaseCallback<JobEntity>() {
                        @Override
                        public void onResult(JobEntity jobEntity) {
                            if (jobEntity != null) {
                                jobEntity.setResumeCount(resumeCount);
                                dataRepository.updateJob(jobEntity, null);
                            }
                        }
                    });
                }
            });
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
}
