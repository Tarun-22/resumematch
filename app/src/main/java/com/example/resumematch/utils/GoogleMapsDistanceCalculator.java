package com.example.resumematch.utils;

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
        void distancecalulate(double distance, String duration, String distanceText);
        void onError(String error);
    }
    

    public static void calculateDistance(Context context, String originAddress, String destinationAddress, DistanceCallback callback) {
        try {
            String apiKey = Config.getGoogleMapsApiKey();
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-google-maps-api-key-here")) {
                Log.w("GoogleMapsDistance", "Invalid Google Maps API key, using fallback");
                calculateDistanceFallback(originAddress, destinationAddress, callback);
                return;
            }
            
            String string_url = DISTANCE_MATRIX_URL + "?" +
                "origins=" + URLEncoder.encode(originAddress, "UTF-8") +
                "&destinations=" + URLEncoder.encode(destinationAddress, "UTF-8") +
                "&units=imperial" +
                "&key=" + apiKey;
            
            Log.d("GoogleMapsDistance", "Making request to: " + string_url);
            
            URL url = new URL(string_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            Log.d("GoogleMapsDistance", "Response code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                Log.d("GoogleMapsDistance", "Response: " + response.toString());
                
                distanceresponseparse(response.toString(), callback);
                
            } else {
                Log.e("GoogleMapsDistance", "API error: " + responseCode);
                calculateDistanceFallback(originAddress, destinationAddress, callback);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error calculating distance: " + e.getMessage());
            calculateDistanceFallback(originAddress, destinationAddress, callback);
        }
    }
    
    private static void distanceresponseparse(String responseBody, DistanceCallback callback) {
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
                            callback.distancecalulate(distanceInMiles, durationText, distanceText);
                            return;
                        }
                    }
                }
            }
            
            Log.e("GoogleMapsDistance", "Invalid response: " + responseBody);
            callback.onError("Invalid response from Google Maps API");
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error parsing response: " + e.getMessage());
            callback.onError("Error parsing Google Maps response: " + e.getMessage());
        }
    }
    

    private static void calculateDistanceFallback(String originAddress, String destinationAddress, DistanceCallback callback) {
        try {

            String originZip = zipcodes(originAddress);
            String destZip = zipcodes(destinationAddress);
            
            if (originZip != null && destZip != null) {
                double distance = estimatefromzip(originZip, destZip);
                String duration = "30 mins";
                String distanceText = String.format("%.1f miles", distance);
                
                Log.d("GoogleMapsDistance", "Fallback distance: " + distance + " miles");
                callback.distancecalulate(distance, duration, distanceText);
            } else {

                double distance = 15.0; // Default 15 miles
                String duration = "30 mins";
                String distanceText = String.format("%.1f miles", distance);
                
                Log.d("GoogleMapsDistance", "Default fallback distance: " + distance + " miles");
                callback.distancecalulate(distance, duration, distanceText);
            }
            
        } catch (Exception e) {
            Log.e("GoogleMapsDistance", "Error in fallback calculation: " + e.getMessage());
            callback.onError("Error in fallback distance calculation: " + e.getMessage());
        }
    }
    
    private static String zipcodes(String address) {
        if (address == null || address.isEmpty()) return null;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");
        java.util.regex.Matcher matcher = pattern.matcher(address);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
    
    private static double estimatefromzip(String zip1, String zip2) {
        try {

            int zip1Num = Integer.parseInt(zip1.substring(0, 3));
            int zip2Num = Integer.parseInt(zip2.substring(0, 3));
            
            int difference = Math.abs(zip1Num - zip2Num);
            return difference * 0.5;
            
        } catch (Exception e) {
            return 15.0;
        }
    }

    public static int calculateDistanceScore(double distance) {
        if (distance <= 5) {
            return 25;
        } else if (distance <= 10) {
            return 22;
        } else if (distance <= 15) {
            return 20;
        } else if (distance <= 25) {
            return 17;
        } else if (distance <= 35) {
            return 12;
        } else if (distance <= 50) {
            return 8;
        } else {
            return 3;
        }
    }

    public static String getDes(double distance) {
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