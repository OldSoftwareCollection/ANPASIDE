package com.github.helltar.anpaside;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import com.github.helltar.anpaside.logging.RoboErrorReporter;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
