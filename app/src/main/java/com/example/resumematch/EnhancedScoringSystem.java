package com.example.resumematch;

import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnhancedScoringSystem {
    
    public static class ScoringResult {
        private int overallScore;
        private int skillMatchScore;
        private int experienceScore;
        private int availabilityScore;
        private int educationScore;
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
        
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public Map<String, Integer> getCategoryScores() { return categoryScores; }
        public void setCategoryScores(Map<String, Integer> categoryScores) { this.categoryScores = categoryScores; }
    }
    
    public static ScoringResult calculateEnhancedScore(String jobDescription, ResumeDataExtractor.ExtractedData resumeData) {
        ScoringResult result = new ScoringResult();
        
        try {
            // Extract job requirements
            JobRequirements jobReqs = extractJobRequirements(jobDescription);
            
            // Calculate skill match score
            calculateSkillMatchScore(jobReqs, resumeData, result);
            
            // Calculate experience score
            calculateExperienceScore(jobReqs, resumeData, result);
            
            // Calculate availability score
            calculateAvailabilityScore(jobReqs, resumeData, result);
            
            // Calculate education score
            calculateEducationScore(jobReqs, resumeData, result);
            
            // Calculate overall score
            calculateOverallScore(result);
            
            // Generate recommendations
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
        private String jobType; // full-time, part-time, etc.
        
        public JobRequirements() {
            this.requiredSkills = new ArrayList<>();
        }
        
        // Getters and Setters
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
    }
    
    private static JobRequirements extractJobRequirements(String jobDescription) {
        JobRequirements reqs = new JobRequirements();
        String lowerDesc = jobDescription.toLowerCase();
        
        // Extract skills from job description
        String[] skillKeywords = {
            "java", "python", "javascript", "react", "angular", "vue", "node.js", "spring",
            "android", "ios", "swift", "kotlin", "sql", "mongodb", "mysql", "postgresql",
            "aws", "azure", "docker", "kubernetes", "jenkins", "git", "agile", "scrum",
            "rest", "api", "microservices", "machine learning", "ai", "data science",
            "frontend", "backend", "full stack", "devops", "ui", "ux", "design",
            "project management", "leadership", "communication", "team", "collaboration",
            "customer service", "sales", "marketing", "analytics", "excel", "powerpoint",
            "word", "photoshop", "illustrator", "inventory", "pos", "cash handling"
        };
        
        for (String skill : skillKeywords) {
            if (lowerDesc.contains(skill)) {
                reqs.getRequiredSkills().add(skill);
            }
        }
        
        // Extract experience requirements
        if (lowerDesc.contains("senior") || lowerDesc.contains("5+ years") || lowerDesc.contains("5 years")) {
            reqs.setMinExperienceYears(5);
        } else if (lowerDesc.contains("mid-level") || lowerDesc.contains("3+ years") || lowerDesc.contains("3 years")) {
            reqs.setMinExperienceYears(3);
        } else if (lowerDesc.contains("junior") || lowerDesc.contains("entry") || lowerDesc.contains("1+ years")) {
            reqs.setMinExperienceYears(1);
        } else {
            reqs.setMinExperienceYears(0);
        }
        
        // Extract availability preferences
        if (lowerDesc.contains("immediate") || lowerDesc.contains("available now")) {
            reqs.setPreferredAvailability("immediate");
        } else if (lowerDesc.contains("flexible") || lowerDesc.contains("negotiable")) {
            reqs.setPreferredAvailability("flexible");
        } else {
            reqs.setPreferredAvailability("any");
        }
        
        // Extract education requirements
        if (lowerDesc.contains("bachelor") || lowerDesc.contains("degree")) {
            reqs.setEducationLevel("bachelor");
        } else if (lowerDesc.contains("high school") || lowerDesc.contains("diploma")) {
            reqs.setEducationLevel("high school");
        } else {
            reqs.setEducationLevel("any");
        }
        
        // Extract job type
        if (lowerDesc.contains("full-time") || lowerDesc.contains("full time")) {
            reqs.setJobType("full-time");
        } else if (lowerDesc.contains("part-time") || lowerDesc.contains("part time")) {
            reqs.setJobType("part-time");
        } else {
            reqs.setJobType("any");
        }
        
        return reqs;
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
        
        // Calculate skill match percentage
        if (jobReqs.getRequiredSkills().isEmpty()) {
            result.setSkillMatchScore(100); // No skills required
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
            result.setExperienceScore(100); // No experience required
        } else if (actualYears >= requiredYears) {
            result.setExperienceScore(100); // Meets or exceeds requirement
        } else if (actualYears >= requiredYears * 0.7) {
            result.setExperienceScore(80); // Close to requirement
        } else if (actualYears >= requiredYears * 0.5) {
            result.setExperienceScore(60); // Some experience
        } else {
            result.setExperienceScore(30); // Limited experience
        }
        
        result.getCategoryScores().put("Experience", result.getExperienceScore());
    }
    
    private static void calculateAvailabilityScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        String preferred = jobReqs.getPreferredAvailability();
        String actual = resumeData.getAvailability().toLowerCase();
        
        if (preferred.equals("any")) {
            result.setAvailabilityScore(100); // No specific requirement
        } else if (preferred.equals("immediate") && actual.contains("immediate")) {
            result.setAvailabilityScore(100);
        } else if (preferred.equals("flexible") && (actual.contains("flexible") || actual.contains("negotiable"))) {
            result.setAvailabilityScore(100);
        } else if (actual.contains("immediate")) {
            result.setAvailabilityScore(90); // Immediate availability is always good
        } else if (actual.contains("flexible") || actual.contains("negotiable")) {
            result.setAvailabilityScore(80);
        } else if (actual.contains("2 weeks")) {
            result.setAvailabilityScore(70);
        } else if (actual.contains("1 month")) {
            result.setAvailabilityScore(60);
        } else {
            result.setAvailabilityScore(50); // Not specified
        }
        
        result.getCategoryScores().put("Availability", result.getAvailabilityScore());
    }
    
    private static void calculateEducationScore(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        String required = jobReqs.getEducationLevel();
        String actual = resumeData.getEducation().toLowerCase();
        
        if (required.equals("any")) {
            result.setEducationScore(100); // No specific requirement
        } else if (required.equals("bachelor") && actual.contains("bachelor")) {
            result.setEducationScore(100);
        } else if (required.equals("high school") && (actual.contains("high school") || actual.contains("diploma"))) {
            result.setEducationScore(100);
        } else if (actual.contains("bachelor") || actual.contains("university")) {
            result.setEducationScore(90); // Higher education is always good
        } else if (actual.contains("high school") || actual.contains("diploma")) {
            result.setEducationScore(70);
        } else {
            result.setEducationScore(50); // Not specified
        }
        
        result.getCategoryScores().put("Education", result.getEducationScore());
    }
    
    private static void calculateOverallScore(ScoringResult result) {
        // Weighted average: Skills (40%), Experience (30%), Availability (20%), Education (10%)
        int overallScore = (int) (
            result.getSkillMatchScore() * 0.4 +
            result.getExperienceScore() * 0.3 +
            result.getAvailabilityScore() * 0.2 +
            result.getEducationScore() * 0.1
        );
        
        result.setOverallScore(overallScore);
    }
    
    private static void generateRecommendations(JobRequirements jobReqs, ResumeDataExtractor.ExtractedData resumeData, ScoringResult result) {
        List<String> recommendations = new ArrayList<>();
        
        // Skill-based recommendations
        if (result.getSkillMatchScore() < 70) {
            recommendations.add("Consider candidates with more relevant skills");
        }
        
        // Experience-based recommendations
        if (result.getExperienceScore() < 70) {
            recommendations.add("May need additional training for required experience level");
        }
        
        // Availability-based recommendations
        if (result.getAvailabilityScore() < 70) {
            recommendations.add("Check candidate's availability timeline");
        }
        
        // Overall recommendations
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