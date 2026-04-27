package com.example.desarrollo_apps_1.data.local;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class NetworkMonitor {

    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(true);

    @Inject
    public NetworkMonitor(@ApplicationContext Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        registerCallback();
    }

    private void registerCallback() {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                isConnected.postValue(true);
            }

            @Override
            public void onLost(Network network) {
                isConnected.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public boolean isCurrentlyConnected() {
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}