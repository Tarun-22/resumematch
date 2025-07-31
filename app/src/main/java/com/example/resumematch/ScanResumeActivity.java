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
        progressDialog.setMessage("Analyzing resume with AI...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // First check if store profile exists
        dataRepository.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
            @Override
            public void onResult(StoreProfile storeProfile) {
                if (storeProfile == null) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        // Show dialog to redirect to store profile setup
                        showStoreProfileRequiredDialog();
                    });
                    return;
                }

                // Set a timeout for GPT API call
                android.os.Handler timeoutHandler = new android.os.Handler();
                Runnable timeoutRunnable = () -> {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.w("ScanResume", "GPT API timeout, using fallback");
                        Toast.makeText(ScanResumeActivity.this, "AI analysis timed out, using fallback method", Toast.LENGTH_LONG).show();
                        processWithFallback(resumeText, storeProfile);
                    });
                };

                // Set 30 second timeout
                timeoutHandler.postDelayed(timeoutRunnable, 30000);

                // Use GPT API for analysis
                GPTApiService.analyzeResume(resumeText, jobDescription, storeProfile.getFormattedAddress(),
                    new GPTApiService.GPTCallback() {
                        @Override
                        public void onSuccess(GPTApiService.GPTResponse gptResponse) {
                            // Cancel timeout
                            timeoutHandler.removeCallbacks(timeoutRunnable);

                            runOnUiThread(() -> {
                                Log.d("ScanResume", "GPT analysis successful");
                                processGPTResponse(gptResponse, resumeText, progressDialog);
                            });
                        }

                        @Override
                        public void onError(String error) {
                            // Cancel timeout
                            timeoutHandler.removeCallbacks(timeoutRunnable);

                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Log.e("ScanResume", "GPT API error: " + error);

                                // Show detailed error dialog
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ScanResumeActivity.this);
                                builder.setTitle("AI Analysis Failed")
                                        .setMessage("The AI analysis encountered an error:\n\n" + error + "\n\nWould you like to try the fallback method?")
                                        .setPositiveButton("Use Fallback", (dialog, which) -> {
                                            processWithFallback(resumeText, storeProfile);
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {
                                            Toast.makeText(ScanResumeActivity.this, "Analysis cancelled", Toast.LENGTH_SHORT).show();
                                        })
                                        .setCancelable(false)
                                        .show();
                            });
                        }
                    });
            }
        });
    }

    private void processGPTResponse(GPTApiService.GPTResponse gptResponse, String resumeText, android.app.ProgressDialog progressDialog) {
        try {
            // Create and save resume to database
            String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            // Get store profile for distance calculation
            dataRepository.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
                @Override
                public void onResult(StoreProfile storeProfile) {
                    // Calculate distance score (25 points max)
                    int distanceScore = 0;
                    double distanceMiles = 0;
                    String distanceDescription = "Distance not available";
                    
                    if (storeProfile != null && !gptResponse.getAddress().isEmpty()) {
                        // Calculate distance using Google Maps
                        GoogleMapsDistanceCalculator.calculateDistance(ScanResumeActivity.this, 
                            gptResponse.getAddress(), storeProfile.getFormattedAddress(),
                            new GoogleMapsDistanceCalculator.DistanceCallback() {
                                @Override
                                public void onDistanceCalculated(double distance, String duration, String distanceText) {
                                    // Calculate distance score out of 25 points
                                    int calculatedDistanceScore = GoogleMapsDistanceCalculator.calculateDistanceScore(distance);
                                    String calculatedDistanceDescription = GoogleMapsDistanceCalculator.getDistanceDescription(distance);
                                    
                                    // Calculate final overall score: GPT (75) + Distance (25) = 100
                                    int finalOverallScore = gptResponse.getOverallScore() + calculatedDistanceScore;
                                    
                                    Log.d("ScanResume", "GPT Score: " + gptResponse.getOverallScore() + 
                                          ", Distance Score: " + calculatedDistanceScore + 
                                          ", Final Score: " + finalOverallScore);
                                    
                                    // Save resume with final score
                                    saveResumeWithScore(resumeId, currentDate, finalOverallScore, resumeText, 
                                        gptResponse, calculatedDistanceScore, distance, calculatedDistanceDescription);
                                }
                                
                                @Override
                                public void onError(String error) {
                                    Log.e("ScanResume", "Distance calculation error: " + error);
                                    // Use default distance score
                                    int defaultDistanceScore = 12; // Default 12/25 points
                                    int finalOverallScore = gptResponse.getOverallScore() + defaultDistanceScore;
                                    
                                    Log.d("ScanResume", "Using default distance score. GPT: " + gptResponse.getOverallScore() + 
                                          ", Distance: " + defaultDistanceScore + ", Final: " + finalOverallScore);
                                    
                                    saveResumeWithScore(resumeId, currentDate, finalOverallScore, resumeText, 
                                        gptResponse, defaultDistanceScore, 15.0, "Default distance (15 miles)");
                                }
                            });
                    } else {
                        // No store profile or address, use default distance score
                        int defaultDistanceScore = 12; // Default 12/25 points
                        int finalOverallScore = gptResponse.getOverallScore() + defaultDistanceScore;
                        
                        Log.d("ScanResume", "No store profile. GPT: " + gptResponse.getOverallScore() + 
                              ", Distance: " + defaultDistanceScore + ", Final: " + finalOverallScore);
                        
                        saveResumeWithScore(resumeId, currentDate, finalOverallScore, resumeText, 
                            gptResponse, defaultDistanceScore, 15.0, "No store profile available");
                    }
                }
            });

        } catch (Exception e) {
            Log.e("ScanResume", "Error processing GPT response: " + e.getMessage());
            progressDialog.dismiss();
            Toast.makeText(this, "Error processing AI analysis: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void saveResumeWithScore(String resumeId, String currentDate, int finalOverallScore, String resumeText, 
                                   GPTApiService.GPTResponse gptResponse, int distanceScore, double distanceMiles, String distanceDescription) {
        String matchScore = finalOverallScore + "%";

        // Save resume photo
        String photoPath = saveResumePhoto(selectedImageUri, resumeId);

        // Convert GPT response to JSON for storage
        String extractedDataJson = convertGPTResponseToJson(gptResponse);

        ResumeEntity newResume = new ResumeEntity(
            resumeId,
            jobId,
            jobTitle,
            currentDate,
            matchScore,
            resumeText,
            System.currentTimeMillis()
        );
        newResume.setPhotoPath(photoPath);
        newResume.setExtractedDataJson(extractedDataJson);

        // Save to database
        dataRepository.insertResume(newResume, new DataRepository.DatabaseCallback<Void>() {
            @Override
            public void onResult(Void result) {
                runOnUiThread(() -> {
                    // Update job resume count in database
                    updateJobResumeCount();

                    Log.d("ScanResume", "Resume saved to database: " + resumeId + " with score: " + matchScore);

                    // Navigate to match score screen with GPT data
                    Intent intent = new Intent(ScanResumeActivity.this, MatchScoreActivity.class);
                    intent.putExtra("resumeId", resumeId);
                    intent.putExtra("matchScore", finalOverallScore);
                    intent.putExtra("resumeText", resumeText);

                    // Add GPT extracted data
                    intent.putExtra("candidateName", gptResponse.getCandidateName());
                    intent.putExtra("candidateEmail", gptResponse.getEmail());
                    intent.putExtra("candidatePhone", gptResponse.getPhone());
                    intent.putExtra("candidateAddress", gptResponse.getAddress());
                    intent.putExtra("candidateCity", gptResponse.getCity());
                    intent.putExtra("candidateState", gptResponse.getState());
                    intent.putExtra("candidateZipCode", gptResponse.getZipCode());
                    intent.putExtra("candidateTitle", gptResponse.getCurrentTitle());
                    intent.putExtra("experienceYears", gptResponse.getExperienceYears());
                    intent.putExtra("education", gptResponse.getEducation());
                    intent.putExtra("availability", gptResponse.getAvailability());
                    intent.putExtra("availabilityDetails", gptResponse.getAvailabilityDetails());
                    intent.putExtra("transportation", gptResponse.getTransportation());
                    intent.putExtra("startDate", gptResponse.getStartDate());
                    intent.putExtra("workAuthorization", gptResponse.getWorkAuthorization());
                    intent.putExtra("emergencyContact", gptResponse.getEmergencyContact());
                    intent.putExtra("emergencyPhone", gptResponse.getEmergencyPhone());
                    intent.putExtra("references", gptResponse.getReferences());
                    intent.putExtra("previousRetailExperience", gptResponse.getPreviousRetailExperience());
                    intent.putExtra("languages", gptResponse.getLanguages());
                    intent.putExtra("certifications", gptResponse.getCertifications());

                    // Add category scores from GPT (out of 75 total)
                    intent.putExtra("skillScore", gptResponse.getSkillScore());
                    intent.putExtra("experienceScore", gptResponse.getExperienceScore());
                    intent.putExtra("availabilityScore", gptResponse.getAvailabilityScore());
                    intent.putExtra("educationScore", gptResponse.getEducationScore());
                    
                    // Add distance score (out of 25 total)
                    intent.putExtra("distanceScore", distanceScore);
                    intent.putExtra("distanceMiles", distanceMiles);
                    intent.putExtra("distanceDescription", distanceDescription);

                    // Add feedback and recommendations from GPT
                    intent.putExtra("feedback", gptResponse.getFeedback());
                    intent.putExtra("recommendations", gptResponse.getRecommendations());

                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private void processWithFallback(String resumeText, StoreProfile storeProfile) {
        // Fallback to old method if GPT fails
        ResumeDataExtractor.extractDataWithMLKit(this, resumeText, extractedData -> {
            try {
                // Calculate enhanced match score with store profile
                EnhancedScoringSystem.ScoringResult scoringResult = EnhancedScoringSystem.calculateEnhancedScore(jobDescription, extractedData, storeProfile);

                // Create and save resume to database
                String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                String matchScore = scoringResult.getOverallScore() + "%";

                // Save resume photo
                String photoPath = saveResumePhoto(selectedImageUri, resumeId);

                // Convert extracted data to JSON for manual editing
                String extractedDataJson = convertExtractedDataToJson(extractedData);

                ResumeEntity newResume = new ResumeEntity(
                    resumeId,
                    jobId,
                    jobTitle,
                    currentDate,
                    matchScore,
                    resumeText,
                    System.currentTimeMillis()
                );
                newResume.setPhotoPath(photoPath);
                newResume.setExtractedDataJson(extractedDataJson);

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
                            intent.putExtra("distanceScore", scoringResult.getDistanceScore());
                            intent.putExtra("distanceMiles", scoringResult.getDistanceMiles());
                            intent.putExtra("distanceDescription", scoringResult.getDistanceDescription());

                            // Add recommendations
                            intent.putExtra("recommendations", scoringResult.getRecommendations().toArray(new String[0]));

                            startActivity(intent);
                            finish();
                        });
                    }
                });

            } catch (Exception e) {
                Log.e("ScanResume", "Error in fallback processing: " + e.getMessage());
                Toast.makeText(ScanResumeActivity.this, "Error processing resume: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showStoreProfileRequiredDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Store Profile Required")
                .setMessage("You need to set up your store profile first to calculate distance and provide accurate scoring.")
                .setPositiveButton("Setup Store Profile", (dialog, which) -> {
                    Intent intent = new Intent(ScanResumeActivity.this, StoreProfileActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private String saveResumePhoto(Uri imageUri, String resumeId) {
        try {
            // Create a unique filename for the resume photo
            String fileName = "resume_" + resumeId + ".jpg";
            
            // Get the app's internal storage directory
            java.io.File storageDir = new java.io.File(getFilesDir(), "resume_photos");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            
            java.io.File photoFile = new java.io.File(storageDir, fileName);
            
            // Copy the image to internal storage
            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(photoFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            inputStream.close();
            outputStream.close();
            
            Log.d("ScanResume", "Resume photo saved: " + photoFile.getAbsolutePath());
            return photoFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e("ScanResume", "Error saving resume photo: " + e.getMessage());
            return "";
        }
    }

    private String convertExtractedDataToJson(ResumeDataExtractor.ExtractedData data) {
        try {
            // Create a simple JSON representation of the extracted data
            // In a real app, you might use a JSON library like Gson
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"name\":\"").append(data.getName()).append("\",");
            json.append("\"email\":\"").append(data.getEmail()).append("\",");
            json.append("\"phone\":\"").append(data.getPhone()).append("\",");
            json.append("\"address\":\"").append(data.getAddress()).append("\",");
            json.append("\"city\":\"").append(data.getCity()).append("\",");
            json.append("\"state\":\"").append(data.getState()).append("\",");
            json.append("\"zipCode\":\"").append(data.getZipCode()).append("\",");
            json.append("\"currentTitle\":\"").append(data.getCurrentTitle()).append("\",");
            json.append("\"experienceYears\":").append(data.getExperienceYears()).append(",");
            json.append("\"education\":\"").append(data.getEducation()).append("\",");
            json.append("\"availability\":\"").append(data.getAvailability()).append("\",");
            json.append("\"availabilityDetails\":\"").append(data.getAvailabilityDetails()).append("\",");
            json.append("\"transportation\":\"").append(data.getTransportation()).append("\",");
            json.append("\"expectedSalary\":\"").append(data.getExpectedSalary()).append("\",");
            json.append("\"startDate\":\"").append(data.getStartDate()).append("\",");
            json.append("\"workAuthorization\":\"").append(data.getWorkAuthorization()).append("\",");
            json.append("\"emergencyContact\":\"").append(data.getEmergencyContact()).append("\",");
            json.append("\"emergencyPhone\":\"").append(data.getEmergencyPhone()).append("\",");
            json.append("\"references\":\"").append(data.getReferences()).append("\",");
            json.append("\"previousRetailExperience\":\"").append(data.getPreviousRetailExperience()).append("\",");
            json.append("\"languages\":\"").append(data.getLanguages()).append("\",");
            json.append("\"certifications\":\"").append(data.getCertifications()).append("\"");
            json.append("}");
            
            return json.toString();
        } catch (Exception e) {
            Log.e("ScanResume", "Error converting data to JSON: " + e.getMessage());
            return "{}";
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

    private String convertGPTResponseToJson(GPTApiService.GPTResponse gptResponse) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("candidateName", gptResponse.getCandidateName());
            json.put("email", gptResponse.getEmail());
            json.put("phone", gptResponse.getPhone());
            json.put("address", gptResponse.getAddress());
            json.put("city", gptResponse.getCity());
            json.put("state", gptResponse.getState());
            json.put("zipCode", gptResponse.getZipCode());
            json.put("currentTitle", gptResponse.getCurrentTitle());
            json.put("experienceYears", gptResponse.getExperienceYears());
            json.put("education", gptResponse.getEducation());
            json.put("availability", gptResponse.getAvailability());
            json.put("availabilityDetails", gptResponse.getAvailabilityDetails());
            json.put("transportation", gptResponse.getTransportation());
            json.put("startDate", gptResponse.getStartDate());
            json.put("workAuthorization", gptResponse.getWorkAuthorization());
            json.put("emergencyContact", gptResponse.getEmergencyContact());
            json.put("emergencyPhone", gptResponse.getEmergencyPhone());
            json.put("references", gptResponse.getReferences());
            json.put("previousRetailExperience", gptResponse.getPreviousRetailExperience());
            json.put("languages", gptResponse.getLanguages());
            json.put("certifications", gptResponse.getCertifications());
            json.put("skillScore", gptResponse.getSkillScore());
            json.put("experienceScore", gptResponse.getExperienceScore());
            json.put("availabilityScore", gptResponse.getAvailabilityScore());
            json.put("educationScore", gptResponse.getEducationScore());
            json.put("overallScore", gptResponse.getOverallScore());
            json.put("feedback", gptResponse.getFeedback());
            json.put("recommendations", gptResponse.getRecommendations());
            return json.toString();
        } catch (Exception e) {
            Log.e("ScanResume", "Error converting GPT response to JSON: " + e.getMessage());
            return "{}";
        }
    }
}
