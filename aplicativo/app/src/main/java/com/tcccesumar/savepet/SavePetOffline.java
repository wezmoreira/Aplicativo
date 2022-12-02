package com.tcccesumar.savepet;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SavePetOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
