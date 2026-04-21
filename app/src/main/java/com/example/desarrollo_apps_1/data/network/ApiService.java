package com.example.desarrollo_apps_1.data.network;

import com.example.desarrollo_apps_1.data.model.AuthResponse;
import com.example.desarrollo_apps_1.data.model.LoginRequest;
import com.example.desarrollo_apps_1.data.model.OtpRequest;
import com.example.desarrollo_apps_1.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("auth/otp/send")
    Call<Void> sendOtp(@Body OtpRequest body);

    @POST("auth/otp/verify")
    Call<AuthResponse> verifyOtp(@Body OtpRequest body);

    @GET("auth/me")
    Call<UserResponse> getCurrentUser();
}