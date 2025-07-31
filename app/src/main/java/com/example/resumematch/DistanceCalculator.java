package com.example.resumematch;

import android.util.Log;

public class DistanceCalculator {
    
    // Earth's radius in miles
    private static final double EARTH_RADIUS = 3959.0;
    
    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in miles
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * Calculate distance score based on distance
     * @param distance Distance in miles
     * @return Score from 0-100 (100 = very close, 0 = very far)
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
     * @param distance Distance in miles
     * @return Human readable description
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
    
    /**
     * Convert address to coordinates (simplified - in real app, use Geocoding API)
     * This is a placeholder - you would typically use Google Geocoding API
     */
    public static double[] getCoordinatesFromAddress(String address) {
        // This is a simplified version
        // In a real implementation, you would use Google Geocoding API
        // For now, return default coordinates (you can manually set these)
        Log.d("DistanceCalculator", "Address to coordinates: " + address);
        
        // Example coordinates (you can modify these)
        return new double[]{37.7749, -122.4194}; // Default to San Francisco
    }
} 