package com.example.resumematch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Config {
    
    private static final String PREF_NAME = "app_config";
    private static final String KEY_OPENAI_API_KEY = "openai_api_key";
    private static final String KEY_GOOGLE_MAPS_API_KEY = "google_maps_api_key";
    
    // Default API keys (these should be replaced with your actual keys)
    private static final String DEFAULT_OPENAI_API_KEY = "your-openai-api-key-here";
    private static final String DEFAULT_GOOGLE_MAPS_API_KEY = "your-google-maps-api-key-here";
    
    private static SharedPreferences preferences;
    
    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static String getOpenAIApiKey() {
        if (preferences == null) {
            Log.e("Config", "Config not initialized. Call Config.init() first.");
            return DEFAULT_OPENAI_API_KEY;
        }
        
        String apiKey = preferences.getString(KEY_OPENAI_API_KEY, DEFAULT_OPENAI_API_KEY);
        
        // Check if it's the default placeholder
        if (apiKey.equals(DEFAULT_OPENAI_API_KEY)) {
            Log.w("Config", "Using default OpenAI API key. Please set your actual API key.");
        }
        
        return apiKey;
    }
    
    public static String getGoogleMapsApiKey() {
        if (preferences == null) {
            Log.e("Config", "Config not initialized. Call Config.init() first.");
            return DEFAULT_GOOGLE_MAPS_API_KEY;
        }
        
        return preferences.getString(KEY_GOOGLE_MAPS_API_KEY, DEFAULT_GOOGLE_MAPS_API_KEY);
    }
    
    public static void setOpenAIApiKey(String apiKey) {
        if (preferences != null) {
            preferences.edit().putString(KEY_OPENAI_API_KEY, apiKey).apply();
            Log.d("Config", "OpenAI API key updated");
        }
    }
    
    public static void setGoogleMapsApiKey(String apiKey) {
        if (preferences != null) {
            preferences.edit().putString(KEY_GOOGLE_MAPS_API_KEY, apiKey).apply();
            Log.d("Config", "Google Maps API key updated");
        }
    }
    
    public static boolean isApiKeyConfigured() {
        String apiKey = getOpenAIApiKey();
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals(DEFAULT_OPENAI_API_KEY);
    }
} 