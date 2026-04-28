package com.example.desarrollo_apps_1.data.network;

import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Noticia;
import com.example.desarrollo_apps_1.data.model.ActividadListResponse;
import com.example.desarrollo_apps_1.data.model.AuthResponse;
import com.example.desarrollo_apps_1.data.model.CheckFavoritoResponse;
import com.example.desarrollo_apps_1.data.model.FavoritosResponse;
import com.example.desarrollo_apps_1.data.model.HistorialItem;
import com.example.desarrollo_apps_1.data.model.LoginRequest;
import com.example.desarrollo_apps_1.data.model.OtpRequest;
import com.example.desarrollo_apps_1.data.model.ProfileResponse;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.model.ReviewRequest;
import com.example.desarrollo_apps_1.data.model.UpdateProfileRequest;
import com.example.desarrollo_apps_1.data.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("profile/me")
    Call<ProfileResponse> getProfile();

    @PUT("profile/me")
    Call<ProfileResponse> updateProfile(@Body UpdateProfileRequest body);

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

    @GET("reservas")
    Call<ReservaListResponse> getMisReservas();

    @POST("reservas")
    Call<Reserva> crearReserva(@Body ReservaRequest body);

    @PATCH("reservas/{id}/cancelar")
    Call<Reserva> cancelarReserva(@Path("id") String id);

    @POST("usuarios/historial/review")
    Call<Void> postReview(@Body ReviewRequest review);

    @GET("usuarios/historial")
    Call<List<HistorialItem>> getHistorial(
            @Query("fecha_inicio") String fechaInicio,
            @Query("fecha_fin") String fechaFin,
            @Query("destino") String destino
    );
    @GET("favoritos")
    Call<FavoritosResponse> getMisFavoritos();

    @POST("favoritos/{actividadId}")
    Call<Void> addFavorito(@Path("actividadId") int actividadId);

    @DELETE("favoritos/{actividadId}")
    Call<Void> removeFavorito(@Path("actividadId") int actividadId);

    @GET("favoritos/{actividadId}/check")
    Call<CheckFavoritoResponse> checkFavorito(@Path("actividadId") int actividadId);

    @GET("noticias")
    Call<List<Noticia>> getNoticias();
}
