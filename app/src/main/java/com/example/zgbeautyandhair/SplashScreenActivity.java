package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView logo, appName;
    ImageView splashImg;

    private static final int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        splashImg = findViewById(R.id.img);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.logo_title);

        splashImg.animate().translationY(-5000).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(2500).setDuration(1000).setStartDelay(4000);
        appName.animate().translationY(2500).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

}