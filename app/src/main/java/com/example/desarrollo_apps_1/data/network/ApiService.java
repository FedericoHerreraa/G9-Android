package com.example.desarrollo_apps_1.data.network;

import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.ActividadListResponse;
import com.example.desarrollo_apps_1.data.model.AuthResponse;
import com.example.desarrollo_apps_1.data.model.LoginRequest;
import com.example.desarrollo_apps_1.data.model.OtpRequest;
import com.example.desarrollo_apps_1.data.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("auth/otp/send")
    Call<Void> sendOtp(@Body OtpRequest body);

    @POST("auth/otp/verify")
    Call<AuthResponse> verifyOtp(@Body OtpRequest body);

    @GET("auth/me")
    Call<UserResponse> getCurrentUser();

    @GET("actividades")
    Call<ActividadListResponse> getActividades(
            @Query("limit") int limit,
            @Query("page") int page,
            @Query("destino") String destino,
            @Query("categoria") String categoria,
            @Query("precio_min") Double precioMin,
            @Query("precio_max") Double precioMax,
            @Query("fecha") String fecha,
            @Query("destacadas") Boolean destacadas
    );

    @GET("actividades/{id}")
    Call<Actividad> getActividadById(@Path("id") int id);

    @GET("actividades/recomendadas")
    Call<List<Actividad>> getRecomendadas(@Query("preferencias") String preferencias);


}