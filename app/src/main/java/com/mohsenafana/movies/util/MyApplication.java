package com.mohsenafana.movies.util;

import android.app.Application;

import com.mohsenafana.movies.BrodcastReciverNetwork.ConnectivityReceiver;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}