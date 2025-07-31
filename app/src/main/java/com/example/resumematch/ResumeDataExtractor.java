package com.example.resumematch;

import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeDataExtractor {
    
    public static class ExtractedData implements Serializable {
        private String name;
        private String email;
        private String phone;
        private String location;
        private String currentTitle;
        private int yearsOfExperience;
        private List<String> skills;
        private List<String> availability;
        private String education;
        private String expectedSalary;
        private Map<String, Object> additionalInfo;
        
        public ExtractedData() {
            this.skills = new ArrayList<>();
            this.availability = new ArrayList<>();
            this.additionalInfo = new HashMap<>();
        }
        
        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getLocation() { return location; }
        public String getCurrentTitle() { return currentTitle; }
        public int getYearsOfExperience() { return yearsOfExperience; }
        public List<String> getSkills() { return skills; }
        public List<String> getAvailability() { return availability; }
        public String getEducation() { return education; }
        public String getExpectedSalary() { return expectedSalary; }
        public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
        
        // Setters
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setLocation(String location) { this.location = location; }
        public void setCurrentTitle(String currentTitle) { this.currentTitle = currentTitle; }
        public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public void setAvailability(List<String> availability) { this.availability = availability; }
        public void setEducation(String education) { this.education = education; }
        public void setExpectedSalary(String expectedSalary) { this.expectedSalary = expectedSalary; }
        public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
        
        @Override
        public String toString() {
            return "ExtractedData{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", location='" + location + '\'' +
                    ", currentTitle='" + currentTitle + '\'' +
                    ", yearsOfExperience=" + yearsOfExperience +
                    ", skills=" + skills +
                    ", availability=" + availability +
                    ", education='" + education + '\'' +
                    ", expectedSalary='" + expectedSalary + '\'' +
                    '}';
        }
    }
    
    public static class EnhancedMatchResult {
        private int overallScore;
        private int skillsScore;
        private int experienceScore;
        private int availabilityScore;
        private int educationScore;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private String recommendation;
        private ExtractedData extractedData;
        
        public EnhancedMatchResult(int overallScore, int skillsScore, int experienceScore, 
                                 int availabilityScore, int educationScore, 
                                 List<String> matchedSkills, List<String> missingSkills, 
                                 String recommendation, ExtractedData extractedData) {
            this.overallScore = overallScore;
            this.skillsScore = skillsScore;
            this.experienceScore = experienceScore;
            this.availabilityScore = availabilityScore;
            this.educationScore = educationScore;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.recommendation = recommendation;
            this.extractedData = extractedData;
        }
        
        // Getters
        public int getOverallScore() { return overallScore; }
        public int getSkillsScore() { return skillsScore; }
        public int getExperienceScore() { return experienceScore; }
        public int getAvailabilityScore() { return availabilityScore; }
        public int getEducationScore() { return educationScore; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
        public String getRecommendation() { return recommendation; }
        public ExtractedData getExtractedData() { return extractedData; }
    }
    
    public static ExtractedData extractDataFromResume(String resumeText) {
        ExtractedData data = new ExtractedData();
        
        try {
            // Extract name (usually at the top of resume)
            data.setName(extractName(resumeText));
            
            // Extract email
            data.setEmail(extractEmail(resumeText));
            
            // Extract phone
            data.setPhone(extractPhone(resumeText));
            
            // Extract location
            data.setLocation(extractLocation(resumeText));
            
            // Extract current title
            data.setCurrentTitle(extractCurrentTitle(resumeText));
            
            // Extract years of experience
            data.setYearsOfExperience(extractYearsOfExperience(resumeText));
            
            // Extract skills
            data.setSkills(extractSkills(resumeText));
            
            // Extract availability
            data.setAvailability(extractAvailability(resumeText));
            
            // Extract education
            data.setEducation(extractEducation(resumeText));
            
            // Extract expected salary
            data.setExpectedSalary(extractExpectedSalary(resumeText));
            
            Log.d("ResumeDataExtractor", "Extracted data: " + data.toString());
            
        } catch (Exception e) {
            Log.e("ResumeDataExtractor", "Error extracting data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }
    
    public static EnhancedMatchResult calculateEnhancedMatchScore(String jobDescription, String resumeText) {
        ExtractedData extractedData = extractDataFromResume(resumeText);
        
        // Extract job requirements
        List<String> jobSkills = extractSkillsFromJob(jobDescription);
        List<String> jobAvailability = extractAvailabilityFromJob(jobDescription);
        int requiredExperience = extractRequiredExperience(jobDescription);
        String requiredEducation = extractRequiredEducation(jobDescription);
        
        // Calculate individual scores
        int skillsScore = calculateSkillsScore(jobSkills, extractedData.getSkills());
        int experienceScore = calculateExperienceScore(requiredExperience, extractedData.getYearsOfExperience());
        int availabilityScore = calculateAvailabilityScore(jobAvailability, extractedData.getAvailability());
        int educationScore = calculateEducationScore(requiredEducation, extractedData.getEducation());
        
        // Calculate overall score (weighted average)
        int overallScore = (skillsScore * 40 + experienceScore * 25 + availabilityScore * 20 + educationScore * 15) / 100;
        
        // Generate recommendation
        String recommendation = generateRecommendation(overallScore, skillsScore, experienceScore, availabilityScore, educationScore);
        
        // Find matched and missing skills
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        
        for (String jobSkill : jobSkills) {
            if (extractedData.getSkills().contains(jobSkill.toLowerCase())) {
                matchedSkills.add(jobSkill);
            } else {
                missingSkills.add(jobSkill);
            }
        }
        
        return new EnhancedMatchResult(overallScore, skillsScore, experienceScore, 
                                     availabilityScore, educationScore, matchedSkills, 
                                     missingSkills, recommendation, extractedData);
    }
    
    // Helper methods for data extraction
    private static String extractName(String text) {
        // Look for name patterns at the beginning of the resume
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 0 && line.length() < 50 && 
                !line.contains("@") && !line.contains("http") && 
                !line.contains("phone") && !line.contains("email")) {
                // Simple name validation
                if (line.matches("^[A-Z][a-z]+\\s+[A-Z][a-z]+.*")) {
                    return line.split("\\s+")[0] + " " + line.split("\\s+")[1];
                }
            }
        }
        return "Unknown";
    }
    
    private static String extractEmail(String text) {
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = emailPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    private static String extractPhone(String text) {
        Pattern phonePattern = Pattern.compile("(\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})");
        Matcher matcher = phonePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    private static String extractLocation(String text) {
        // Look for city, state patterns
        Pattern locationPattern = Pattern.compile("([A-Z][a-z]+,\\s*[A-Z]{2})");
        Matcher matcher = locationPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    private static String extractCurrentTitle(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            if (line.contains("experience") || line.contains("work") || line.contains("employment")) {
                // Look for title in next few lines
                for (int j = i + 1; j < Math.min(i + 5, lines.length); j++) {
                    String titleLine = lines[j].trim();
                    if (titleLine.length() > 0 && titleLine.length() < 100) {
                        return titleLine;
                    }
                }
            }
        }
        return "";
    }
    
    private static int extractYearsOfExperience(String text) {
        // Look for years of experience patterns
        Pattern expPattern = Pattern.compile("(\\d+)\\s*(?:years?|yrs?)\\s*(?:of\\s*)?experience", Pattern.CASE_INSENSITIVE);
        Matcher matcher = expPattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    
    private static List<String> extractSkills(String text) {
        List<String> skills = new ArrayList<>();
        String[] skillKeywords = {
            "java", "python", "javascript", "react", "angular", "vue", "node.js", "spring",
            "android", "ios", "swift", "kotlin", "sql", "mongodb", "mysql", "postgresql",
            "aws", "azure", "docker", "kubernetes", "jenkins", "git", "agile", "scrum",
            "rest", "api", "microservices", "machine learning", "ai", "data science",
            "frontend", "backend", "full stack", "devops", "ui", "ux", "design",
            "project management", "leadership", "communication", "team", "collaboration",
            "customer service", "sales", "marketing", "accounting", "finance", "hr",
            "retail", "inventory", "pos", "cash handling", "stock management"
        };
        
        String lowerText = text.toLowerCase();
        for (String skill : skillKeywords) {
            if (lowerText.contains(skill)) {
                skills.add(skill);
            }
        }
        
        return skills;
    }
    
    private static List<String> extractAvailability(String text) {
        List<String> availability = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("immediate") || lowerText.contains("available now")) {
            availability.add("Immediate");
        }
        if (lowerText.contains("part-time") || lowerText.contains("part time")) {
            availability.add("Part-time");
        }
        if (lowerText.contains("full-time") || lowerText.contains("full time")) {
            availability.add("Full-time");
        }
        if (lowerText.contains("weekend")) {
            availability.add("Weekend");
        }
        if (lowerText.contains("evening") || lowerText.contains("night")) {
            availability.add("Evening");
        }
        if (lowerText.contains("morning")) {
            availability.add("Morning");
        }
        if (lowerText.contains("remote") || lowerText.contains("work from home")) {
            availability.add("Remote");
        }
        
        return availability;
    }
    
    private static String extractEducation(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            if (line.contains("education") || line.contains("degree") || line.contains("university")) {
                // Look for education details in next few lines
                for (int j = i + 1; j < Math.min(i + 5, lines.length); j++) {
                    String eduLine = lines[j].trim();
                    if (eduLine.length() > 0 && eduLine.length() < 200) {
                        return eduLine;
                    }
                }
            }
        }
        return "";
    }
    
    private static String extractExpectedSalary(String text) {
        Pattern salaryPattern = Pattern.compile("\\$?([\\d,]+)\\s*(?:k|thousand|per year|annually)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = salaryPattern.matcher(text);
        if (matcher.find()) {
            return "$" + matcher.group(1) + "k";
        }
        return "";
    }
    
    // Job requirement extraction methods
    private static List<String> extractSkillsFromJob(String jobDescription) {
        return extractSkills(jobDescription);
    }
    
    private static List<String> extractAvailabilityFromJob(String jobDescription) {
        return extractAvailability(jobDescription);
    }
    
    private static int extractRequiredExperience(String jobDescription) {
        Pattern expPattern = Pattern.compile("(\\d+)\\s*(?:years?|yrs?)\\s*(?:of\\s*)?experience", Pattern.CASE_INSENSITIVE);
        Matcher matcher = expPattern.matcher(jobDescription);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    
    private static String extractRequiredEducation(String jobDescription) {
        String lowerDesc = jobDescription.toLowerCase();
        if (lowerDesc.contains("bachelor") || lowerDesc.contains("degree")) {
            return "Bachelor's Degree";
        } else if (lowerDesc.contains("high school") || lowerDesc.contains("diploma")) {
            return "High School Diploma";
        } else if (lowerDesc.contains("master")) {
            return "Master's Degree";
        }
        return "";
    }
    
    // Scoring methods
    private static int calculateSkillsScore(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills.isEmpty()) return 100;
        
        int matched = 0;
        for (String skill : requiredSkills) {
            if (candidateSkills.contains(skill.toLowerCase())) {
                matched++;
            }
        }
        
        return (matched * 100) / requiredSkills.size();
    }
    
    private static int calculateExperienceScore(int required, int candidate) {
        if (required == 0) return 100;
        if (candidate >= required) return 100;
        return Math.max(0, (candidate * 100) / required);
    }
    
    private static int calculateAvailabilityScore(List<String> required, List<String> candidate) {
        if (required.isEmpty()) return 100;
        
        int matched = 0;
        for (String avail : required) {
            if (candidate.contains(avail)) {
                matched++;
            }
        }
        
        return (matched * 100) / required.size();
    }
    
    private static int calculateEducationScore(String required, String candidate) {
        if (required.isEmpty()) return 100;
        if (candidate.toLowerCase().contains(required.toLowerCase())) {
            return 100;
        }
        return 50; // Partial match
    }
    
    private static String generateRecommendation(int overallScore, int skillsScore, int experienceScore, 
                                               int availabilityScore, int educationScore) {
        if (overallScore >= 90) {
            return "Excellent match! Strong candidate for immediate interview.";
        } else if (overallScore >= 75) {
            return "Good match. Consider for interview with some training.";
        } else if (overallScore >= 60) {
            return "Moderate match. May need additional training.";
        } else {
            return "Low match. Consider other candidates or different position.";
        }
    }
} 