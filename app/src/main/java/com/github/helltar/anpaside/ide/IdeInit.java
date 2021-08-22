package com.github.helltar.anpaside.ide;

import android.content.Context;
import android.content.res.AssetManager;

import com.github.helltar.anpaside.R;
import com.github.helltar.anpaside.logging.LoggerInterface;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class IdeInit {
    private Context context;
    private LoggerInterface logger;
    private AssetManager assetManager;

    public IdeInit(
        Context context,
        LoggerInterface logger,
        AssetManager assetManager
    ) {
        this.context = context;
        this.logger = logger;
        this.assetManager = assetManager;
    }

    public boolean install() {
        if (
            copyAssets(
                context.getString(R.string.assets_directory_stubs)
            )
        ) {
            return copyAssets(
                context.getString(R.string.assets_directory_files)
            );
        }

        return false;
    }

    private boolean copyAssets(String assetDir) {
        try {
            String[] assets = assetManager.list(assetDir);
    
            final String dataPackagePath = context.getApplicationInfo().dataDir + "/";
    
            if (assets.length > 0) {
                File dir = new File(dataPackagePath + assetDir);

                if (!dir.exists()) {
                    dir.mkdir();
                }

                for (int i = 0; i < assets.length; i++) {
                    copyAssets(assetDir + "/" + assets[i]);
                }
            } else {
                FileUtils.copyInputStreamToFile(assetManager.open(assetDir), new File(dataPackagePath + assetDir));
            }

            return true;

        } catch (IOException exception) {
            logger.showLoggerErrorMessage(exception);
        }

        return false;
    }
}
