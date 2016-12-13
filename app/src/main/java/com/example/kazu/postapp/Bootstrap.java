package com.example.kazu.postapp;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class Bootstrap extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
