package com.example.desarrollo_apps_1.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.inject.Inject;
import javax.inject.Singleton;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class TokenManager {

    private static final String PREF_NAME = "xplorenow_secure_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences prefs;

    @Inject
    public TokenManager(@ApplicationContext Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public void savePreferencias(String preferencias) {
        prefs.edit().putString("preferencias", preferencias).apply();
    }

    public String getPreferencias() {
        return prefs.getString("preferencias", null);
    }
}