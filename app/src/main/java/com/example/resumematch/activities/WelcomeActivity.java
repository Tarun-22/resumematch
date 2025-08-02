package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.activities.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnEnter = findViewById(R.id.btnEnterEmployer);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, EmployerHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
