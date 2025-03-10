package com.github.helltar.anpaside;

import static com.github.helltar.anpaside.Consts.LANG_ERR_CREATE_DIR;
import static com.github.helltar.anpaside.Consts.LANG_ERR_FILE_NOT_FOUND;
import static com.github.helltar.anpaside.logging.Logger.LMT_ERROR;

import com.github.helltar.anpaside.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static void rmrf(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                rmrf(child);
            }
        }

        file.delete();
    }

    public static boolean mkdir(String dirName) {
        if (new File(dirName).mkdirs() | fileExists(dirName)) {
            return true;
        } else {
            Logger.addLog(LANG_ERR_CREATE_DIR + ": " + dirName, LMT_ERROR);
        }

        return false;
    }

    public static boolean copyFileToDir(String srcFile, String destDir) {
        return copyFileToDir(srcFile, destDir, true);
    }

    public static boolean copyFileToDir(String srcFile, String destDir, boolean showErrMsg) {
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

    public static boolean fileExists(String filename) {
        return fileExists(filename, false);
    }

    public static boolean fileExists(String filename, boolean showErrMsg) {
        if (!filename.isEmpty()) {
            if (new File(filename).exists()) {
                return true;
            } else if (showErrMsg) {
                Logger.addLog(LANG_ERR_FILE_NOT_FOUND + ": " + filename, LMT_ERROR);
            }
        }

        return false;
    }

    public static String getFileNameOnly(String filename) {
        return FilenameUtils.getBaseName(filename);
    }

    public static long getFileSize(String filename) {
        return new File(filename).length() / 1024;
    }

    public static boolean createTextFile(String filename, String text) {
        try {
            FileUtils.writeStringToFile(new File(filename), text);
            return true;
        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }

    public static ProcessResult runProc(String args) {
        boolean result = false;
        StringBuilder output = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec(args);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                process.waitFor();
            }

            result = true;

        } catch (IOException | InterruptedException ioe) {
            Logger.addLog(ioe);
        }

        return new ProcessResult(result, output.toString());
    }

    public static void decryptAVClass(String secretKey, String fileInputPath, String fileOutPath) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        byte[] b = {1, 2, 3, 4, 5, 6, 7, 8};
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(b));

        File f = new File(fileInputPath);
        FileInputStream inputStream = new FileInputStream(f);
        byte[] inputBytes = new byte[(int) f.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        File fileEncryptOut = new File(fileOutPath);
        FileOutputStream outputStream = new FileOutputStream(fileEncryptOut);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }
}