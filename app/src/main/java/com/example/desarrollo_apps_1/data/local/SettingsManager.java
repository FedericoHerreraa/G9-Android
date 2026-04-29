package com.example.desarrollo_apps_1.data.local;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class SettingsManager {
    private final RxDataStore<Preferences> dataStore;
    private final Preferences.Key<Boolean> AUTO_EXPORT_KEY = PreferencesKeys.booleanKey("auto_export");
    private final Preferences.Key<Boolean> BIOMETRIC_ENABLED_KEY = PreferencesKeys.booleanKey("biometric_enabled");

    @Inject
    public SettingsManager(@ApplicationContext Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
    }

    public Flowable<Boolean> isAutoExportEnabled() {
        return dataStore.data().map(prefs -> prefs.get(AUTO_EXPORT_KEY) != null ? prefs.get(AUTO_EXPORT_KEY) : false);
    }

    public Single<Preferences> setAutoExport(boolean enabled) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(AUTO_EXPORT_KEY, enabled);
            return Single.just(mutablePreferences);
        });
    }

    public Flowable<Boolean> isBiometricEnabled() {
        return dataStore.data().map(prefs -> prefs.get(BIOMETRIC_ENABLED_KEY) != null ? prefs.get(BIOMETRIC_ENABLED_KEY) : false);
    }

    public Single<Preferences> setBiometricEnabled(boolean enabled) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(BIOMETRIC_ENABLED_KEY, enabled);
            return Single.just(mutablePreferences);
        });
    }
}
