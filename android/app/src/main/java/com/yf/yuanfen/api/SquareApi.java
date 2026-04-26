package com.yf.yuanfen.api;

import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.PageResult;
import com.yf.yuanfen.dto.UserPublicDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SquareApi {

    @GET("api/v1/users")
    Call<ApiResponse<PageResult<UserPublicDTO>>> listUsers(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/v1/users/{id}")
    Call<ApiResponse<UserPublicDTO>> getUserById(@Path("id") long id);
}
