package com.github.helltar.anpaside.project;

import com.github.helltar.anpaside.Logger;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;

import static com.github.helltar.anpaside.Utils.*;
import static com.github.helltar.anpaside.Consts.*;

public class ProjectBuilder {

    private final String mp3cc;
    private final String stubsDir;
    private final String globLibsDir;

    private final String projPath;
    private final String mainModule;
    private final String prebuildDir;

    private final int mathType;
    private final int canvasType;

    private final String midletName;
    private final String midletVendor;
    private final String midletVersion;
    private final String midletIcon;

    private final String jarFilename;

    public static class Builder {

        private final String mp3cc;
        private final String stubsDir;
        private final String globLibsDir;
        private final String projPath;
        private final String mainModule;

        private int mathType = 0;
        private int canvasType = 1;

        private String midletName = "app";
        private String midletVendor = "vendor";
        private String midletVersion = "1.0.0";
        private String midletIcon = "/icon.png";

        public Builder(String mp3cc, 
                       String stubsDir, String globLibsDir, 
                       String projPath, String mainModule) {

            this.mp3cc = mp3cc;
            this.stubsDir = stubsDir;
            this.globLibsDir = globLibsDir;
            this.projPath = projPath;
            this.mainModule = mainModule;
        }

        public ProjectBuilder create() {
            return new ProjectBuilder(this);
        }

        public Builder setMathType(int val) {
            mathType = val;
            return this;
        }

        public Builder setCanvasType(int val) {
            canvasType = val;
            return this;
        }

        public Builder setMidletName(String val) {
            midletName = val;
            return this;
        }

        public Builder setMidletVendor(String val) {
            midletVendor = val;
            return this;
        }

        public Builder setMidletVersion(String val) {
            midletVersion = val;
            return this;
        }

        public Builder setMidletIcon(String val) {
            midletIcon = val;
            return this;
        }
    }

    private ProjectBuilder(Builder b) {
        mp3cc = b.mp3cc;
        stubsDir = b.stubsDir;
        globLibsDir = b.globLibsDir;

        projPath = b.projPath;
        mainModule = b.mainModule;
        prebuildDir = projPath + DIR_PREBUILD;

        mathType = b.mathType;
        canvasType = b.canvasType;

        midletName = b.midletName;
        midletVendor = b.midletVendor;
        midletVersion = b.midletVersion;
        midletIcon = b.midletIcon;

        jarFilename = projPath + DIR_BIN + midletName + EXT_JAR;
    }

    private boolean prebulid() {
        try {
            FileUtils.cleanDirectory(new File(prebuildDir));
        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        String manifestDir = prebuildDir + "META-INF";

        if (mkdir(manifestDir)) {
            if (createManifest(manifestDir)) {
                if (copyFileToDir(stubsDir + "/" + FW_CLASS, prebuildDir)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean build() {
        if (prebulid()) {
            if (compile(mainModule)) {
                if (createZip(prebuildDir, jarFilename)) {
                    if (addResToZip(projPath + DIR_RES, jarFilename)) {
                        Logger.addLog(
                            "Сборка успешно завершена, " 
                            + DIR_BIN + midletName + EXT_JAR + ", "
                            + getFileSize(jarFilename) + " KB", 1);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void findAndCopyLib(String output) {
        Matcher m = Pattern.compile("\\^1(.*?)\n").matcher(output);

        while (m.find()) {
            String libName = "Lib_" + m.group(1) + EXT_CLASS;
            String libFilename = projPath + DIR_LIBS + libName;

            if (fileExists(libFilename)) {
                copyFileToDir(libFilename, prebuildDir);
            } else {
                libFilename = globLibsDir + libName;

                if (fileExists(libFilename)) {
                    copyFileToDir(libFilename, prebuildDir);
                }
            }
        }
    }

    public boolean compile(String filename) {
        String args =
            mp3cc
            + " -s " + filename
            + " -o " + prebuildDir
            + " -l " + globLibsDir
            + " -p " + projPath + DIR_LIBS
            + " -m " + Integer.toString(mathType)
            + " c " + Integer.toString(canvasType);

        String output = runProc(args + " -d"); // detect units

        Matcher m = Pattern.compile("\\^0(.*?)\n").matcher(output);

        while (m.find()) {
            String moduleName = m.group(1);

            if (!(new File(projPath + DIR_PREBUILD + moduleName + EXT_CLASS).exists())) {
                if (!(compile(projPath + DIR_SRC + moduleName + EXT_PAS))) {
                    return false;
                }
            }
        }

        output = runProc(args);
        findAndCopyLib(output);

        boolean result = false;
        String cleanOutput = deleteCharacters(output);

        if (!(isErr(output))) {
            Logger.addLog(cleanOutput);
            result = true;
        } else {
            Logger.addLog(cleanOutput, 2);
        }

        return result;
    }

    private boolean isErr(String output) {
        return output.contains("[Pascal Error]");
    }

    public String getJarFilename() {
        return jarFilename;
    }

    private String deleteCharacters(String output) {
        String[] lines = output.split("\n");
        String cleanOutput = "";

        for (int i = 0; i < lines.length; i++) {
            if (!(lines[i].contains("@"))) {
                cleanOutput += lines[i] + "\n";
            }
        }

        cleanOutput = cleanOutput
            .replace("[Pascal Error]", "Error!:")
            .replace("^1", "Lib: ")
            .replace("^2", "")
            .replace("^3", "")
            .trim();

        return cleanOutput;
    }

    private boolean createManifest(String path) {
        int midp = (canvasType < 1) ? 1 : 2;
        int cldc = (midp == 2) ? 1 : 0;

        return createTextFile(path + "/MANIFEST.MF",
                              String.format(TPL_MANIFEST,
                                            midletName, midletVendor, midletName, midletVersion,
                                            cldc, midp));
    }

    private boolean addResToZip(String resDir, String zipFilename) {
        return createZip(resDir, zipFilename, true);
    }

    private boolean createZip(String dirPath, String zipFilename) {
        return createZip(dirPath, zipFilename, false);
    }

    private boolean createZip(String dirPath, String zipFilename, boolean isAddToArchive) {
        ZipParameters param = new ZipParameters();

        param.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        param.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA); 
        param.setIncludeRootFolder(false);

        if (isAddToArchive) {
            param.setRootFolderInZip("/");
        }

        try {
            new ZipFile(zipFilename).addFolder(dirPath, param);
            return true;
        } catch (ZipException ze) {
            Logger.addLog("Не удалось создать архив: " + dirPath + " (" + ze.getMessage() + ")", 2);
        }

        return false;
    }
}

