package com.github.helltar.anpaside;

public class Consts {
    public static final String DATA_PKG_PATH = MainApp.getContext().getApplicationInfo().dataDir + "/";
    public static final String DATA_LIB_PATH = MainApp.getContext().getApplicationInfo().nativeLibraryDir + "/";
    
    public static final String DIR_MAIN = "AppProjects";
    public static final String DIR_BIN = "bin/";
    public static final String DIR_SRC = "src/";
    public static final String DIR_LIBS = "libs/";
    public static final String DIR_RES = "res/";
    public static final String DIR_PREBUILD = "prebuild/";

    // strings
    public static final String LANG_MSG_BUILD_SUCCESSFULLY = getString(R.string.msg_build_successfully);
    public static final String LANG_ERR_FAILED_CREATE_ARCHIVE = getString(R.string.err_failed_create_archive);
    public static final String LANG_ERR_FILE_NOT_FOUND = getString(R.string.err_file_not_found);
    public static final String LANG_ERR_CREATE_DIR = getString(R.string.err_create_dir);

    private static String getString(int resId) {
        return MainApp.receiveString(resId);
    }
}

