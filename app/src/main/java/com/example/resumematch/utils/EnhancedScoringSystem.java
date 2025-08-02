package com.example.resumematch.utils;
// this is the static enhancing score one, we used gpt to extract certain fields to calculate the score
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.resumematch.models.StoreProfile;

public class EnhancedScoringSystem {
    
    public static class ScoringResult {
        private int overallScore;
        private int skillMatchScore;
        private int experienceScore;
        private int availabilityScore;
        private int educationScore;
        private int distanceScore;
        private double distanceMiles;
        private String distanceDescription;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private List<String> recommendations;
        private Map<String, Integer> categoryScores;
        
        public ScoringResult() {
            this.matchedSkills = new ArrayList<>();
            this.missingSkills = new ArrayList<>();
            this.recommendations = new ArrayList<>();
            this.categoryScores = new HashMap<>();
        }
        
        // Getters and Setters
        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
        
        public int getSkillMatchScore() { return skillMatchScore; }
        public void setSkillMatchScore(int skillMatchScore) { this.skillMatchScore = skillMatchScore; }
        
        public int getExperienceScore() { return experienceScore; }
        public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }
        
        public int getAvailabilityScore() { return availabilityScore; }
        public void setAvailabilityScore(int availabilityScore) { this.availabilityScore = availabilityScore; }
        
        public int getEducationScore() { return educationScore; }
        public void setEducationScore(int educationScore) { this.educationScore = educationScore; }
        
        public int getDistanceScore() { return distanceScore; }
        public void setDistanceScore(int distanceScore) { this.distanceScore = distanceScore; }
        
        public double getDistanceMiles() { return distanceMiles; }
        public void setDistanceMiles(double distanceMiles) { this.distanceMiles = distanceMiles; }
        
        public String getDistanceDescription() { return distanceDescription; }
        public void setDistanceDescription(String distanceDescription) { this.distanceDescription = distanceDescription; }
        
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public Map<String, Integer> getCategoryScores() { return categoryScores; }
        public void setCategoryScores(Map<String, Integer> categoryScores) { this.categoryScores = categoryScores; }
    }
    
    public static ScoringResult calculateEnhancedScore(String jobDescription, ResumeDataExtractor.ExtractedData resumeData, StoreProfile storeProfile) {
        ScoringResult result = new ScoringResult();
        
        try {
            JobRequirements jobReqs = extractJobRequirements(jobDescription);
            
            calculateSkillMatchScore(jobReqs, resumeData, result);
            
            calculateExperienceScore(jobReqs, resumeData, result);
            
            calculateAvailabilityScore(jobReqs, resumeData, result);
            
            calculateEducationScore(jobReqs, resumeData, result);
            
            calculateDistanceScore(resumeData, storeProfile, result);
            

            calculateOverallScore(result);
            
            generateRecommendations(jobReqs, resumeData, result);
            
            Log.d("EnhancedScoring", "Overall score: " + result.getOverallScore() + "%");
            
        } catch (Exception e) {
            Log.e("EnhancedScoring", "Error calculating score: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    private static class JobRequirements {
        private List<String> requiredSkills;
        private int minExperienceYears;
        private String preferredAvailability;
        private String educationLevel;
        private String jobType;
        private String preferredTransportation;
        private String preferredLanguages;
        private String salaryRange;
        
        public JobRequirements() {
            this.requiredSkills = new ArrayList<>();
        }
        

        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
        
        public int getMinExperienceYears() { return minExperienceYears; }
        public void setMinExperienceYears(int minExperienceYears) { this.minExperienceYears = minExperienceYears; }
        
        public String getPreferredAvailability() { return preferredAvailability; }
        public void setPreferredAvailability(String preferredAvailability) { this.preferredAvailability = preferredAvailability; }
        
        public String getEducationLevel() { return educationLevel; }
        public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
        
        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }
        
        public String getPreferredTransportation() { return preferredTransportation; }
        public void setPreferredTransportation(String preferredTransportation) { this.preferredTransportation = preferredTransportation; }
        
        public String getPreferredLanguages() { return preferredLanguages; }
        public void setPreferredLanguages(String preferredLanguages) { this.preferredLanguages = preferredLanguages; }
        
        public String getSalaryRange() { return salaryRange; }
        public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    }
    
    private static JobRequirements extractJobRequirements(String jobDescription) {
        JobRequirements reqs = new JobRequirements();
        String lowerDesc = jobDescription.toLowerCase();
        
        List<String> extractedSkills = extractSkillsFromDescription(jobDescription);
        reqs.setRequiredSkills(extractedSkills);
        
        if (lowerDesc.contains("senior") || lowerDesc.contains("5+ years") || lowerDesc.contains("5 years")) {
            reqs.setMinExperienceYears(5);
        } else if (lowerDesc.contains("mid-level") || lowerDesc.contains("3+ years") || lowerDesc.contains("3 years")) {
            reqs.setMinExperienceYears(3);
        } else if (lowerDesc.contains("junior") || lowerDesc.contains("entry") || lowerDesc.contains("1+ years")) {
            reqs.setMinExperienceYears(1);
        } else {
            reqs.setMinExperienceYears(0);
        }
        
        if (lowerDesc.contains("immediate") || lowerDesc.contains("available now")) {
            reqs.setPreferredAvailability("immediate");
        } else if (lowerDesc.contains("flexible") || lowerDesc.contains("negotiable")) {
            reqs.setPreferredAvailability("flexible");
        } else {
            reqs.setPreferredAvailability("any");
        }
        
        if (lowerDesc.contains("bachelor") || lowerDesc.contains("degree")) {
            reqs.setEducationLevel("bachelor");
        } else if (lowerDesc.contains("high school") || lowerDesc.contains("diploma")) {
            reqs.setEducationLevel("high school");
        } else {
            reqs.setEducationLevel("any");
        }
        
        if (lowerDesc.contains("full-time") || lowerDesc.contains("full time")) {
            reqs.setJobType("full-time");
        } else if (lowerDesc.contains("part-time") || lowerDesc.contains("part time")) {
            reqs.setJobType("part-time");
        } else {
            reqs.setJobType("any");
        }
        
        if (lowerDesc.contains("car") || lowerDesc.contains("vehicle") || lowerDesc.contains("driving")) {
            reqs.setPreferredTransportation("car");
        } else if (lowerDesc.contains("public transit") || lowerDesc.contains("bus")) {
            reqs.setPreferredTransportation("public transit");
        } else {
            reqs.setPreferredTransportation("any");
        }
        
        if (lowerDesc.contains("spanish") || lowerDesc.contains("bilingual")) {
            reqs.setPreferredLanguages("spanish");
        } else if (lowerDesc.contains("chinese") || lowerDesc.contains("mandarin")) {
            reqs.setPreferredLanguages("chinese");
        } else {
            reqs.setPreferredLanguages("english");
        }
        
        if (lowerDesc.contains("$15") || lowerDesc.contains("15/hour")) {
            reqs.setSalaryRange("$15/hour");
        } else if (lowerDesc.contains("$20") || lowerDesc.contains("20/hour")) {
            reqs.setSalaryRange("$20/hour");
        } else if (lowerDesc.contains("$25") || lowerDesc.contains("25/hour")) {
            reqs.setSalaryRange("$25/hour");
        } else {
            reqs.setSalaryRange("negotiable");
        }
        
        return reqs;
    }
    
    private static List<String> extractSkillsFromDescription(String jobDescription) {
        List<String> skills = new ArrayList<>();
        String lowerDesc = jobDescription.toLowerCase();
        

        String[] skillKeywords = {
            "java", "python", "javascript", "react", "angular", "vue", "node.js", "spring",
            "android", "ios", "swift", "kotlin", "sql", "mongodb", "mysql", "postgresql",
            "aws", "azure", "docker", "kubernetes", "jenkins", "git", "agile", "scrum",
            "rest", "api", "microservices", "machine learning", "ai", "data science",
            "frontend", "backend", "full stack", "devops", "ui", "ux", "design",
            
            "project management", "leadership", "communication", "team", "collaboration",
            "customer service", "sales", "marketing", "analytics", "excel", "powerpoint",
            "word", "photoshop", "illustrator", "inventory", "pos", "cash handling",
            
            "retail", "cashier", "stock", "merchandising", "food service", "cooking",
            "cleaning", "maintenance", "security", "driving", "delivery", "customer",
            "cash register", "point of sale", "inventory management", "scheduling",
            "multitasking", "problem solving", "attention to detail", "time management"
        };
        
        for (String skill : skillKeywords) {
            if (lowerDesc.contains(skill)) {
                skills.add(skill);
            }
        }
        
        String[] requirementPatterns = {
            "must have", "required", "needed", "essential", "preferred", "experience with",
            "knowledge of", "familiar with", "proficient in", "skilled in"
        };
        
        for (String pattern : requirementPatterns) {
            if (lowerDesc.contains(pattern)) {
                int index = lowerDesc.indexOf(pattern);
                if (index != -1) {
                    String afterPattern = lowerDesc.substring(index + pattern.length());
                    String[] words = afterPattern.split("\\s+");
                    for (int i = 0; i < Math.min(5, words.length); i++) {
                        String word = words[i].replaceAll("[^a-zA-Z]", "");
                        if (word.length() > 2) {
                            skills.add(word);
                        }
                    }
                }
            }
        }
        
        return skills;
    }
    
    private static void calculateSkillMatchScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        
        for (String requiredSkill : jobReqs.getRequiredSkills()) {
            if (resumeData.getSkills().contains(requiredSkill)) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }
        
        result.setMatchedSkills(matchedSkills);
        result.setMissingSkills(missingSkills);
        
        if (jobReqs.getRequiredSkills().isEmpty()) {
            result.setSkillMatchScore(100);
        } else {
            double skillMatchPercentage = (double) matchedSkills.size() / jobReqs.getRequiredSkills().size() * 100;
            result.setSkillMatchScore((int) skillMatchPercentage);
        }
        
        result.getCategoryScores().put("Skills", result.getSkillMatchScore());
    }
    
    private static void calculateExperienceScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        int requiredYears = jobReqs.getMinExperienceYears();
        int actualYears = resumeData.getExperienceYears();
        
        if (requiredYears == 0) {
            result.setExperienceScore(100);
        } else if (actualYears >= requiredYears) {
            result.setExperienceScore(100);
        } else if (actualYears >= requiredYears * 0.7) {
            result.setExperienceScore(80);
        } else if (actualYears >= requiredYears * 0.5) {
            result.setExperienceScore(60);
        } else {
            result.setExperienceScore(30);
        }
        
        result.getCategoryScores().put("Experience", result.getExperienceScore());
    }
    
    private static void calculateAvailabilityScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        String preferred = jobReqs.getPreferredAvailability();
        String actual = resumeData.getAvailability().toLowerCase();
        
        if (preferred.equals("any")) {
            result.setAvailabilityScore(100);
        } else if (preferred.equals("immediate") && actual.contains("immediate")) {
            result.setAvailabilityScore(100);
        } else if (preferred.equals("flexible") && (actual.contains("flexible") || actual.contains("negotiable"))) {
            result.setAvailabilityScore(100);
        } else if (actual.contains("immediate")) {
            result.setAvailabilityScore(90);
        } else if (actual.contains("flexible") || actual.contains("negotiable")) {
            result.setAvailabilityScore(80);
        } else if (actual.contains("2 weeks")) {
            result.setAvailabilityScore(70);
        } else if (actual.contains("1 month")) {
            result.setAvailabilityScore(60);
        } else {
            result.setAvailabilityScore(50);
        }
        
        result.getCategoryScores().put("Availability", result.getAvailabilityScore());
    }
    
    private static void calculateEducationScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        String required = jobReqs.getEducationLevel();
        String actual = resumeData.getEducation().toLowerCase();
        
        if (required.equals("any")) {
            result.setEducationScore(100);
        } else if (required.equals("bachelor") && actual.contains("bachelor")) {
            result.setEducationScore(100);
        } else if (required.equals("high school") && (actual.contains("high school") || actual.contains("diploma"))) {
            result.setEducationScore(100);
        } else if (actual.contains("bachelor") || actual.contains("university")) {
            result.setEducationScore(90);
        } else if (actual.contains("high school") || actual.contains("diploma")) {
            result.setEducationScore(70);
        } else {
            result.setEducationScore(50);
        }
        
        result.getCategoryScores().put("Education", result.getEducationScore());
    }
    
    private static void calculateDistanceScore(ResumeDataExtractor.ExtractedData resumeData, StoreProfile storeProfile, ScoringResult result) {
        if (storeProfile == null || resumeData.getAddress().isEmpty()) {
            result.setDistanceScore(50);
            result.setDistanceMiles(0);
            result.setDistanceDescription("Distance not available");
            return;
        }
        
        try {
            String candidateAddress = resumeData.getFormattedAddress();
            String storeAddress = storeProfile.getFormattedAddress();
            
            if (candidateAddress.isEmpty() || storeAddress.isEmpty()) {
                result.setDistanceScore(50);
                result.setDistanceMiles(0);
                result.setDistanceDescription("Address not available");
                return;
            }
            
            GoogleMapsDistanceCalculator.calculateDistance(null, candidateAddress, storeAddress,
                new GoogleMapsDistanceCalculator.DistanceCallback() {
                    @Override
                    public void distancecalulate(double distance, String duration, String distanceText) {
                        int distanceScore = GoogleMapsDistanceCalculator.calculateDistanceScore(distance);
                        String distanceDescription = GoogleMapsDistanceCalculator.getDes(distance);
                        
                        result.setDistanceScore(distanceScore);
                        result.setDistanceMiles(distance);
                        result.setDistanceDescription(distanceDescription);
                        
                        result.getCategoryScores().put("Distance", distanceScore);
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e("EnhancedScoring", "Error calculating distance: " + error);
                        result.setDistanceScore(50);
                        result.setDistanceMiles(0);
                        result.setDistanceDescription("Distance calculation error");
                    }
                });
            
        } catch (Exception e) {
            Log.e("EnhancedScoring", "Error calculating distance: " + e.getMessage());
            result.setDistanceScore(50);
            result.setDistanceMiles(0);
            result.setDistanceDescription("Distance calculation error");
        }
    }
    
    private static void calculateOverallScore(ScoringResult result) {
        int overallScore = (int) (
            result.getSkillMatchScore() * 0.25 +
            result.getExperienceScore() * 0.20 +
            result.getAvailabilityScore() * 0.20 +
            result.getEducationScore() * 0.10 +
            result.getDistanceScore() * 0.25
        );
        
        result.setOverallScore(overallScore);
    }
    
    private static void generateRecommendations(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        List<String> recommendations = new ArrayList<>();
        
        if (result.getSkillMatchScore() < 70) {
            recommendations.add("Consider candidates with more relevant skills");
        }
        
        if (result.getExperienceScore() < 70) {
            recommendations.add("May need additional training for required experience level");
        }
        
        if (result.getAvailabilityScore() < 70) {
            recommendations.add("Check candidate's availability timeline");
        }
        
        if (result.getDistanceScore() < 70) {
            recommendations.add("Consider commute time and transportation options");
        }
        
        if (result.getOverallScore() >= 90) {
            recommendations.add("Strong candidate - recommend for interview");
        } else if (result.getOverallScore() >= 70) {
            recommendations.add("Good candidate - consider for interview");
        } else if (result.getOverallScore() >= 50) {
            recommendations.add("Moderate match - consider for junior role");
        } else {
            recommendations.add("Limited match - may not be suitable");
        }
        
        result.setRecommendations(recommendations);
    }
} 