package com.yf.yuanfen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.UserPublicDTO;
import com.yf.yuanfen.network.RetrofitClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private TextView tvNickname, tvCityAge, tvOccupation, labelBio, tvBio, labelTags, tvNotFound;
    private ChipGroup chipGroupTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvNickname = findViewById(R.id.tvNickname);
        tvCityAge = findViewById(R.id.tvCityAge);
        tvOccupation = findViewById(R.id.tvOccupation);
        labelBio = findViewById(R.id.labelBio);
        tvBio = findViewById(R.id.tvBio);
        labelTags = findViewById(R.id.labelTags);
        tvNotFound = findViewById(R.id.tvNotFound);
        chipGroupTags = findViewById(R.id.chipGroupTags);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        long userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            showNotFound();
            return;
        }
        loadUser(userId);
    }

    private void loadUser(long userId) {
        RetrofitClient.squareApi(this).getUserById(userId)
                .enqueue(new Callback<ApiResponse<UserPublicDTO>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserPublicDTO>> call,
                                           Response<ApiResponse<UserPublicDTO>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            fillUser(response.body().getData());
                        } else if (response.code() == 404) {
                            showNotFound();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UserPublicDTO>> call, Throwable t) {
                        showNotFound();
                    }
                });
    }

    private void fillUser(UserPublicDTO user) {
        tvNickname.setText(TextUtils.isEmpty(user.getNickname()) ? "用户" : user.getNickname());

        String city = TextUtils.isEmpty(user.getCity()) ? "未知" : user.getCity();
        String age = user.getAge() != null ? user.getAge() + " 岁" : "未知";
        tvCityAge.setText(city + " · " + age);

        if (!TextUtils.isEmpty(user.getOccupation())) {
            tvOccupation.setText(user.getOccupation());
            tvOccupation.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            Glide.with(this).load(user.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivAvatar);
        }

        if (!TextUtils.isEmpty(user.getBio())) {
            tvBio.setText(user.getBio());
            labelBio.setVisibility(View.VISIBLE);
            tvBio.setVisibility(View.VISIBLE);
        }

        List<String> tags = user.getPartnerTags();
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                Chip chip = new Chip(this);
                chip.setText(tag);
                chip.setClickable(false);
                chipGroupTags.addView(chip);
            }
            labelTags.setVisibility(View.VISIBLE);
            chipGroupTags.setVisibility(View.VISIBLE);
        }
    }

    private void showNotFound() {
        tvNotFound.setVisibility(View.VISIBLE);
    }
}
