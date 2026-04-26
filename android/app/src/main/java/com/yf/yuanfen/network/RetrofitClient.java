package com.yf.yuanfen.network;

import android.content.Context;

import com.yf.yuanfen.api.AuthApi;
import com.yf.yuanfen.api.SmsApi;
import com.yf.yuanfen.api.SquareApi;
import com.yf.yuanfen.api.UserApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = new TokenManager(context);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenManager))
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApi authApi(Context context) {
        return getInstance(context).create(AuthApi.class);
    }

    public static UserApi userApi(Context context) {
        return getInstance(context).create(UserApi.class);
    }

    public static SmsApi smsApi(Context context) {
        return getInstance(context).create(SmsApi.class);
    }

    public static SquareApi squareApi(Context context) {
        return getInstance(context).create(SquareApi.class);
    }
}
