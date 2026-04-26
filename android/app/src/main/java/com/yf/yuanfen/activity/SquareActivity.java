package com.yf.yuanfen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.PageResult;
import com.yf.yuanfen.dto.UserPublicDTO;
import com.yf.yuanfen.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SquareActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_square);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return true;
        });

        loadUsers();
    }

    private void loadUsers() {
        RetrofitClient.squareApi(this).listUsers(0, 20)
                .enqueue(new Callback<ApiResponse<PageResult<UserPublicDTO>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResult<UserPublicDTO>>> call,
                                           Response<ApiResponse<PageResult<UserPublicDTO>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            recyclerView.setAdapter(new UserCardAdapter(
                                    SquareActivity.this,
                                    response.body().getData().getItems()
                            ));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResult<UserPublicDTO>>> call, Throwable t) {
                        Toast.makeText(SquareActivity.this, "加载失败：" + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
