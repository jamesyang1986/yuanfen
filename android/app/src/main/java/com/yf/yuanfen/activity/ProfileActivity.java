package com.yf.yuanfen.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.yf.yuanfen.R;
import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.AvatarResponse;
import com.yf.yuanfen.dto.RefreshRequest;
import com.yf.yuanfen.dto.UserProfileDTO;
import com.yf.yuanfen.network.RetrofitClient;
import com.yf.yuanfen.network.TokenManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private TextInputEditText etNickname, etGender, etBirthDate, etCity, etAddress;
    private MaterialButton btnSave, btnLogout;

    private Integer selectedGender;
    private TokenManager tokenManager;

    private static final String[] GENDER_LABELS = {"未知", "男", "女", "其他"};

    // 权限请求
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    granted -> { if (granted) openGallery(); else toast("需要图片读取权限"); });

    // 相册选取
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> { if (uri != null) uploadAvatar(uri); });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);
        bindViews();
        setupListeners();
        loadProfile();
    }

    private void bindViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        etNickname = findViewById(R.id.etNickname);
        etGender = findViewById(R.id.etGender);
        etBirthDate = findViewById(R.id.etBirthDate);
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        // 头像点击 → 检查权限 → 相册
        ivAvatar.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // 性别点击 → 单选弹窗
        etGender.setOnClickListener(v -> showGenderDialog());
        etGender.setFocusable(false);

        // 出生年月点击 → DatePicker
        etBirthDate.setOnClickListener(v -> showMonthPicker());
        etBirthDate.setFocusable(false);

        // 保存
        btnSave.setOnClickListener(v -> saveProfile());

        // 登出
        btnLogout.setOnClickListener(v -> logout());
    }

    // ── 加载资料 ──────────────────────────────────────────────────────────────

    private void loadProfile() {
        RetrofitClient.userApi(this).getProfile()
                .enqueue(new Callback<ApiResponse<UserProfileDTO>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserProfileDTO>> call,
                                           Response<ApiResponse<UserProfileDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fillProfile(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<UserProfileDTO>> call, Throwable t) {
                        toast("加载失败：" + t.getMessage());
                    }
                });
    }

    private void fillProfile(UserProfileDTO p) {
        if (p == null) return;
        setText(etNickname, p.getNickname());
        setText(etCity, p.getCity());
        setText(etAddress, p.getAddress());
        setText(etBirthDate, p.getBirthDate());

        if (p.getGender() != null) {
            selectedGender = p.getGender();
            etGender.setText(selectedGender < GENDER_LABELS.length
                    ? GENDER_LABELS[selectedGender] : "");
        }

        if (!TextUtils.isEmpty(p.getAvatarUrl())) {
            Glide.with(this).load(p.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivAvatar);
        }
    }

    // ── 保存资料 ──────────────────────────────────────────────────────────────

    private void saveProfile() {
        Map<String, Object> fields = new HashMap<>();
        String nickname = getText(etNickname);
        String city = getText(etCity);
        String address = getText(etAddress);
        String birthDate = getText(etBirthDate);

        if (!TextUtils.isEmpty(nickname)) fields.put("nickname", nickname);
        if (!TextUtils.isEmpty(city)) fields.put("city", city);
        if (!TextUtils.isEmpty(address)) fields.put("address", address);
        if (!TextUtils.isEmpty(birthDate)) fields.put("birthDate", birthDate);
        if (selectedGender != null) fields.put("gender", selectedGender);

        btnSave.setEnabled(false);
        RetrofitClient.userApi(this).updateProfile(fields)
                .enqueue(new Callback<ApiResponse<UserProfileDTO>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserProfileDTO>> call,
                                           Response<ApiResponse<UserProfileDTO>> response) {
                        btnSave.setEnabled(true);
                        if (response.isSuccessful()) {
                            toast("保存成功");
                            if (response.body() != null) fillProfile(response.body().getData());
                        } else {
                            toast("保存失败");
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<UserProfileDTO>> call, Throwable t) {
                        btnSave.setEnabled(true);
                        toast("网络错误：" + t.getMessage());
                    }
                });
    }

    // ── 性别弹窗 ──────────────────────────────────────────────────────────────

    private void showGenderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择性别")
                .setItems(GENDER_LABELS, (dialog, which) -> {
                    selectedGender = which;
                    etGender.setText(GENDER_LABELS[which]);
                })
                .show();
    }

    // ── 出生年月选择 ──────────────────────────────────────────────────────────

    private void showMonthPicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String formatted = String.format("%d-%02d", year, month + 1);
            etBirthDate.setText(formatted);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1).show();
    }

    // ── 头像上传 ──────────────────────────────────────────────────────────────

    private void checkPermissionAndOpenGallery() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void uploadAvatar(Uri uri) {
        try {
            // Uri → 临时文件
            InputStream in = getContentResolver().openInputStream(uri);
            File tmp = File.createTempFile("avatar_", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tmp);
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            in.close();
            out.close();

            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody body = RequestBody.create(tmp, MediaType.parse(mimeType));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", tmp.getName(), body);

            RetrofitClient.userApi(this).uploadAvatar(part)
                    .enqueue(new Callback<ApiResponse<AvatarResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<AvatarResponse>> call,
                                               Response<ApiResponse<AvatarResponse>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                String url = response.body().getData().getAvatarUrl();
                                Glide.with(ProfileActivity.this).load(url).into(ivAvatar);
                                toast("头像上传成功");
                            } else {
                                String msg = "上传失败";
                                if (response.code() == 400) msg = "文件类型不支持或超过 5MB 限制";
                                toast(msg);
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<AvatarResponse>> call, Throwable t) {
                            toast("网络错误：" + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            toast("文件处理失败：" + e.getMessage());
        }
    }

    // ── 登出 ──────────────────────────────────────────────────────────────────

    private void logout() {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken != null) {
            RetrofitClient.authApi(this).logout(new RefreshRequest(refreshToken))
                    .enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call,
                                               Response<ApiResponse<Void>> response) {
                            doLocalLogout();
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            doLocalLogout();
                        }
                    });
        } else {
            doLocalLogout();
        }
    }

    private void doLocalLogout() {
        tokenManager.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setText(TextInputEditText et, String val) {
        et.setText(val != null ? val : "");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
