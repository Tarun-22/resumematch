package com.example.resumematch.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import com.example.resumematch.R;
import com.example.resumematch.database.DataRepository;
import com.example.resumematch.models.StoreProfile;

import java.util.UUID;

public class StoreProfileActivity extends AppCompatActivity {
    
    private EditText EditTextStoreName, EditTextStoreAddress, EditTextStoreCity,
            EditTextStoreState, EditTextStoreZipCode, EditTextStorePhone,
            EditTextStoreEmail, EditTextStoreDescription;
    private Button ButtonSave, ButtonCancel;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        
        dataRepository = new DataRepository(this);
        
        EditTextStoreName = findViewById(R.id.editTextStoreName);
        EditTextStoreAddress = findViewById(R.id.editTextStoreAddress);
        EditTextStoreCity = findViewById(R.id.editTextStoreCity);
        EditTextStoreState = findViewById(R.id.editTextStoreState);
        EditTextStoreZipCode = findViewById(R.id.editTextStoreZipCode);
        EditTextStorePhone = findViewById(R.id.editTextStorePhone);
        EditTextStoreEmail = findViewById(R.id.editTextStoreEmail);
        EditTextStoreDescription = findViewById(R.id.editTextStoreDescription);
        ButtonSave = findViewById(R.id.buttonSave);
        ButtonCancel = findViewById(R.id.buttonCancel);
        ImageView backButton = findViewById(R.id.backButton);
        
        backButton.setOnClickListener(v -> finish());
        ButtonCancel.setOnClickListener(v -> finish());
        ButtonSave.setOnClickListener(v -> saveStoreProfile());
        
        loadExistingStoreProfile();
    }
    
    private void loadExistingStoreProfile() {
        dataRepository.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
            @Override
            public void onResult(StoreProfile storeProfile) {
                if (storeProfile != null) {
                    runOnUiThread(() -> {
                        EditTextStoreName.setText(storeProfile.getStoreName());
                        EditTextStoreAddress.setText(storeProfile.getStoreAddress());
                        EditTextStoreCity.setText(storeProfile.getStoreCity());
                        EditTextStoreState.setText(storeProfile.getStoreState());
                        EditTextStoreZipCode.setText(storeProfile.getStoreZipCode());
                        EditTextStorePhone.setText(storeProfile.getPhone());
                        EditTextStoreEmail.setText(storeProfile.getEmail());
                        EditTextStoreDescription.setText(storeProfile.getDescription());
                    });
                }
            }
        });
    }
    
    private void saveStoreProfile() {
        String storeName = EditTextStoreName.getText().toString().trim();
        String storeAddress = EditTextStoreAddress.getText().toString().trim();
        String storeCity = EditTextStoreCity.getText().toString().trim();
        String storeState = EditTextStoreState.getText().toString().trim();
        String storeZipCode = EditTextStoreZipCode.getText().toString().trim();
        String phone = EditTextStorePhone.getText().toString().trim();
        String email = EditTextStoreEmail.getText().toString().trim();
        String description = EditTextStoreDescription.getText().toString().trim();
        
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
        
        Snackbar.make(ButtonSave, "Saving store profile...", Snackbar.LENGTH_SHORT).show();
        
        dataRepository.insertStore(storeProfile, new DataRepository.DatabaseCallback<Void>() {
            @Override
            public void onResult(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(StoreProfileActivity.this, "Store profile saved successfully!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(ButtonSave, "Store profile updated!", Snackbar.LENGTH_LONG).show();
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