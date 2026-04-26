package com.yf.yuanfen.api;

import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.LoginRequest;
import com.yf.yuanfen.dto.RefreshRequest;
import com.yf.yuanfen.dto.TokenResponse;
import com.yf.yuanfen.dto.UsernameRegisterRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/v1/auth/register/username")
    Call<ApiResponse<TokenResponse>> registerByUsername(@Body UsernameRegisterRequest request);

    @POST("api/v1/auth/login")
    Call<ApiResponse<TokenResponse>> login(@Body LoginRequest request);

    @POST("api/v1/auth/refresh")
    Call<ApiResponse<TokenResponse>> refresh(@Body RefreshRequest request);

    @POST("api/v1/auth/logout")
    Call<ApiResponse<Void>> logout(@Body RefreshRequest request);

    @GET("api/v1/auth/wechat/auth-url")
    Call<ApiResponse<Map<String, String>>> getWechatAuthUrl();
}
