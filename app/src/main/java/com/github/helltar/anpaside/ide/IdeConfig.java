package com.github.helltar.anpaside.ide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.github.helltar.anpaside.R;

public class IdeConfig {

    private final String PREF_NAME_INSTALL = "install";
    private final String PREF_NAME_GLOBAL_DIR_PATH = "global_libs_dir";

    private final Context context;

    public IdeConfig(Context context) {
        this.context = context;
    }

    private SharedPreferences getSpMain() {
        return context.getSharedPreferences("ide_config", context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getMainEditor() {
        return getSpMain().edit();   
    }

    private boolean getInstState() {
        return getSpMain().getBoolean(PREF_NAME_INSTALL, false);
    }

    public void setInstState(boolean val) {
        getMainEditor().putBoolean(PREF_NAME_INSTALL, val).apply();
    }

    public String getGlobalDirPath() {
        return getSpMain().getString(
            PREF_NAME_GLOBAL_DIR_PATH,
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + context.getString(R.string.directory_main) + "/"
                + context.getString(R.string.directory_libs)
        );
    }

    public void setGlobalDirPath(String path) {
        getMainEditor().putString(PREF_NAME_GLOBAL_DIR_PATH, path).apply();
    }

    public boolean isAssetsInstall() {
        return getInstState();
    }
}
