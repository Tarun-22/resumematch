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

    Button btnenter;
    ImageView iconimage;
    TextView appTitle, Apptitle;
    private static final int splash_delay = 3000; // 3secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnenter = findViewById(R.id.btnEnterEmployer);
        iconimage = findViewById(R.id.iconImage);
        appTitle = findViewById(R.id.appTitle);
        Apptitle = findViewById(R.id.apptitle);

        iconimage.setAlpha(0f);
        appTitle.setAlpha(0f);
        Apptitle.setAlpha(0f);

        startslideupanimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigatetomainactivity();
            }
        }, splash_delay);

        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetomainactivity();
            }
        });
    }

    private void startslideupanimation() {
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
        
        iconimage.startAnimation(slideUp);
        iconimage.startAnimation(fadeIn);
        iconimage.setAlpha(1f);

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

    private void navigatetomainactivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
