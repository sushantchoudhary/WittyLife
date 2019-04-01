package com.appdev.schoudhary.wittylife.app;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.Stetho;

public class WittyLife extends Application {

    private static WittyLife wittyLife;
    private static Context context;

    public static WittyLife getMyApplication() {
        return wittyLife;
    }

    public static Context getAppContext() {
        return WittyLife.context;
    }

    public void onCreate() {
        super.onCreate();

        wittyLife = this;
        WittyLife.context = getApplicationContext();

        initializeStetho();

    }

    public void initializeStetho() {
        Stetho.initializeWithDefaults(context);
    }
}
