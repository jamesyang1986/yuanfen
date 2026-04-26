package com.yf.yuanfen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.yf.yuanfen.dto.SmsRequest;
import com.yf.yuanfen.dto.TokenResponse;
import com.yf.yuanfen.network.RetrofitClient;
import com.yf.yuanfen.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneLoginFragment extends Fragment {

    private TextInputEditText etPhone, etCode;
    private MaterialButton btnSendCode, btnPhoneLogin;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etPhone = view.findViewById(R.id.etPhone);
        etCode = view.findViewById(R.id.etCode);
        btnSendCode = view.findViewById(R.id.btnSendCode);
        btnPhoneLogin = view.findViewById(R.id.btnPhoneLogin);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnPhoneLogin.setOnClickListener(v -> attemptLogin());
    }

    private void sendCode() {
        String phone = getText(etPhone);
        if (TextUtils.isEmpty(phone)) {
            toast("请输入手机号");
            return;
        }
        btnSendCode.setEnabled(false);
        RetrofitClient.smsApi(requireContext()).sendCode(new SmsRequest(phone))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            toast("验证码已发送（测试环境固定为 123456）");
                            startCountdown();
                        } else {
                            btnSendCode.setEnabled(true);
                            toast("发送失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        if (!isAdded()) return;
                        btnSendCode.setEnabled(true);
                        toast("网络错误：" + t.getMessage());
                    }
                });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isAdded()) return;
                btnSendCode.setText(millisUntilFinished / 1000 + "s 后重发");
            }

            @Override
            public void onFinish() {
                if (!isAdded()) return;
                btnSendCode.setText("发送验证码");
                btnSendCode.setEnabled(true);
            }
        }.start();
    }

    private void attemptLogin() {
        String phone = getText(etPhone);
        String code = getText(etCode);
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            toast("请输入手机号和验证码");
            return;
        }
        btnPhoneLogin.setEnabled(false);
        RetrofitClient.authApi(requireContext()).login(
                new LoginRequest("PHONE_SMS", phone, code)
        ).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call,
                                   Response<ApiResponse<TokenResponse>> response) {
                if (!isAdded()) return;
                btnPhoneLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse token = response.body().getData();
                    new TokenManager(requireContext())
                            .saveTokens(token.getAccessToken(), token.getRefreshToken());
                    if (token.isNewUser()) {
                        Intent intent = new Intent(requireContext(), ProfileActivity.class);
                        intent.putExtra("welcome", true);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(requireContext(), SquareActivity.class));
                    }
                    requireActivity().finishAffinity();
                } else {
                    toast("验证码错误或已过期");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                if (!isAdded()) return;
                btnPhoneLogin.setEnabled(true);
                toast("网络错误：" + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
