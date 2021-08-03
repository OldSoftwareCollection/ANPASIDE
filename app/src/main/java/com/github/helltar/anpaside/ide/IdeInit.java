package com.github.helltar.anpaside.ide;

import static com.github.helltar.anpaside.Consts.DATA_PKG_PATH;

import android.content.Context;
import android.content.res.AssetManager;

import com.github.helltar.anpaside.R;
import com.github.helltar.anpaside.logging.Logger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class IdeInit {
    private Context context;
    private AssetManager assetManager;

    public IdeInit(
        Context context,
        AssetManager assetManager
    ) {
        this.context = context;
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

            if (assets.length > 0) {
                File dir = new File(DATA_PKG_PATH + assetDir);

                if (!dir.exists()) {
                    dir.mkdir();
                }

                for (int i = 0; i < assets.length; i++) {
                    copyAssets(assetDir + "/" + assets[i]);
                }
            } else {
                FileUtils.copyInputStreamToFile(assetManager.open(assetDir), new File(DATA_PKG_PATH + assetDir));
            }

            return true;

        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }
}
