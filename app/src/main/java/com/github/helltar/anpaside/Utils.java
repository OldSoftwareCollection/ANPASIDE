package com.github.helltar.anpaside;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import com.github.helltar.anpaside.logging.Logger;
import com.github.helltar.anpaside.logging.LoggerMessageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import static com.github.helltar.anpaside.logging.Logger.*;

public class Utils {
    Context context;
    
    public Utils(Context context) {
        this.context = context;
    }
    
    public boolean mkdir(String dirName) {
        if (new File(dirName).mkdirs() | fileExists(dirName)) {
            return true;
        } else {
            Logger.addLog(
                context.getString(R.string.error_directory_creation) + ": " + dirName,
                LoggerMessageType.ERROR.ordinal()
            );
        }

        return false;
    }

    public boolean copyFileToDir(String srcFile, String destDir) {
        return copyFileToDir(srcFile, destDir, true);
    }

    public boolean copyFileToDir(String srcFile, String destDir, boolean showErrMsg) {
        if (fileExists(srcFile, showErrMsg)) {
            try {
                FileUtils.copyFileToDirectory(new File(srcFile), new File(destDir));
                return true;
            } catch (IOException ioe) {
                Logger.addLog(ioe);
            }
        }

        return false;
    }

    public boolean fileExists(String filename) {
        return fileExists(filename, false);
    }

    public boolean fileExists(String filename, boolean showErrMsg) {
        if (!filename.isEmpty()) {
            if (new File(filename).exists()) {
                return true;
            } else if (showErrMsg) {
                Logger.addLog(
                    context.getString(R.string.error_file_not_found) + ": " + filename,
                    LoggerMessageType.ERROR.ordinal()
                );
            }
        }

        return false;
    }

    public String getFileNameOnly(String filename) {
        return FilenameUtils.getBaseName(filename);
    }

    public long getFileSize(String filename) {
        return new File(filename).length() / 1024;
    }

    public boolean createTextFile(String filename, String text) {
        try {
            FileUtils.writeStringToFile(new File(filename), text);
            return true;
        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }

    public ProcessResult runProc(String args) {
        boolean result = false;
        StringBuffer output = new StringBuffer();

        try {
            Process process = Runtime.getRuntime().exec(args);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";

            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
                process.waitFor();
            }

            result = true;

        } catch (IOException | InterruptedException exception) {
            Logger.addLog(exception);
        }
    
        return new ProcessResult(result, output.toString());
    }

    public String getPathFromUri(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.toString();
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return uri.toString();
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
