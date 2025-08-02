package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;
import com.example.resumematch.activities.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnEnter;
    ImageView iconImage;
    TextView appTitle, appSubtitle;
    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize views
        btnEnter = findViewById(R.id.btnEnterEmployer);
        iconImage = findViewById(R.id.iconImage);
        appTitle = findViewById(R.id.appTitle);
        appSubtitle = findViewById(R.id.appSubtitle);

        // Set initial alpha to 0 for fade-in effect
        iconImage.setAlpha(0f);
        appTitle.setAlpha(0f);
        appSubtitle.setAlpha(0f);

        // Start slide-up and fade-in animation
        startSlideUpAnimation();

        // Set up automatic navigation after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToMainActivity();
            }
        }, SPLASH_DELAY);

        // Manual button click (optional - user can skip the delay)
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMainActivity();
            }
        });
    }

    private void startSlideUpAnimation() {
        // Slide up and fade in the logo
        TranslateAnimation slideUp = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.3f,
            Animation.RELATIVE_TO_SELF, 0.0f
        );
        slideUp.setDuration(1200);
        slideUp.setFillAfter(true);
        
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        
        // Combine slide up and fade in for logo
        iconImage.startAnimation(slideUp);
        iconImage.startAnimation(fadeIn);
        iconImage.setAlpha(1f);

        // Slide up and fade in the title after a short delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation titleSlideUp = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.2f,
                    Animation.RELATIVE_TO_SELF, 0.0f
                );
                titleSlideUp.setDuration(800);
                titleSlideUp.setFillAfter(true);
                
                AlphaAnimation titleFadeIn = new AlphaAnimation(0.0f, 1.0f);
                titleFadeIn.setDuration(800);
                
                appTitle.startAnimation(titleSlideUp);
                appTitle.startAnimation(titleFadeIn);
                appTitle.setAlpha(1f);
            }
        }, 400);

        // Slide up and fade in the subtitle after title
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation subtitleSlideUp = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.2f,
                    Animation.RELATIVE_TO_SELF, 0.0f
                );
                subtitleSlideUp.setDuration(800);
                subtitleSlideUp.setFillAfter(true);
                
                AlphaAnimation subtitleFadeIn = new AlphaAnimation(0.0f, 1.0f);
                subtitleFadeIn.setDuration(800);
                
                appSubtitle.startAnimation(subtitleSlideUp);
                appSubtitle.startAnimation(subtitleFadeIn);
                appSubtitle.setAlpha(1f);
            }
        }, 700);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the welcome activity so user can't go back
    }
}
