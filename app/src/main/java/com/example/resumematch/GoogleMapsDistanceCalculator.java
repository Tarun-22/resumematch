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
    
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    
    public interface DistanceCallback {
        void onDistanceCalculated(double distance, String duration, String distanceText);
        void onError(String error);
    }
    
    /**
     * Calculate distance between two addresses using Google Maps Distance Matrix API
     */
    public static void calculateDistance(Context context, String originAddress, String destinationAddress, DistanceCallback callback) {
        try {
            // Get API key from Config
            String apiKey = Config.getGoogleMapsApiKey();
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-google-maps-api-key-here")) {
                Log.w("GoogleMapsDistance", "Invalid Google Maps API key, using fallback");
                calculateDistanceFallback(originAddress, destinationAddress, callback);
                return;
            }
            
            // Build the URL for Google Maps Distance Matrix API
            String urlString = DISTANCE_MATRIX_URL + "?" +
                "origins=" + URLEncoder.encode(originAddress, "UTF-8") +
                "&destinations=" + URLEncoder.encode(destinationAddress, "UTF-8") +
                "&units=imperial" +
                "&key=" + apiKey;
            
            Log.d("GoogleMapsDistance", "Making request to: " + urlString);
            
            // Make the API call
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            Log.d("GoogleMapsDistance", "Response code: " + responseCode);
            
            if (responseCode == 200) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                Log.d("GoogleMapsDistance", "Response: " + response.toString());
                
                // Parse the response
                parseDistanceResponse(response.toString(), callback);
                
            } else {
                Log.e("GoogleMapsDistance", "API error: " + responseCode);
                // Fallback to simplified calculation
                calculateDistanceFallback(originAddress, destinationAddress, callback);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error calculating distance: " + e.getMessage());
            // Fallback to simplified calculation
            calculateDistanceFallback(originAddress, destinationAddress, callback);
        }
    }
    
    private static void parseDistanceResponse(String responseBody, DistanceCallback callback) {
        try {
            JSONObject response = new JSONObject(responseBody);
            
            if (response.getString("status").equals("OK")) {
                JSONArray rows = response.getJSONArray("rows");
                if (rows.length() > 0) {
                    JSONObject row = rows.getJSONObject(0);
                    JSONArray elements = row.getJSONArray("elements");
                    if (elements.length() > 0) {
                        JSONObject element = elements.getJSONObject(0);
                        
                        if (element.getString("status").equals("OK")) {
                            JSONObject distance = element.getJSONObject("distance");
                            JSONObject duration = element.getJSONObject("duration");
                            
                            // Convert meters to miles
                            double distanceInMeters = distance.getDouble("value");
                            double distanceInMiles = distanceInMeters * 0.000621371; // Convert meters to miles
                            
                            String distanceText = distance.getString("text");
                            String durationText = duration.getString("text");
                            
                            Log.d("GoogleMapsDistance", "Distance: " + distanceInMiles + " miles, Duration: " + durationText);
                            callback.onDistanceCalculated(distanceInMiles, durationText, distanceText);
                            return;
                        }
                    }
                }
            }
            
            // If we get here, there was an error in the response
            Log.e("GoogleMapsDistance", "Invalid response: " + responseBody);
            callback.onError("Invalid response from Google Maps API");
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error parsing response: " + e.getMessage());
            callback.onError("Error parsing Google Maps response: " + e.getMessage());
        }
    }
    
    /**
     * Fallback distance calculation using simplified geocoding
     * Used when Google Maps API is not available
     */
    private static void calculateDistanceFallback(String originAddress, String destinationAddress, DistanceCallback callback) {
        try {
            // This is a simplified version - in reality you would:
            // 1. Use Google Geocoding API to convert addresses to coordinates
            // 2. Use Google Distance Matrix API to get actual driving distance
            
            // For now, we'll use a simple estimation based on ZIP codes
            String originZip = extractZipCode(originAddress);
            String destZip = extractZipCode(destinationAddress);
            
            if (originZip != null && destZip != null) {
                // Simple distance estimation based on ZIP code patterns
                double distance = estimateDistanceFromZipCodes(originZip, destZip);
                String duration = "30 mins"; // Placeholder
                String distanceText = String.format("%.1f miles", distance);
                
                Log.d("GoogleMapsDistance", "Fallback distance: " + distance + " miles");
                callback.onDistanceCalculated(distance, duration, distanceText);
            } else {
                // Fallback to a default distance
                double distance = 15.0; // Default 15 miles
                String duration = "30 mins";
                String distanceText = String.format("%.1f miles", distance);
                
                Log.d("GoogleMapsDistance", "Default fallback distance: " + distance + " miles");
                callback.onDistanceCalculated(distance, duration, distanceText);
            }
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error in fallback calculation: " + e.getMessage());
            callback.onError("Error in fallback distance calculation: " + e.getMessage());
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
     * Returns score out of 25 points (as per new scoring system)
     */
    public static int calculateDistanceScore(double distance) {
        if (distance <= 5) {
            return 25; // Excellent - within 5 miles (25/25 points)
        } else if (distance <= 10) {
            return 22; // Very good - within 10 miles (22/25 points)
        } else if (distance <= 15) {
            return 20; // Good - within 15 miles (20/25 points)
        } else if (distance <= 25) {
            return 17; // Acceptable - within 25 miles (17/25 points)
        } else if (distance <= 35) {
            return 12; // Moderate - within 35 miles (12/25 points)
        } else if (distance <= 50) {
            return 8; // Poor - within 50 miles (8/25 points)
        } else {
            return 3; // Very poor - over 50 miles (3/25 points)
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