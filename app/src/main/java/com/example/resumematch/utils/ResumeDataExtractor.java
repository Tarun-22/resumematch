package com.example.resumematch.utils;
//used gpt to write this huge static logic for extracting key words,
//but we are not using this code as we connected to ai api to get the data
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeDataExtractor {
    
    public static class ExtractedData {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String currentTitle;
        private int experienceYears;
        private String education;
        private List<String> skills;
        private String availability;
        private String availabilityDetails; // "Mon-Fri 9-5, Sat 10-3"
        private String transportation; // "Car", "Public Transit", "Walking"
        private String expectedSalary;
        private String startDate;
        private String workAuthorization; // "US Citizen", "Work Visa", etc.
        private String emergencyContact;
        private String emergencyPhone;
        private String references;
        private String previousRetailExperience;
        private String languages;
        private String certifications;
        private Map<String, Object> additionalInfo;
        
        public ExtractedData() {
            this.skills = new ArrayList<>();
            this.additionalInfo = new HashMap<>();
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getLocation() { return address; }
        public void setLocation(String location) { this.address = location; }
        
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
        
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        
        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }
        
        public String getAvailabilityDetails() { return availabilityDetails; }
        public void setAvailabilityDetails(String availabilityDetails) { this.availabilityDetails = availabilityDetails; }
        
        public String getTransportation() { return transportation; }
        public void setTransportation(String transportation) { this.transportation = transportation; }
        
        public String getExpectedSalary() { return expectedSalary; }
        public void setExpectedSalary(String expectedSalary) { this.expectedSalary = expectedSalary; }
        
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
        
        public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
        public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
        
        public String getFormattedAddress() {
            StringBuilder address = new StringBuilder();
            if (address != null && !address.isEmpty()) {
                address.append(this.address);
            }
            if (city != null && !city.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(city);
            }
            if (state != null && !state.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(state);
            }
            if (zipCode != null && !zipCode.isEmpty()) {
                if (address.length() > 0) address.append(" ");
                address.append(zipCode);
            }
            return address.toString();
        }
    }
    
    public static ExtractedData extractData(String resumeText) {
        ExtractedData data = new ExtractedData();
        
        try {
            data.setName(extractName(resumeText));
            data.setEmail(extractEmail(resumeText));
            data.setPhone(extractPhone(resumeText));
            data.setAddress(extractAddress(resumeText));
            data.setCity(extractCity(resumeText));
            data.setState(extractState(resumeText));
            data.setZipCode(extractzipcode(resumeText));
            data.setCurrentTitle(extractcurrenttitle(resumeText));
            data.setExperienceYears(extractexperienceyears(resumeText));
            data.setEducation(extracteducation(resumeText));
            data.setSkills(extractSkills(resumeText));
            data.setAvailability(extractavailability(resumeText));
            data.setAvailabilityDetails(extractavailabilitydetails(resumeText));
            data.setTransportation(extractTransportation(resumeText));
            data.setExpectedSalary(extractexpectedsalary(resumeText));
            data.setStartDate(extractstartdate(resumeText));
            data.setWorkAuthorization(extractworkauthorization(resumeText));
            data.setEmergencyContact(extractemergencycontact(resumeText));
            data.setEmergencyPhone(extractemergencyphone(resumeText));
            data.setReferences(extractreferences(resumeText));
            data.setPreviousRetailExperience(extractpreviousretailexperience(resumeText));
            data.setLanguages(extractlanguages(resumeText));
            data.setCertifications(extractcertifications(resumeText));
            
            Log.d("ResumeDataExtractor", "Extracted data: " + data.getName() + ", " + data.getEmail());
            
        } catch (Exception e) {
            Log.e("ResumeDataExtractor", "Error extracting data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    public interface EntityExtractionCallback {
        void onExtracted(ExtractedData data);
    }

    public static void extractDataWithMLKit(Context context, String resumeText, EntityExtractionCallback callback) {
        ExtractedData data = extractData(resumeText);

        postProcessExtractedData(data, resumeText);
        
        callback.onExtracted(data);
    }
    
    private static void postProcessExtractedData(ExtractedData data, String resumeText) {
        if (!data.getAddress().isEmpty()) {
            String[] addressParts = data.getAddress().split(",");
            if (addressParts.length >= 3) {
                data.setCity(addressParts[1].trim());
                String stateZip = addressParts[2].trim();
                String[] stateZipParts = stateZip.split("\\s+");
                if (stateZipParts.length >= 2) {
                    data.setState(stateZipParts[0]);
                    data.setZipCode(stateZipParts[1]);
                }
            }
        }
        
        if (data.getAvailabilityDetails().isEmpty()) {
            data.setAvailabilityDetails(extractavailabilitydetails(resumeText));
        }
        
        if (data.getTransportation().isEmpty()) {
            data.setTransportation(extractTransportation(resumeText));
        }
        
        if (data.getWorkAuthorization().isEmpty()) {
            data.setWorkAuthorization(extractworkauthorization(resumeText));
        }
        
        if (data.getEmergencyContact().isEmpty()) {
            data.setEmergencyContact(extractemergencycontact(resumeText));
        }
        
        if (data.getReferences().isEmpty()) {
            data.setReferences(extractreferences(resumeText));
        }
        
        if (data.getPreviousRetailExperience().isEmpty()) {
            data.setPreviousRetailExperience(extractpreviousretailexperience(resumeText));
        }
        
        if (data.getLanguages().isEmpty()) {
            data.setLanguages(extractlanguages(resumeText));
        }
        
        if (data.getCertifications().isEmpty()) {
            data.setCertifications(extractcertifications(resumeText));
        }
    }
    
    private static String extractName(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 0 && line.length() < 50) {
                if (line.matches("^[A-Za-z\\s]+$") && line.split("\\s+").length >= 2) {
                    return line;
                }
            }
        }
        return "Unknown";
    }
    
    private static String extractEmail(String text) {
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = emailPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    private static String extractPhone(String text) {
        Pattern phonePattern = Pattern.compile("\\b(\\+?1[-.]?)?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})\\b");
        Matcher matcher = phonePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    private static String extractAddress(String text) {
        Pattern addressPattern = Pattern.compile("\\d+\\s+[A-Za-z\\s]+(?:Street|St|Avenue|Ave|Road|Rd|Boulevard|Blvd|Drive|Dr|Lane|Ln|Court|Ct|Place|Pl|Way|Terrace|Ter|Circle|Cir|Trail|Trl)\\s*,\\s*[A-Za-z\\s]+,\\s*[A-Z]{2}\\s*\\d{5}(?:-\\d{4})?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = addressPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        Pattern simpleAddressPattern = Pattern.compile("\\d+\\s+[A-Za-z\\s]+(?:Street|St|Avenue|Ave|Road|Rd|Boulevard|Blvd|Drive|Dr|Lane|Ln|Court|Ct|Place|Pl|Way|Terrace|Ter|Circle|Cir|Trail|Trl)", Pattern.CASE_INSENSITIVE);
        Matcher simpleMatcher = simpleAddressPattern.matcher(text);
        if (simpleMatcher.find()) {
            return simpleMatcher.group();
        }
        
        return "";
    }
    
    private static String extractCity(String text) {
        Pattern cityPattern = Pattern.compile("\\b[A-Za-z\\s]+(?:City|Town|Village|Borough|County)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(",")) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String potentialCity = parts[1].trim();
                    if (potentialCity.length() > 0 && potentialCity.length() < 50) {
                        return potentialCity;
                    }
                }
            }
        }
        
        return "";
    }
    
    private static String extractState(String text) {
        Pattern statePattern = Pattern.compile("\\b[A-Z]{2}\\b");
        Matcher matcher = statePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        return "";
    }
    
    private static String extractzipcode(String text) {
        Pattern zipPattern = Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");
        Matcher matcher = zipPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        return "";
    }
    
    private static String extractcurrenttitle(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.toLowerCase().contains("experience") || line.toLowerCase().contains("work")) {
                for (int j = i + 1; j < Math.min(i + 5, lines.length); j++) {
                    String nextLine = lines[j].trim();
                    if (nextLine.length() > 0 && nextLine.length() < 100) {
                        if (nextLine.contains("-") || nextLine.contains("at") ||
                            nextLine.toLowerCase().contains("engineer") || 
                            nextLine.toLowerCase().contains("developer") ||
                            nextLine.toLowerCase().contains("manager") ||
                            nextLine.toLowerCase().contains("associate")) {
                            return nextLine;
                        }
                    }
                }
            }
        }
        return "";
    }
    
    private static int extractexperienceyears(String text) {
        Pattern expPattern = Pattern.compile("(\\d+)\\s*(?:years?|yrs?)\\s*(?:of\\s*)?experience", Pattern.CASE_INSENSITIVE);
        Matcher matcher = expPattern.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    private static String extracteducation(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.toLowerCase().contains("education")) {
                StringBuilder education = new StringBuilder();
                for (int j = i + 1; j < Math.min(i + 5, lines.length); j++) {
                    String nextLine = lines[j].trim();
                    if (nextLine.length() > 0 && !nextLine.toLowerCase().contains("experience")) {
                        education.append(nextLine).append(" ");
                    }
                }
                return education.toString().trim();
            }
        }
        return "";
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
            "customer service", "sales", "marketing", "analytics", "excel", "powerpoint",
            "word", "photoshop", "illustrator", "inventory", "pos", "cash handling"
        };
        
        String lowerText = text.toLowerCase();
        for (String skill : skillKeywords) {
            if (lowerText.contains(skill)) {
                skills.add(skill);
            }
        }
        
        return skills;
    }
    
    private static String extractavailability(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("immediate") || lowerText.contains("available now")) {
            return "Immediate";
        } else if (lowerText.contains("2 weeks") || lowerText.contains("two weeks")) {
            return "2 weeks notice";
        } else if (lowerText.contains("1 month") || lowerText.contains("one month")) {
            return "1 month notice";
        } else if (lowerText.contains("flexible") || lowerText.contains("negotiable")) {
            return "Flexible";
        }
        
        return "Not specified";
    }
    
    private static String extractavailabilitydetails(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("monday") || lowerText.contains("tuesday") || lowerText.contains("wednesday") ||
            lowerText.contains("thursday") || lowerText.contains("friday") || lowerText.contains("saturday") || 
            lowerText.contains("sunday")) {
            
            String[] lines = text.split("\n");
            for (String line : lines) {
                String lowerLine = line.toLowerCase();
                if (lowerLine.contains("available") || lowerLine.contains("schedule") || 
                    lowerLine.contains("monday") || lowerLine.contains("tuesday") || 
                    lowerLine.contains("wednesday") || lowerLine.contains("thursday") || 
                    lowerLine.contains("friday") || lowerLine.contains("saturday") || 
                    lowerLine.contains("sunday")) {
                    return line.trim();
                }
            }
        }
        
        return "";
    }
    
    private static String extractTransportation(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("car") || lowerText.contains("vehicle") || lowerText.contains("driving")) {
            return "Car";
        } else if (lowerText.contains("public transit") || lowerText.contains("bus") || lowerText.contains("train") || lowerText.contains("subway")) {
            return "Public Transit";
        } else if (lowerText.contains("walking") || lowerText.contains("walk")) {
            return "Walking";
        } else if (lowerText.contains("bike") || lowerText.contains("cycling")) {
            return "Bicycle";
        }
        
        return "";
    }
    
    private static String extractexpectedsalary(String text) {
        Pattern salaryPattern = Pattern.compile("\\$?(\\d{1,3}(?:,\\d{3})*(?:k)?)\\s*-\\s*\\$?(\\d{1,3}(?:,\\d{3})*(?:k)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = salaryPattern.matcher(text);
        if (matcher.find()) {
            return "$" + matcher.group(1) + " - $" + matcher.group(2);
        }
        
        Pattern singleSalaryPattern = Pattern.compile("\\$?(\\d{1,3}(?:,\\d{3})*(?:k)?)", Pattern.CASE_INSENSITIVE);
        Matcher singleMatcher = singleSalaryPattern.matcher(text);
        if (singleMatcher.find()) {
            return "$" + singleMatcher.group(1);
        }
        
        return "";
    }
    
    private static String extractstartdate(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("immediate") || lowerText.contains("available now")) {
            return "Immediate";
        } else if (lowerText.contains("2 weeks") || lowerText.contains("two weeks")) {
            return "2 weeks notice";
        } else if (lowerText.contains("1 month") || lowerText.contains("one month")) {
            return "1 month notice";
        } else if (lowerText.contains("flexible") || lowerText.contains("negotiable")) {
            return "Flexible";
        }
        
        Pattern datePattern = Pattern.compile("\\b(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{1,2},?\\s+\\d{4}\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        return "";
    }
    
    private static String extractworkauthorization(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("us citizen") || lowerText.contains("citizen")) {
            return "US Citizen";
        } else if (lowerText.contains("work visa") || lowerText.contains("h1b") || lowerText.contains("visa")) {
            return "Work Visa";
        } else if (lowerText.contains("green card") || lowerText.contains("permanent resident")) {
            return "Permanent Resident";
        } else if (lowerText.contains("daca") || lowerText.contains("dreamer")) {
            return "DACA";
        }
        
        return "";
    }
    
    private static String extractemergencycontact(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            if (line.contains("emergency") || line.contains("contact") || line.contains("reference")) {
                for (int j = i + 1; j < Math.min(i + 3, lines.length); j++) {
                    String nextLine = lines[j].trim();
                    if (nextLine.length() > 0 && nextLine.length() < 100) {
                        if (nextLine.matches("^[A-Za-z\\s]+$") && nextLine.split("\\s+").length >= 2) {
                            return nextLine;
                        }
                    }
                }
            }
        }
        
        return "";
    }
    
    private static String extractemergencyphone(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            if (line.contains("emergency") || line.contains("contact")) {
                for (int j = i + 1; j < Math.min(i + 3, lines.length); j++) {
                    String nextLine = lines[j].trim();
                    Pattern phonePattern = Pattern.compile("\\b(\\+?1[-.]?)?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})\\b");
                    Matcher matcher = phonePattern.matcher(nextLine);
                    if (matcher.find()) {
                        return matcher.group();
                    }
                }
            }
        }
        
        return "";
    }
    
    private static String extractreferences(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            if (line.contains("reference") || line.contains("referee")) {
                StringBuilder references = new StringBuilder();
                for (int j = i + 1; j < Math.min(i + 6, lines.length); j++) {
                    String nextLine = lines[j].trim();
                    if (nextLine.length() > 0 && !nextLine.toLowerCase().contains("experience")) {
                        references.append(nextLine).append("\n");
                    }
                }
                return references.toString().trim();
            }
        }
        
        return "";
    }
    
    private static String extractpreviousretailexperience(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("retail") || lowerText.contains("cashier") || lowerText.contains("sales") ||
            lowerText.contains("customer service") || lowerText.contains("store") || lowerText.contains("shop")) {
            
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].toLowerCase();
                if (line.contains("retail") || line.contains("cashier") || line.contains("sales") || 
                    line.contains("customer service") || line.contains("store") || line.contains("shop")) {
                    
                    StringBuilder experience = new StringBuilder();
                    for (int j = i; j < Math.min(i + 4, lines.length); j++) {
                        String nextLine = lines[j].trim();
                        if (nextLine.length() > 0) {
                            experience.append(nextLine).append("\n");
                        }
                    }
                    return experience.toString().trim();
                }
            }
        }
        
        return "";
    }
    
    private static String extractlanguages(String text) {
        String[] languages = {"english", "spanish", "french", "german", "italian", "portuguese", "chinese", "japanese", "korean", "arabic", "russian", "hindi"};
        List<String> foundLanguages = new ArrayList<>();
        
        String lowerText = text.toLowerCase();
        for (String language : languages) {
            if (lowerText.contains(language)) {
                foundLanguages.add(language.substring(0, 1).toUpperCase() + language.substring(1));
            }
        }
        
        if (!foundLanguages.isEmpty()) {
            return String.join(", ", foundLanguages);
        }
        
        return "";
    }
    
    private static String extractcertifications(String text) {
        String[] certifications = {"food handler", "servsafe", "first aid", "cpr", "customer service", "sales", "management"};
        List<String> foundCertifications = new ArrayList<>();
        
        String lowerText = text.toLowerCase();
        for (String cert : certifications) {
            if (lowerText.contains(cert)) {
                foundCertifications.add(cert.substring(0, 1).toUpperCase() + cert.substring(1));
            }
        }
        
        if (!foundCertifications.isEmpty()) {
            return String.join(", ", foundCertifications);
        }
        
        return "";
    }
} 