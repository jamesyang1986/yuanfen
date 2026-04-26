package com.yf.yuanfen.api;

import com.yf.yuanfen.dto.ApiResponse;
import com.yf.yuanfen.dto.SmsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SmsApi {

    @POST("api/v1/sms/send")
    Call<ApiResponse<Void>> sendCode(@Body SmsRequest request);
}
