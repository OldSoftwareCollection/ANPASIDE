package com.github.helltar.anpaside;

public class Consts {
    public static final String DATA_PKG_PATH = MainApp.getContext().getApplicationInfo().dataDir + "/";
    public static final String DATA_LIB_PATH = MainApp.getContext().getApplicationInfo().nativeLibraryDir + "/";
    
    // strings
    public static final String LANG_ERR_FILE_NOT_FOUND = getString(R.string.err_file_not_found);
    public static final String LANG_ERR_CREATE_DIR = getString(R.string.err_create_dir);

    private static String getString(int resId) {
        return MainApp.receiveString(resId);
    }
}

