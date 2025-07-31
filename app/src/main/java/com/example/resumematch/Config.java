package com.example.resumematch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    
    private static final String PREF_NAME = "app_config";
    private static final String KEY_OPENAI_API_KEY = "openai_api_key";
    private static final String KEY_GOOGLE_MAPS_API_KEY = "google_maps_api_key";
    
    // Default API keys (fallback if env file not found)
    private static final String DEFAULT_OPENAI_API_KEY = "your-openai-api-key-here";
    private static final String DEFAULT_GOOGLE_MAPS_API_KEY = "your-google-maps-api-key-here";
    
    private static SharedPreferences preferences;
    private static Properties envProperties;
    
    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadEnvironmentFile(context);
    }
    
    private static void loadEnvironmentFile(Context context) {
        try {
            envProperties = new Properties();
            InputStream inputStream = context.getAssets().open("env.properties");
            envProperties.load(inputStream);
            inputStream.close();
            Log.d("Config", "Environment file loaded successfully");
        } catch (IOException e) {
            Log.w("Config", "Could not load env.properties file: " + e.getMessage());
            envProperties = null;
        }
    }
    
    public static String getOpenAIApiKey() {
        // First try to get from SharedPreferences (user configured)
        if (preferences != null) {
            String userKey = preferences.getString(KEY_OPENAI_API_KEY, null);
            if (userKey != null && !userKey.equals(DEFAULT_OPENAI_API_KEY)) {
                Log.d("Config", "Using OpenAI API key from SharedPreferences");
                return userKey;
            }
        }
        
        // Then try to get from environment file (development)
        if (envProperties != null) {
            String envKey = envProperties.getProperty("OPENAI_API_KEY");
            Log.d("Config", "Environment file loaded, checking OpenAI key: " + (envKey != null ? "found" : "not found"));
            if (envKey != null && !envKey.equals("your-openai-api-key-here")) {
                Log.d("Config", "Using OpenAI API key from environment file");
                return envKey;
            } else {
                Log.w("Config", "OpenAI API key in environment file is placeholder or invalid");
            }
        } else {
            Log.w("Config", "Environment properties not loaded");
        }
        
        // Fallback to default
        Log.w("Config", "Using default OpenAI API key. Please configure your API key.");
        return DEFAULT_OPENAI_API_KEY;
    }
    
    public static String getGoogleMapsApiKey() {
        // First try to get from SharedPreferences (user configured)
        if (preferences != null) {
            String userKey = preferences.getString(KEY_GOOGLE_MAPS_API_KEY, null);
            if (userKey != null && !userKey.equals(DEFAULT_GOOGLE_MAPS_API_KEY)) {
                Log.d("Config", "Using Google Maps API key from SharedPreferences");
                return userKey;
            }
        }
        
        // Then try to get from environment file (development)
        if (envProperties != null) {
            String envKey = envProperties.getProperty("GOOGLE_MAPS_API_KEY");
            Log.d("Config", "Environment file loaded, checking Google Maps key: " + (envKey != null ? "found" : "not found"));
            if (envKey != null && !envKey.equals("your-google-maps-api-key-here")) {
                Log.d("Config", "Using Google Maps API key from environment file");
                return envKey;
            } else {
                Log.w("Config", "Google Maps API key in environment file is placeholder or invalid");
            }
        } else {
            Log.w("Config", "Environment properties not loaded");
        }
        
        // Fallback to default
        Log.w("Config", "Using default Google Maps API key. Please configure your API key.");
        return DEFAULT_GOOGLE_MAPS_API_KEY;
    }
    
    public static void setOpenAIApiKey(String apiKey) {
        if (preferences != null) {
            preferences.edit().putString(KEY_OPENAI_API_KEY, apiKey).apply();
            Log.d("Config", "OpenAI API key updated in SharedPreferences");
        }
    }
    
    public static void setGoogleMapsApiKey(String apiKey) {
        if (preferences != null) {
            preferences.edit().putString(KEY_GOOGLE_MAPS_API_KEY, apiKey).apply();
            Log.d("Config", "Google Maps API key updated in SharedPreferences");
        }
    }
    
    public static boolean isApiKeyConfigured() {
        String apiKey = getOpenAIApiKey();
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals(DEFAULT_OPENAI_API_KEY);
    }
    
    public static boolean isDevelopmentMode() {
        if (envProperties != null) {
            String devMode = envProperties.getProperty("DEV_MODE", "false");
            return "true".equalsIgnoreCase(devMode);
        }
        return false;
    }
    
    public static String getApiKeySource() {
        if (preferences != null) {
            String userKey = preferences.getString(KEY_OPENAI_API_KEY, null);
            if (userKey != null && !userKey.equals(DEFAULT_OPENAI_API_KEY)) {
                return "User Configuration";
            }
        }
        
        if (envProperties != null) {
            String envKey = envProperties.getProperty("OPENAI_API_KEY");
            if (envKey != null && !envKey.equals("your-openai-api-key-here")) {
                return "Environment File";
            }
        }
        
        return "Default (Not Configured)";
    }
} 