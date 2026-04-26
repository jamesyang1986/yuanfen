package com.yf.yuanfen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.yf.yuanfen.R;
import com.yf.yuanfen.network.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TokenManager tokenManager = new TokenManager(this);
            Class<?> target = tokenManager.hasToken() ? SquareActivity.class : LoginActivity.class;
            startActivity(new Intent(this, target));
            finish();
        }, SPLASH_DELAY_MS);
    }

    @Override
    public void onBackPressed() {
        // 开屏期间禁用返回键
    }
}
