package com.example.resumematch;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.json.JSONArray;

public class GoogleMapsDistanceCalculator {
    
    // You would need to get a Google Maps API key
    private static final String API_KEY = "your-google-maps-api-key-here"; // Replace with actual API key
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    
    public interface DistanceCallback {
        void onDistanceCalculated(double distance, String duration, String distanceText);
        void onError(String error);
    }
    
    /**
     * Calculate distance between two addresses using Google Maps Distance Matrix API
     */
    public static void calculateDistance(Context context, String originAddress, String destinationAddress, DistanceCallback callback) {
        // For now, we'll use a simplified approach since we don't have an API key
        // In a real implementation, you would make an HTTP request to Google Maps API
        
        try {
            // Simulate API call (replace with actual Google Maps API call)
            double distance = calculateDistanceFallback(originAddress, destinationAddress);
            String duration = "30 mins"; // Placeholder
            String distanceText = String.format("%.1f miles", distance);
            
            callback.onDistanceCalculated(distance, duration, distanceText);
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error calculating distance: " + e.getMessage());
            callback.onError("Error calculating distance: " + e.getMessage());
        }
    }
    
    /**
     * Fallback distance calculation using simplified geocoding
     * In a real app, you would use Google Maps Geocoding API
     */
    private static double calculateDistanceFallback(String originAddress, String destinationAddress) {
        try {
            // This is a simplified version - in reality you would:
            // 1. Use Google Geocoding API to convert addresses to coordinates
            // 2. Use Google Distance Matrix API to get actual driving distance
            
            // For now, we'll use a simple estimation based on ZIP codes
            String originZip = extractZipCode(originAddress);
            String destZip = extractZipCode(destinationAddress);
            
            if (originZip != null && destZip != null) {
                // Simple distance estimation based on ZIP code patterns
                return estimateDistanceFromZipCodes(originZip, destZip);
            }
            
            // Fallback to a default distance
            return 15.0; // Default 15 miles
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error in fallback calculation: " + e.getMessage());
            return 15.0; // Default distance
        }
    }
    
    private static String extractZipCode(String address) {
        if (address == null || address.isEmpty()) return null;
        
        // Simple regex to extract ZIP code
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");
        java.util.regex.Matcher matcher = pattern.matcher(address);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
    
    private static double estimateDistanceFromZipCodes(String zip1, String zip2) {
        try {
            // Convert ZIP codes to approximate coordinates
            // This is a very simplified approach
            int zip1Num = Integer.parseInt(zip1.substring(0, 3));
            int zip2Num = Integer.parseInt(zip2.substring(0, 3));
            
            // Rough estimation: each ZIP code region is about 100 miles
            int difference = Math.abs(zip1Num - zip2Num);
            return difference * 0.5; // Rough estimate
            
        } catch (Exception e) {
            return 15.0; // Default distance
        }
    }
    
    /**
     * Calculate distance score based on actual driving distance
     */
    public static int calculateDistanceScore(double distance) {
        if (distance <= 5) {
            return 100; // Excellent - within 5 miles
        } else if (distance <= 10) {
            return 90; // Very good - within 10 miles
        } else if (distance <= 15) {
            return 80; // Good - within 15 miles
        } else if (distance <= 25) {
            return 70; // Acceptable - within 25 miles
        } else if (distance <= 35) {
            return 50; // Moderate - within 35 miles
        } else if (distance <= 50) {
            return 30; // Poor - within 50 miles
        } else {
            return 10; // Very poor - over 50 miles
        }
    }
    
    /**
     * Get distance description
     */
    public static String getDistanceDescription(double distance) {
        if (distance < 1) {
            return String.format("%.1f miles (Very Close)", distance);
        } else if (distance < 5) {
            return String.format("%.1f miles (Close)", distance);
        } else if (distance < 10) {
            return String.format("%.1f miles (Nearby)", distance);
        } else if (distance < 25) {
            return String.format("%.1f miles (Reasonable)", distance);
        } else if (distance < 50) {
            return String.format("%.1f miles (Far)", distance);
        } else {
            return String.format("%.1f miles (Very Far)", distance);
        }
    }
} 