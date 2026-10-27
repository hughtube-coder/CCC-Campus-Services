package com.navigatio.campusservices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_splash);

        animateSplash();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent = new Intent(
                    SplashActivity.this,
                    SecurityScanActivity.class
            );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, SPLASH_DELAY);
    }

    /* Logo pops in, then the texts slide up one after another. */
    private void animateSplash() {

        float d = getResources().getDisplayMetrics().density;

        android.view.View logo = findViewById(R.id.ivSplashLogo);
        android.view.View school = findViewById(R.id.tvSchoolName);
        android.view.View services = findViewById(R.id.tvCampusServices);

        logo.setAlpha(0f);
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);

        logo.animate()
                .alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(700)
                .setInterpolator(
                        new android.view.animation.OvershootInterpolator(1.6f))
                .start();

        android.view.View[] texts = {school, services};

        for (int i = 0; i < texts.length; i++) {

            texts[i].setAlpha(0f);
            texts[i].setTranslationY(24 * d);

            texts[i].animate()
                    .alpha(1f).translationY(0f)
                    .setStartDelay(500 + i * 200L)
                    .setDuration(500)
                    .setInterpolator(
                            new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }
}