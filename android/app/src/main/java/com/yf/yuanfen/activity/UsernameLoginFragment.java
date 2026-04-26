package com.yf.yuanfen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class UsernameLoginFragment extends Fragment {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = getText(etUsername);
        String password = getText(etPassword);
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            toast("请输入用户名和密码");
            return;
        }
        btnLogin.setEnabled(false);
        RetrofitClient.authApi(requireContext()).login(
                new LoginRequest("USERNAME_PASSWORD", username, password)
        ).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call,
                                   Response<ApiResponse<TokenResponse>> response) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse token = response.body().getData();
                    new TokenManager(requireContext())
                            .saveTokens(token.getAccessToken(), token.getRefreshToken());
                    startActivity(new Intent(requireContext(), SquareActivity.class));
                    requireActivity().finishAffinity();
                } else {
                    toast("账号或密码错误");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                toast("网络错误：" + t.getMessage());
            }
        });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
