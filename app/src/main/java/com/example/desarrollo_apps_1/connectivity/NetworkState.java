package com.example.desarrollo_apps_1.connectivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkState {

    private static NetworkState instance;
    private final MutableLiveData<Boolean> connected = new MutableLiveData<>(true);

    private NetworkState() {}

    public static synchronized NetworkState getInstance() {
        if (instance == null) {
            instance = new NetworkState();
        }
        return instance;
    }

    public LiveData<Boolean> getConnected() {
        return connected;
    }

    public void setConnected(boolean isConnected) {
        connected.postValue(isConnected);
    }

    public boolean isConnected() {
        Boolean val = connected.getValue();
        return val != null && val;
    }
}
