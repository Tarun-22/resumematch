package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.StoreProfile;

import java.util.UUID;

public class StoreProfileActivity extends AppCompatActivity {
    
    private EditText editTextStoreName, editTextStoreAddress, editTextStoreCity,
                     editTextStoreState, editTextStoreZipCode, editTextStorePhone, 
                     editTextStoreEmail, editTextStoreDescription;
    private Button buttonSave, buttonCancel;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        
        dataRepository = new DataRepository(this);
        
        editTextStoreName = findViewById(R.id.editTextStoreName);
        editTextStoreAddress = findViewById(R.id.editTextStoreAddress);
        editTextStoreCity = findViewById(R.id.editTextStoreCity);
        editTextStoreState = findViewById(R.id.editTextStoreState);
        editTextStoreZipCode = findViewById(R.id.editTextStoreZipCode);
        editTextStorePhone = findViewById(R.id.editTextStorePhone);
        editTextStoreEmail = findViewById(R.id.editTextStoreEmail);
        editTextStoreDescription = findViewById(R.id.editTextStoreDescription);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        ImageView backButton = findViewById(R.id.backButton);
        
        backButton.setOnClickListener(v -> finish());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSave.setOnClickListener(v -> saveStoreProfile());
        
        loadExistingStoreProfile();
    }
    
    private void loadExistingStoreProfile() {
        dataRepository.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
            @Override
            public void onResult(StoreProfile storeProfile) {
                if (storeProfile != null) {
                    runOnUiThread(() -> {
                        editTextStoreName.setText(storeProfile.getStoreName());
                        editTextStoreAddress.setText(storeProfile.getStoreAddress());
                        editTextStoreCity.setText(storeProfile.getStoreCity());
                        editTextStoreState.setText(storeProfile.getStoreState());
                        editTextStoreZipCode.setText(storeProfile.getStoreZipCode());
                        editTextStorePhone.setText(storeProfile.getPhone());
                        editTextStoreEmail.setText(storeProfile.getEmail());
                        editTextStoreDescription.setText(storeProfile.getDescription());
                    });
                }
            }
        });
    }
    
    private void saveStoreProfile() {
        String storeName = editTextStoreName.getText().toString().trim();
        String storeAddress = editTextStoreAddress.getText().toString().trim();
        String storeCity = editTextStoreCity.getText().toString().trim();
        String storeState = editTextStoreState.getText().toString().trim();
        String storeZipCode = editTextStoreZipCode.getText().toString().trim();
        String phone = editTextStorePhone.getText().toString().trim();
        String email = editTextStoreEmail.getText().toString().trim();
        String description = editTextStoreDescription.getText().toString().trim();
        
        if (storeName.isEmpty() || storeAddress.isEmpty() || storeCity.isEmpty() ||
            storeState.isEmpty() || storeZipCode.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String storeId = "STORE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        StoreProfile storeProfile = new StoreProfile(
            storeId, storeName, storeAddress, storeCity, storeState, storeZipCode,
            phone, email, description
        );
        
        Snackbar.make(buttonSave, "Saving store profile...", Snackbar.LENGTH_SHORT).show();
        
        dataRepository.insertStore(storeProfile, new DataRepository.DatabaseCallback<Void>() {
            @Override
            public void onResult(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(StoreProfileActivity.this, "Store profile saved successfully!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(buttonSave, "Store profile updated!", Snackbar.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepository != null) {
            dataRepository.shutdown();
        }
    }
} 