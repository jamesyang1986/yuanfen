package com.yf.yuanfen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new LoginPagerAdapter(this));
        viewPager.setUserInputEnabled(false); // 禁止滑动切换，只用 Tab 点击

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "用户名登录" : "手机号登录");
        }).attach();

        findViewById(R.id.tvGoRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        MaterialButton btnWechatLogin = findViewById(R.id.btnWechatLogin);
        btnWechatLogin.setOnClickListener(v -> loginWithWechat());
    }

    private void loginWithWechat() {
        RetrofitClient.authApi(this).getWechatAuthUrl()
                .enqueue(new Callback<ApiResponse<Map<String, String>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, String>>> call,
                                           Response<ApiResponse<Map<String, String>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            String url = response.body().getData().get("url");
                            if (url != null) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "获取微信授权链接失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, String>>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    private static class LoginPagerAdapter extends FragmentStateAdapter {

        LoginPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == 0 ? new UsernameLoginFragment() : new PhoneLoginFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
