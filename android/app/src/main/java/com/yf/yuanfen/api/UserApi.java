package com.yf.yuanfen.api;

import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.AvatarResponse;
import com.yf.yuanfen.dto.UserProfileDTO;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @GET("api/v1/users/profile")
    Call<ApiResponse<UserProfileDTO>> getProfile();

    @PUT("api/v1/users/profile")
    Call<ApiResponse<UserProfileDTO>> updateProfile(@Body Map<String, Object> fields);

    @Multipart
    @POST("api/v1/users/avatar")
    Call<ApiResponse<AvatarResponse>> uploadAvatar(@Part MultipartBody.Part file);
}
