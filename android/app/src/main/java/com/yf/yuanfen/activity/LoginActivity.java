package com.yf.yuanfen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.LoginRequest;
import com.yf.yuanfen.dto.TokenResponse;
import com.yf.yuanfen.network.RetrofitClient;
import com.yf.yuanfen.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String username = getText(etUsername);
        String password = getText(etPassword);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            toast("请输入用户名和密码");
            return;
        }

        btnLogin.setEnabled(false);
        RetrofitClient.authApi(this).login(
                new LoginRequest("USERNAME_PASSWORD", username, password)
        ).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call,
                                   Response<ApiResponse<TokenResponse>> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse token = response.body().getData();
                    new TokenManager(LoginActivity.this)
                            .saveTokens(token.getAccessToken(), token.getRefreshToken());
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    finishAffinity();
                } else {
                    toast("账号或密码错误");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                btnLogin.setEnabled(true);
                toast("网络错误：" + t.getMessage());
            }
        });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
