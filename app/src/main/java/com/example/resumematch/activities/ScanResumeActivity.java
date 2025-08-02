package com.example.resumematch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.resumematch.R;
import com.example.resumematch.api.GPTApiService;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.models.StoreProfile;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.utils.GoogleMapsDistanceCalculator;
import com.example.resumematch.utils.ResumeDataExtractor;
import com.example.resumematch.utils.EnhancedScoringSystem;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScanResumeActivity extends AppCompatActivity {

    ImageView backbtn;
    Button btncamera, btnupload;
    TextView txt_OCRprev;
    String jobId;
    String jobtitle;
    String jobDescription;
    DataRepository dataRepo;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    ActivityResultLauncher<Intent> cameraLauncher;

    Uri selectedImageUri;
    java.io.File currentPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resume);

        dataRepo = new DataRepository(this);

        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            jobtitle = intent.getStringExtra("jobTitle");
            jobDescription = intent.getStringExtra("jobDescription");
            Log.d("ScanResume", "Job ID: " + jobId + ", Title: " + jobtitle);
        }

        if (jobId == null || jobtitle == null || jobDescription == null) {
            Toast.makeText(this, "No job selected. Please select a job first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backbtn = findViewById(R.id.backButton);
        btncamera = findViewById(R.id.buttonCamera);
        btnupload = findViewById(R.id.buttonUpload);
        txt_OCRprev = findViewById(R.id.textOCRPreview);
        TextView textJobTitle = findViewById(R.id.textJobTitle);

        textJobTitle.setText("For: " + jobtitle);

        backbtn.setOnClickListener(v -> finish());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        runOCR(selectedImageUri);
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {

                        processCameraResult();
                    } else {
                        Toast.makeText(this, "Camera capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        btnupload.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent2);
        });

        txt_OCRprev.setMovementMethod(new android.text.method.ScrollingMovementMethod());


        btncamera.setOnClickListener(v -> {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 100);
            } else {
                openCamera();
            }
        });
    }

    private void openCamera() {
        try {
            currentPhotoFile = createImageFile();
            if (currentPhotoFile == null) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.resumematch.fileprovider", currentPhotoFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 0); // Back camera
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0);
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false);
            cameraIntent.putExtra("android.intent.extras.CAPTURE_STABLE_IMAGE", true);
            
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ScanResume", "Error opening camera: " + e.getMessage());
            Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show();
        }
    }

    private java.io.File createImageFile() {
        try {

            String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            java.io.File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
            java.io.File image = java.io.File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;
        } catch (Exception e) {
            Log.e("ScanResume", "Error creating image file: " + e.getMessage());
            return null;
        }
    }

    private void processCameraResult() {
        try {
            if (currentPhotoFile != null && currentPhotoFile.exists()) {
                // Convert the file to URI
                selectedImageUri = Uri.fromFile(currentPhotoFile);
                

                Log.d("ScanResume", "Photo file size: " + currentPhotoFile.length() + " bytes");
                Log.d("ScanResume", "Photo file path: " + currentPhotoFile.getAbsolutePath());
                
                runOCR(selectedImageUri);
            } else {
                Toast.makeText(this, "Error: Photo file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ScanResume", "Error processing camera result: " + e.getMessage());
            Toast.makeText(this, "Error processing camera image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && 
                grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera and storage permissions required to scan resumes", Toast.LENGTH_SHORT).show();
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

            Log.d("OCR", "Image dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String resumeText = visionText.getText();
                        Log.d("OCR", "Extracted text length: " + resumeText.length());
                        Log.d("OCR", "Extracted text: " + resumeText.substring(0, Math.min(200, resumeText.length())) + "...");
                        
                        if (resumeText.length() < 50) {
                            Toast.makeText(ScanResumeActivity.this,
                                "Warning: Very little text detected. Please ensure the resume is clearly visible and well-lit.", 
                                Toast.LENGTH_LONG).show();
                        }
                        
                        processOCRResult(resumeText);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OCR", "Error processing image: " + e.getMessage());
                        Toast.makeText(ScanResumeActivity.this, 
                            "Error processing image. Please try again with better lighting and focus.", 
                            Toast.LENGTH_LONG).show();
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

        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Analyzing resume with AI...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dataRepo.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
            @Override
            public void onResult(StoreProfile storeProfile) {
                if (storeProfile == null) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        showStoreProfileRequiredDialog();
                    });
                    return;
                }

                android.os.Handler timeoutHandler = new android.os.Handler();
                Runnable timeoutRunnable = () -> {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.w("ScanResume", "GPT API timeout, using fallback");
                        Toast.makeText(ScanResumeActivity.this, "AI analysis timed out, using fallback method", Toast.LENGTH_LONG).show();
                        processWithFallback(resumeText, storeProfile);
                    });
                };

                timeoutHandler.postDelayed(timeoutRunnable, 30000);

                // GPT API for analysis
                GPTApiService.analyzeResume(resumeText, jobDescription, storeProfile.getFullAddress(),
                    new GPTApiService.GPTCallback() {
                        @Override
                        public void onSuccess(GPTApiService.GPTResponse gptResponse) {
                            timeoutHandler.removeCallbacks(timeoutRunnable);

                            runOnUiThread(() -> {
                                Log.d("ScanResume", "GPT analysis successful");
                                processGPTResponse(gptResponse, resumeText, progressDialog);
                            });
                        }

                        @Override
                        public void onError(String error) {
                            timeoutHandler.removeCallbacks(timeoutRunnable);

                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Log.e("ScanResume", "GPT API error: " + error);

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
            String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            dataRepo.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
                @Override
                public void onResult(StoreProfile storeProfile) {
                    int distanceScore = 0;
                    double distanceMiles = 0;
                    String distanceDescription = "Distance not available";
                    
                    if (storeProfile != null && !gptResponse.getAddress().isEmpty()) {
                        //  Google Maps
                        GoogleMapsDistanceCalculator.calculateDistance(ScanResumeActivity.this, 
                            gptResponse.getAddress(), storeProfile.getFullAddress(),
                            new GoogleMapsDistanceCalculator.DistanceCallback() {
                                @Override
                                public void distancecalulate(double distance, String duration, String distanceText) {
                                    // Calculate distance score out of 25 points
                                    int calculatedDistanceScore = GoogleMapsDistanceCalculator.calculateDistanceScore(distance);
                                    String calculatedDistanceDescription = GoogleMapsDistanceCalculator.getDes(distance);
                                    
                                    // Calculate final overall score: GPT (75) + Distance (25) = 100
                                    int finalOverallScore = gptResponse.getOverallScore() + calculatedDistanceScore;
                                    
                                    Log.d("ScanResume", "GPT Score: " + gptResponse.getOverallScore() + 
                                          ", Distance Score: " + calculatedDistanceScore + 
                                          ", Final Score: " + finalOverallScore);
                                    
                                    saveResumeWithScore(resumeId, currentDate, finalOverallScore, resumeText,
                                        gptResponse, calculatedDistanceScore, distance, calculatedDistanceDescription);
                                }
                                
                                @Override
                                public void onError(String error) {
                                    Log.e("ScanResume", "Distance calculation error: " + error);
                                    int defaultDistanceScore = 12; // Default 12/25 points
                                    int finalOverallScore = gptResponse.getOverallScore() + defaultDistanceScore;
                                    
                                    Log.d("ScanResume", "Using default distance score. GPT: " + gptResponse.getOverallScore() + 
                                          ", Distance: " + defaultDistanceScore + ", Final: " + finalOverallScore);
                                    
                                    saveResumeWithScore(resumeId, currentDate, finalOverallScore, resumeText, 
                                        gptResponse, defaultDistanceScore, 15.0, "Default distance (15 miles)");
                                }
                            });
                    } else {
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

        String photoPath = saveResumePhoto(selectedImageUri, resumeId);

        String extractedDataJson = convertGPTResponseToJson(gptResponse);

        ResumeEntity newResume = new ResumeEntity(
            resumeId,
            jobId,
                jobtitle,
            currentDate,
            matchScore,
            resumeText,
            System.currentTimeMillis()
        );
        newResume.setPhotoPath(photoPath);
        newResume.setExtractedDataJson(extractedDataJson);

        dataRepo.insertResume(newResume, new DataRepository.DatabaseCallback<Void>() {
            @Override
            public void onResult(Void result) {
                runOnUiThread(() -> {
                    updateJobResumeCount();

                    Log.d("ScanResume", "Resume saved to database: " + resumeId + " with score: " + matchScore);

                    Intent intent = new Intent(ScanResumeActivity.this, MatchScoreActivity.class);
                    intent.putExtra("resumeId", resumeId);
                    intent.putExtra("matchScore", finalOverallScore);
                    intent.putExtra("resumeText", resumeText);
                    intent.putExtra("photoPath", photoPath);

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

                    intent.putExtra("skillScore", gptResponse.getSkillScore());
                    intent.putExtra("experienceScore", gptResponse.getExperienceScore());
                    intent.putExtra("availabilityScore", gptResponse.getAvailabilityScore());
                    intent.putExtra("educationScore", gptResponse.getEducationScore());
                    
                    intent.putExtra("distanceScore", distanceScore);
                    intent.putExtra("distanceMiles", distanceMiles);
                    intent.putExtra("distanceDescription", distanceDescription);

                    // Added feedback and recommendations from GPT
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
                EnhancedScoringSystem.ScoringResult scoringResult = EnhancedScoringSystem.calculateEnhancedScore(jobDescription, extractedData, storeProfile);

                String resumeId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                String matchScore = scoringResult.getOverallScore() + "%";

                String photoPath = saveResumePhoto(selectedImageUri, resumeId);

                String extractedDataJson = convertExtractedDataToJson(extractedData);

                ResumeEntity newResume = new ResumeEntity(
                    resumeId,
                    jobId,
                        jobtitle,
                    currentDate,
                    matchScore,
                    resumeText,
                    System.currentTimeMillis()
                );
                newResume.setPhotoPath(photoPath);
                newResume.setExtractedDataJson(extractedDataJson);

                dataRepo.insertResume(newResume, new DataRepository.DatabaseCallback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        runOnUiThread(() -> {
                            updateJobResumeCount();

                            Log.d("ScanResume", "Resume saved to database: " + resumeId + " with score: " + matchScore);

                            Intent intent = new Intent(ScanResumeActivity.this, MatchScoreActivity.class);
                            intent.putExtra("resumeId", resumeId);
                            intent.putExtra("matchScore", scoringResult.getOverallScore());
                            intent.putExtra("matchedKeywords", scoringResult.getMatchedSkills().toArray(new String[0]));
                            intent.putExtra("missingKeywords", scoringResult.getMissingSkills().toArray(new String[0]));
                            intent.putExtra("resumeText", resumeText);
                            intent.putExtra("photoPath", photoPath);

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

                            intent.putExtra("skillScore", scoringResult.getSkillMatchScore());
                            intent.putExtra("experienceScore", scoringResult.getExperienceScore());
                            intent.putExtra("availabilityScore", scoringResult.getAvailabilityScore());
                            intent.putExtra("educationScore", scoringResult.getEducationScore());
                            intent.putExtra("distanceScore", scoringResult.getDistanceScore());
                            intent.putExtra("distanceMiles", scoringResult.getDistanceMiles());
                            intent.putExtra("distanceDescription", scoringResult.getDistanceDescription());

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
            String fileName = "resume_" + resumeId + ".jpg";
            
            java.io.File storageDir = new java.io.File(getFilesDir(), "resume_photos");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            
            java.io.File photoFile = new java.io.File(storageDir, fileName);
            
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
        
        double matchPercentage = jobKeywords.isEmpty() ? 0 :
            (double) matchedKeywords.size() / jobKeywords.size() * 100;
        
        Log.d("KeywordMatching", "Match percentage: " + matchPercentage + "%");
        
        return new MatchResult((int) matchPercentage, matchedKeywords, missingKeywords);
    }

    private List<String> extractKeywords(String text) {
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
            dataRepo.getResumeCountForJob(jobId, new DataRepository.DatabaseCallback<Integer>() {
                @Override
                public void onResult(Integer resumeCount) {
                    dataRepo.get_job_id(jobId, new DataRepository.DatabaseCallback<JobEntity>() {
                        @Override
                        public void onResult(JobEntity jobEntity) {
                            if (jobEntity != null) {
                                jobEntity.setResumeCount(resumeCount);
                                dataRepo.update_Job(jobEntity, null);
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
        if (dataRepo != null) {
            dataRepo.shutdown();
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

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        try {
            Bitmap enhancedBitmap = enhanceBitmapForOCR(bitmap);
            
            java.io.File file = new java.io.File(getCacheDir(), "temp_image.jpg");
            java.io.OutputStream outputStream = new java.io.FileOutputStream(file);
            
            enhancedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            
            if (enhancedBitmap != bitmap) {
                enhancedBitmap.recycle();
            }
            
            return Uri.fromFile(file);
        } catch (Exception e) {
            Log.e("ScanResume", "Error getting image URI from bitmap: " + e.getMessage());
            return null;
        }
    }
    
    private Bitmap enhanceBitmapForOCR(Bitmap originalBitmap) {
        try {
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();
            
            int minWidth = 1200;
            int minHeight = 1600;
            
            if (originalWidth < minWidth || originalHeight < minHeight) {
                float scaleX = (float) minWidth / originalWidth;
                float scaleY = (float) minHeight / originalHeight;
                float scale = Math.max(scaleX, scaleY);
                
                int newWidth = Math.round(originalWidth * scale);
                int newHeight = Math.round(originalHeight * scale);
                
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                
                Bitmap enhancedBitmap = enhanceContrast(scaledBitmap);
                
                if (scaledBitmap != originalBitmap) {
                    scaledBitmap.recycle();
                }
                
                return enhancedBitmap;
            } else {
                return enhanceContrast(originalBitmap);
            }
        } catch (Exception e) {
            Log.e("ScanResume", "Error enhancing bitmap: " + e.getMessage());
            return originalBitmap;
        }
    }
    
    private Bitmap enhanceContrast(Bitmap bitmap) {
        try {
            Bitmap enhancedBitmap = bitmap.copy(bitmap.getConfig(), true);
            
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            
            for (int i = 0; i < pixels.length; i++) {
                int pixel = pixels[i];
                
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                
                int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                
                gray = (int) Math.max(0, Math.min(255, (gray - 128) * 1.5 + 128));
                
                int enhancedPixel = (gray << 16) | (gray << 8) | gray;
                pixels[i] = enhancedPixel;
            }
            
            enhancedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            
            return enhancedBitmap;
        } catch (Exception e) {
            Log.e("ScanResume", "Error enhancing contrast: " + e.getMessage());
            return bitmap;
        }
    }
}
