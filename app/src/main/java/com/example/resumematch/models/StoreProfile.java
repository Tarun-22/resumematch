package com.example.resumematch.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "store_profile")
public class StoreProfile {
    @PrimaryKey
    @NonNull
    private String id;
    private String storeName;
    private String storeAddress;
    private String storeCity;
    private String storeState;
    private String storeZipCode;
    private String phone;
    private String email;
    private String description;
    private long createdAt;

    public StoreProfile(@NonNull String id, String storeName, String storeAddress, String storeCity, 
                       String storeState, String storeZipCode, String phone, String email, String description) {
        this.id = id;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeCity = storeCity;
        this.storeState = storeState;
        this.storeZipCode = storeZipCode;
        this.phone = phone;
        this.email = email;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public String getStoreCity() { return storeCity; }
    public void setStoreCity(String storeCity) { this.storeCity = storeCity; }

    public String getStoreState() { return storeState; }
    public void setStoreState(String storeState) { this.storeState = storeState; }

    public String getStoreZipCode() { return storeZipCode; }
    public void setStoreZipCode(String storeZipCode) { this.storeZipCode = storeZipCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getFullAddress() {
        return storeAddress + ", " + storeCity + ", " + storeState + " " + storeZipCode;
    }
    
    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();
        if (storeAddress != null && !storeAddress.isEmpty()) {
            address.append(storeAddress);
        }
        if (storeCity != null && !storeCity.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(storeCity);
        }
        if (storeState != null && !storeState.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(storeState);
        }
        if (storeZipCode != null && !storeZipCode.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(storeZipCode);
        }
        return address.toString();
    }
} 