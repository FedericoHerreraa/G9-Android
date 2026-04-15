package com.example.desarrollo_apps_1.data.network;

import com.example.desarrollo_apps_1.data.model.AuthResponse;
import com.example.desarrollo_apps_1.data.model.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("auth/send-otp")
    Call<AuthResponse> sendOtp(@Body LoginRequest body);

    @POST("auth/verify-otp")
    Call<AuthResponse> verifyOtp(@Body LoginRequest body);

    @POST("auth/resend-otp")
    Call<AuthResponse> resendOtp(@Body LoginRequest body);
}