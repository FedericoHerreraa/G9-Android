package com.example.desarrollo_apps_1.data.network;

import android.content.Context;

import com.example.desarrollo_apps_1.data.local.TokenManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "https://backend-apps-1.onrender.com/";

    @Provides
    @Singleton
    public static Context provideContext(@ApplicationContext Context context) {
        return context;
    }

    @Provides
    @Singleton
    public static TokenManager provideTokenManager(@ApplicationContext Context context) {
        return new TokenManager(context);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(TokenManager tokenManager) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    
                    // Si es una ruta de auth, no agregamos el token para evitar problemas
                    String path = original.url().encodedPath();
                    if (path.contains("/auth/login") || path.contains("/auth/otp")) {
                        return chain.proceed(original);
                    }

                    String requestToken = tokenManager.getToken();
                    Request.Builder builder = original.newBuilder();

                    if (requestToken != null && !requestToken.isEmpty()) {
                        builder.header("Authorization", "Bearer " + requestToken);
                    }

                    Response response = chain.proceed(builder.build());

                    // Solo deslogueamos si el token que falló es el que tenemos guardado
                    if (response.code() == 401) {
                        String currentToken = tokenManager.getToken();
                        if (requestToken != null && requestToken.equals(currentToken)) {
                            tokenManager.logout();
                        }
                    }

                    return response;
                })
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public static ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
