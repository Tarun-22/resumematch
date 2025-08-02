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
    
    private EditText edittxt_storename, edittxt_storeAddr, edittxt_storecity,
            edittxt_storestate, edittxt_storezipcode, edittxt_storephone,
            edittxt_storeemail, edittxt_storeDescript;
    private Button Btnsave, Btncancel;
    private DataRepository dataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        
        dataRepo = new DataRepository(this);
        
        edittxt_storename = findViewById(R.id.editTextStoreName);
        edittxt_storeAddr = findViewById(R.id.editTextStoreAddress);
        edittxt_storecity = findViewById(R.id.editTextStoreCity);
        edittxt_storestate = findViewById(R.id.editTextStoreState);
        edittxt_storezipcode = findViewById(R.id.editTextStoreZipCode);
        edittxt_storephone = findViewById(R.id.editTextStorePhone);
        edittxt_storeemail = findViewById(R.id.editTextStoreEmail);
        edittxt_storeDescript = findViewById(R.id.editTextStoreDescription);
        Btnsave = findViewById(R.id.buttonSave);
        Btncancel = findViewById(R.id.buttonCancel);
        ImageView backButton = findViewById(R.id.backButton);
        
        backButton.setOnClickListener(v -> finish());
        Btncancel.setOnClickListener(v -> finish());
        Btnsave.setOnClickListener(v -> savestoreprofile());
        
        loadexistingstoreprofile();
    }
    
    private void loadexistingstoreprofile() {
        dataRepo.getFirstStore(new DataRepository.DatabaseCallback<StoreProfile>() {
            @Override
            public void onResult(StoreProfile storeProfile) {
                if (storeProfile != null) {
                    runOnUiThread(() -> {
                        edittxt_storename.setText(storeProfile.getStoreName());
                        edittxt_storeAddr.setText(storeProfile.getStoreAddress());
                        edittxt_storecity.setText(storeProfile.getStoreCity());
                        edittxt_storestate.setText(storeProfile.getStoreState());
                        edittxt_storezipcode.setText(storeProfile.getStoreZipCode());
                        edittxt_storephone.setText(storeProfile.getPhone());
                        edittxt_storeemail.setText(storeProfile.getEmail());
                        edittxt_storeDescript.setText(storeProfile.getDescription());
                    });
                }
            }
        });
    }
    
    private void savestoreprofile() {
        String storeName = edittxt_storename.getText().toString().trim();
        String storeAddress = edittxt_storeAddr.getText().toString().trim();
        String storeCity = edittxt_storecity.getText().toString().trim();
        String storeState = edittxt_storestate.getText().toString().trim();
        String storeZipCode = edittxt_storezipcode.getText().toString().trim();
        String phone = edittxt_storephone.getText().toString().trim();
        String email = edittxt_storeemail.getText().toString().trim();
        String description = edittxt_storeDescript.getText().toString().trim();
        
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
        
        Snackbar.make(Btnsave, "Saving store profile...", Snackbar.LENGTH_SHORT).show();
        
        dataRepo.insertStore(storeProfile, new DataRepository.DatabaseCallback<Void>() {
            @Override
            public void onResult(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(StoreProfileActivity.this, "Store profile saved successfully!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(Btnsave, "Store profile updated!", Snackbar.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataRepo != null) {
            dataRepo.shutdown();
        }
    }
} 