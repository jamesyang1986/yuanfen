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
import com.yf.yuanfen.dto.TokenResponse;
import com.yf.yuanfen.dto.UsernameRegisterRequest;
import com.yf.yuanfen.network.RetrofitClient;
import com.yf.yuanfen.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String username = getText(etUsername);
        String password = getText(etPassword);

        if (TextUtils.isEmpty(username)) {
            toast("用户名不能为空");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            toast("用户名须为3-20位字母/数字/下划线");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            toast("密码长度须在8-32位之间");
            return;
        }

        btnRegister.setEnabled(false);
        RetrofitClient.authApi(this).registerByUsername(
                new UsernameRegisterRequest(username, password)
        ).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call,
                                   Response<ApiResponse<TokenResponse>> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse token = response.body().getData();
                    new TokenManager(RegisterActivity.this)
                            .saveTokens(token.getAccessToken(), token.getRefreshToken());
                    startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                    finishAffinity();
                } else {
                    int code = response.code();
                    toast(code == 409 ? "用户名已被注册" : "注册失败，请重试");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                btnRegister.setEnabled(true);
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
