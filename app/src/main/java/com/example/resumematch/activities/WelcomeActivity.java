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

public class WelcomeActivity extends AppCompatActivity {

    Button ButtonEnter;
    ImageView IconImage;
    TextView appTitle, Apptitle;
    private static final int Splash_Delay = 3000; // 3secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButtonEnter = findViewById(R.id.btnEnterEmployer);
        IconImage = findViewById(R.id.iconImage);
        appTitle = findViewById(R.id.appTitle);
        Apptitle = findViewById(R.id.apptitle);

        IconImage.setAlpha(0f);
        appTitle.setAlpha(0f);
        Apptitle.setAlpha(0f);

        startSlideUpAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToMainActivity();
            }
        }, Splash_Delay);

        ButtonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMainActivity();
            }
        });
    }

    private void startSlideUpAnimation() {
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
        
        IconImage.startAnimation(slideUp);
        IconImage.startAnimation(fadeIn);
        IconImage.setAlpha(1f);

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
                
                Apptitle.startAnimation(subtitleSlideUp);
                Apptitle.startAnimation(subtitleFadeIn);
                Apptitle.setAlpha(1f);
            }
        }, 700);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
