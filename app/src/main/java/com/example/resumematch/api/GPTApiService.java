package com.example.resumematch.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.example.resumematch.utils.Config;

public class GPTApiService {
    
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.d("GPTApi", "Making request to: " + request.url());
                Response response = chain.proceed(request);
                Log.d("GPTApi", "Response code: " + response.code());
                return response;
            })
            .build();
    
    public interface GPTCallback {
        void onSuccess(GPTResponse response);
        void onError(String error);
    }
    
    public static class GPTResponse {
        private String candidateName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String currentTitle;
        private int experienceYears;
        private String education;
        private String availability;
        private String availabilityDetails;
        private String transportation;
        private String startDate;
        private String workAuthorization;
        private String emergencyContact;
        private String emergencyPhone;
        private String references;
        private String previousRetailExperience;
        private String languages;
        private String certifications;
        private String skills;
        private int skillScore;
        private int experienceScore;
        private int availabilityScore;
        private int educationScore;
        private int overallScore;
        private String feedback;
        private String recommendations;
        
        // Getters and Setters
        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getCurrentTitle() { return currentTitle; }
        public void setCurrentTitle(String currentTitle) { this.currentTitle = currentTitle; }
        
        public int getExperienceYears() { return experienceYears; }
        public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
        
        public String getEducation() { return education; }
        public void setEducation(String education) { this.education = education; }
        
        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }
        
        public String getAvailabilityDetails() { return availabilityDetails; }
        public void setAvailabilityDetails(String availabilityDetails) { this.availabilityDetails = availabilityDetails; }
        
        public String getTransportation() { return transportation; }
        public void setTransportation(String transportation) { this.transportation = transportation; }
        
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        
        public String getWorkAuthorization() { return workAuthorization; }
        public void setWorkAuthorization(String workAuthorization) { this.workAuthorization = workAuthorization; }
        
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
        
        public String getEmergencyPhone() { return emergencyPhone; }
        public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
        
        public String getReferences() { return references; }
        public void setReferences(String references) { this.references = references; }
        
        public String getPreviousRetailExperience() { return previousRetailExperience; }
        public void setPreviousRetailExperience(String previousRetailExperience) { this.previousRetailExperience = previousRetailExperience; }
        
        public String getLanguages() { return languages; }
        public void setLanguages(String languages) { this.languages = languages; }
        
        public String getCertifications() { return certifications; }
        public void setCertifications(String certifications) { this.certifications = certifications; }
        
        public String getSkills() { return skills; }
        public void setSkills(String skills) { this.skills = skills; }
        
        public int getSkillScore() { return skillScore; }
        public void setSkillScore(int skillScore) { this.skillScore = skillScore; }
        
        public int getExperienceScore() { return experienceScore; }
        public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }
        
        public int getAvailabilityScore() { return availabilityScore; }
        public void setAvailabilityScore(int availabilityScore) { this.availabilityScore = availabilityScore; }
        
        public int getEducationScore() { return educationScore; }
        public void setEducationScore(int educationScore) { this.educationScore = educationScore; }
        
        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        
        public String getRecommendations() { return recommendations; }
        public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    }
    
    public static void analyzeResume(String resumeText, String jobDescription, String storeAddress, GPTCallback callback) {
        try {
            Log.d("GPTApi", "Starting resume analysis...");
            Log.d("GPTApi", "Resume text length: " + resumeText.length());
            Log.d("GPTApi", "Job description: " + jobDescription);
            Log.d("GPTApi", "Store address: " + storeAddress);
            
            // Validate API key
            String apiKey = Config.getOpenAIApiKey();
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
                Log.e("GPTApi", "Invalid API key");
                callback.onError("Invalid API key. Please check the configuration.");
                return;
            }
            
            // Validate inputs
            if (resumeText == null || resumeText.trim().isEmpty()) {
                Log.e("GPTApi", "Empty resume text");
                callback.onError("Resume text is empty. Please try again.");
                return;
            }
            
            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                Log.e("GPTApi", "Empty job description");
                callback.onError("Job description is empty. Please try again.");
                return;
            }
            
            // Create the prompt for GPT
            String prompt = createAnalysisPrompt(resumeText, jobDescription, storeAddress);
            Log.d("GPTApi", "Prompt created, length: " + prompt.length());
            
            // Create JSON request
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4");
            
            // Create messages array properly
            JSONArray messagesArray = new JSONArray();
            
            // Add system message
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are an expert HR analyst specializing in resume analysis and candidate scoring for small retail businesses. Extract accurate information and provide detailed scoring.");
            messagesArray.put(systemMessage);
            
            // Add user message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messagesArray.put(userMessage);
            
            // Add messages array to request body
            requestBody.put("messages", messagesArray);
            requestBody.put("temperature", 0.1);
            requestBody.put("max_tokens", 2000);
            
            Log.d("GPTApi", "Request body created");
            
            // Create HTTP request
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
            );
            
            Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
            
            Log.d("GPTApi", "Making API request to: " + BASE_URL);
            
            // Execute request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("GPTApi", "API call failed: " + e.getMessage());
                    Log.e("GPTApi", "Exception type: " + e.getClass().getSimpleName());
                    e.printStackTrace();
                    
                    String errorMessage = "Network error: " + e.getMessage();
                    if (e instanceof java.net.SocketTimeoutException) {
                        errorMessage = "Request timed out. Please check your internet connection and try again.";
                    } else if (e instanceof java.net.UnknownHostException) {
                        errorMessage = "Cannot connect to server. Please check your internet connection.";
                    }
                    
                    callback.onError(errorMessage);
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("GPTApi", "Received response: " + response.code());
                    
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            Log.d("GPTApi", "Response body length: " + responseBody.length());
                            Log.d("GPTApi", "Response body preview: " + responseBody.substring(0, Math.min(200, responseBody.length())));
                            
                            GPTResponse gptResponse = parseGPTResponse(responseBody);
                            Log.d("GPTApi", "GPT response parsed successfully");
                            callback.onSuccess(gptResponse);
                        } catch (Exception e) {
                            Log.e("GPTApi", "Error parsing response: " + e.getMessage());
                            Log.e("GPTApi", "Exception type: " + e.getClass().getSimpleName());
                            e.printStackTrace();
                            callback.onError("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        Log.e("GPTApi", "API error: " + response.code() + " - " + response.message());
                        Log.e("GPTApi", "Error body: " + errorBody);
                        
                        String errorMessage = "API error: " + response.code() + " - " + response.message();
                        if (response.code() == 401) {
                            errorMessage = "Authentication failed. Please check the API key.";
                        } else if (response.code() == 429) {
                            errorMessage = "Rate limit exceeded. Please try again later.";
                        } else if (response.code() == 500) {
                            errorMessage = "Server error. Please try again later.";
                        }
                        
                        callback.onError(errorMessage + "\n" + errorBody);
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e("GPTApi", "Error creating request: " + e.getMessage());
            Log.e("GPTApi", "Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            callback.onError("Error creating request: " + e.getMessage());
        }
    }
    
    private static String createAnalysisPrompt(String resumeText, String jobDescription, String storeAddress) {
        return "You are an expert HR analyst. Analyze this resume for a job application and provide detailed scoring. " +
               "Resume Text: " + resumeText + "\n\n" +
               "Job Description: " + jobDescription + "\n\n" +
               "Store Address: " + storeAddress + "\n\n" +
               "Please extract the following information and provide scoring. Return ONLY a valid JSON object with these exact field names:\n" +
               "{\n" +
               "  \"candidateName\": \"extracted name\",\n" +
               "  \"email\": \"extracted email\",\n" +
               "  \"phone\": \"extracted phone\",\n" +
               "  \"address\": \"full address\",\n" +
               "  \"city\": \"city name\",\n" +
               "  \"state\": \"state name\",\n" +
               "  \"zipCode\": \"zip code\",\n" +
               "  \"currentTitle\": \"current job title\",\n" +
               "  \"experienceYears\": number,\n" +
               "  \"education\": \"education level\",\n" +
               "  \"availability\": \"availability status\",\n" +
               "  \"availabilityDetails\": \"detailed availability\",\n" +
               "  \"transportation\": \"transportation method\",\n" +
               "  \"startDate\": \"start date availability\",\n" +
               "  \"workAuthorization\": \"work authorization status\",\n" +
               "  \"emergencyContact\": \"emergency contact name\",\n" +
               "  \"emergencyPhone\": \"emergency contact phone\",\n" +
               "  \"references\": \"references information\",\n" +
               "  \"previousRetailExperience\": \"previous retail experience\",\n" +
               "  \"languages\": \"languages spoken\",\n" +
               "  \"certifications\": \"certifications\",\n" +
               "  \"skills\": \"comma-separated skills\",\n" +
               "  \"skillScore\": number,\n" +
               "  \"experienceScore\": number,\n" +
               "  \"availabilityScore\": number,\n" +
               "  \"educationScore\": number,\n" +
               "  \"overallScore\": number,\n" +
               "  \"feedback\": \"detailed feedback\",\n" +
               "  \"recommendations\": \"specific recommendations\"\n" +
               "}\n\n" +
               "IMPORTANT SCORING GUIDELINES:\n" +
               "- skillScore (0-25): Based on skills match with job requirements\n" +
               "- experienceScore (0-20): Based on relevant experience years\n" +
               "- availabilityScore (0-20): Based on availability match\n" +
               "- educationScore (0-10): Based on education requirements\n" +
               "- overallScore (0-75): Sum of all scores (MUST NOT EXCEED 75)\n" +
               "- Distance will be calculated separately and added to make total 100\n\n" +
               "Return ONLY the JSON object, no additional text.";
    }
    
    private static GPTResponse parseGPTResponse(String responseBody) {
        try {
            Log.d("GPTApi", "Parsing response: " + responseBody.substring(0, Math.min(500, responseBody.length())));
            
            JSONObject response = new JSONObject(responseBody);
            
            // Check if there are choices
            if (!response.has("choices") || response.getJSONArray("choices").length() == 0) {
                throw new RuntimeException("No choices in GPT response");
            }
            
            JSONObject choice = response.getJSONArray("choices").getJSONObject(0);
            if (!choice.has("message")) {
                throw new RuntimeException("No message in choice");
            }
            
            String content = choice.getJSONObject("message").getString("content");
            Log.d("GPTApi", "GPT content: " + content.substring(0, Math.min(200, content.length())));
            
            // Try to parse the content as JSON first
            GPTResponse gptResponse = new GPTResponse();
            
            try {
                // The content should be a JSON object
                JSONObject analysis = new JSONObject(content);
                
                // Parse all fields with proper error handling
                gptResponse.setCandidateName(analysis.optString("candidateName", ""));
                gptResponse.setEmail(analysis.optString("email", ""));
                gptResponse.setPhone(analysis.optString("phone", ""));
                gptResponse.setAddress(analysis.optString("address", ""));
                gptResponse.setCity(analysis.optString("city", ""));
                gptResponse.setState(analysis.optString("state", ""));
                gptResponse.setZipCode(analysis.optString("zipCode", ""));
                gptResponse.setCurrentTitle(analysis.optString("currentTitle", ""));
                gptResponse.setExperienceYears(analysis.optInt("experienceYears", 0));
                gptResponse.setEducation(analysis.optString("education", ""));
                gptResponse.setAvailability(analysis.optString("availability", ""));
                gptResponse.setAvailabilityDetails(analysis.optString("availabilityDetails", ""));
                gptResponse.setTransportation(analysis.optString("transportation", ""));
                gptResponse.setStartDate(analysis.optString("startDate", ""));
                gptResponse.setWorkAuthorization(analysis.optString("workAuthorization", ""));
                gptResponse.setEmergencyContact(analysis.optString("emergencyContact", ""));
                gptResponse.setEmergencyPhone(analysis.optString("emergencyPhone", ""));
                gptResponse.setReferences(analysis.optString("references", ""));
                gptResponse.setPreviousRetailExperience(analysis.optString("previousRetailExperience", ""));
                gptResponse.setLanguages(analysis.optString("languages", ""));
                gptResponse.setCertifications(analysis.optString("certifications", ""));
                gptResponse.setSkills(analysis.optString("skills", ""));
                gptResponse.setSkillScore(analysis.optInt("skillScore", 0));
                gptResponse.setExperienceScore(analysis.optInt("experienceScore", 0));
                gptResponse.setAvailabilityScore(analysis.optInt("availabilityScore", 0));
                gptResponse.setEducationScore(analysis.optInt("educationScore", 0));
                gptResponse.setOverallScore(analysis.optInt("overallScore", 0));
                gptResponse.setFeedback(analysis.optString("feedback", ""));
                gptResponse.setRecommendations(analysis.optString("recommendations", ""));
                
                Log.d("GPTApi", "Successfully parsed GPT response");
                
            } catch (Exception e) {
                Log.e("GPTApi", "Error parsing JSON content: " + e.getMessage());
                Log.e("GPTApi", "Content was: " + content);
                
                // If JSON parsing fails, create a basic response
                gptResponse.setCandidateName("Unknown");
                gptResponse.setEmail("");
                gptResponse.setPhone("");
                gptResponse.setAddress("");
                gptResponse.setCity("");
                gptResponse.setState("");
                gptResponse.setZipCode("");
                gptResponse.setCurrentTitle("");
                gptResponse.setExperienceYears(0);
                gptResponse.setEducation("");
                gptResponse.setAvailability("");
                gptResponse.setAvailabilityDetails("");
                gptResponse.setTransportation("");
                gptResponse.setStartDate("");
                gptResponse.setWorkAuthorization("");
                gptResponse.setEmergencyContact("");
                gptResponse.setEmergencyPhone("");
                gptResponse.setReferences("");
                gptResponse.setPreviousRetailExperience("");
                gptResponse.setLanguages("");
                gptResponse.setCertifications("");
                gptResponse.setSkills("");
                gptResponse.setSkillScore(50);
                gptResponse.setExperienceScore(50);
                gptResponse.setAvailabilityScore(50);
                gptResponse.setEducationScore(50);
                gptResponse.setOverallScore(50);
                gptResponse.setFeedback("Could not parse AI response. Using fallback scoring.");
                gptResponse.setRecommendations("Consider using the fallback method for more accurate results.");
            }
            
            return gptResponse;
            
        } catch (Exception e) {
            Log.e("GPTApi", "Error parsing GPT response: " + e.getMessage());
            Log.e("GPTApi", "Full response: " + responseBody);
            e.printStackTrace();
            throw new RuntimeException("Error parsing GPT response: " + e.getMessage());
        }
    }
} 