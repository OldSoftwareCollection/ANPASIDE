package com.github.helltar.anpaside.project;

import android.content.Context;

import com.github.helltar.anpaside.ProcessResult;
import com.github.helltar.anpaside.R;
import com.github.helltar.anpaside.Utils;
import com.github.helltar.anpaside.logging.LoggerInterface;
import com.github.helltar.anpaside.logging.LoggerMessageType;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectBuilder extends ProjectManager {
    private final String mp3cc;
    private final String stubsDir;
    private final String globLibsDir;

    private final String projPrebuildDir;

    public ProjectBuilder(
        Context context,
        LoggerInterface logger,
        Utils utils,
        String filename,
        String mp3cc,
        String stubsDir,
        String globLibsDir
    ) {
        super(
            context,
            logger,
            utils
        );
        this.mp3cc = mp3cc;
        this.stubsDir = stubsDir;
        this.globLibsDir = globLibsDir;

        openProject(filename);

        projPrebuildDir = getProjectPath() + context.getString(R.string.directory_prebuild);
    }

    private ProcessResult runCompiler(String filename, boolean detectUnits) {
        String args =
            mp3cc
            + " -s " + filename
            + " -o " + projPrebuildDir
            + " -l " + globLibsDir
            + " -p " + getProjLibsDir()
            + " -m " + Integer.toString(getMathType())
            + " c " + Integer.toString(getCanvasType());

        if (detectUnits) {
            args += " -d";
        }

        return utils.runProc(args);
    }

    public boolean compile(final String filename) {
        ProcessResult compilerProc = runCompiler(filename, true);

        if (!compilerProc.started) {
            return false;
        }

        String output = compilerProc.output;

        Matcher matcher = Pattern.compile("\\^0(.*?)\n").matcher(output);

        while (matcher.find()) {
            String unitName = matcher.group(1);
            String unitFilename = getProjectPath() + context.getString(R.string.directory_src)
                + unitName + context.getString(R.string.extension_pas);

            if (utils.fileExists(unitFilename, true)) {
                if (!utils.fileExists(
                        projPrebuildDir + unitName + context.getString(R.string.extension_class)
                )) {
                    if (compile(unitFilename)) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }

        // компиляция родителя
        compilerProc = runCompiler(filename, false);

        if (!compilerProc.started) {
            return false;
        }

        output = compilerProc.output;

        // очистка ненужной информации
        String cleanOutput = deleteCharacters(output);

        if (!isErr(output)) {
            logger.showLoggerMessage(
                cleanOutput,
                LoggerMessageType.TEXT
            );
    
            return findAndCopyStubs(output) && findAndCopyLib(output);
        } else {
            logger.showLoggerMessage(
                cleanOutput,
                LoggerMessageType.ERROR
            );
        }

        return false;
    }

    private boolean isErr(String output) {
        return output.contains("[Pascal Error]") || output.contains("Fatal error");
    }

    private boolean findAndCopyLib(String output) {
        Matcher matcher = Pattern.compile("\\^1(.*?)\n").matcher(output);

        while (matcher.find()) {
            String libName = "Lib_" + matcher.group(1) + context.getString(R.string.extension_class);

            // пробуем найти библиотеку в libs каталоге проекта
            if (!utils.copyFileToDir(getProjLibsDir() + libName, projPrebuildDir, false)) {
                // если нет берем из глобального
                if (!utils.copyFileToDir(globLibsDir + libName, projPrebuildDir, true)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean findAndCopyStubs(String output) {
        Matcher m = Pattern.compile("\\^2(.*?)\n").matcher(output);

        while (m.find()) {
            if (!utils.copyFileToDir(
                    stubsDir + m.group(1),
                    projPrebuildDir
            )) {
                return false;
            }
        }

        return true;
    }

    public boolean build() {
        String jarFilename = getJarFilename();

        if (prebulid()
            && compile(getMainModuleFilename())
            && createZip(projPrebuildDir, jarFilename)
            && addResToZip(
                getProjectPath() + context.getString(R.string.directory_res),
                jarFilename
                )
            ) {
                logger.showLoggerMessage(
                    context.getString(R.string.message_build_successfully) + "\n"
                        + context.getString(R.string.directory_bin) + getMidletName() + context.getString(R.string.extension_jar)
                        + "\n" + utils.getFileSize(jarFilename) + " KB",
                    LoggerMessageType.INFO
                );
    
                return true;
        }

        return false;
    }

    private boolean prebulid() {
        if (!mkProjectDirs(getProjectPath())) {
            return false;
        }

        try {
            FileUtils.cleanDirectory(new File(projPrebuildDir));
        } catch (IOException exception) {
            logger.showLoggerErrorMessage(exception);
        }

        String manifestDir = projPrebuildDir + "META-INF";
        
        return utils.mkdir(manifestDir)
            && createManifest(manifestDir)
            && utils.copyFileToDir(
                stubsDir + "/" + context.getString(R.string.fw_class),
                projPrebuildDir
            );
    }

    private boolean isDirEmpty(String dirPath) {
        File file = new File(dirPath);
        return file.isDirectory() && file.list().length <= 0;
    }

    public String getJarFilename() {
        return getProjectPath() + context.getString(R.string.directory_bin) + getMidletName()
            + context.getString(R.string.extension_jar);
    }

    private String deleteCharacters(String output) {
        String[] lines = output.split("\n");
        StringBuilder cleanOutput = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].startsWith("@")) {
                cleanOutput.append(lines[i] + "\n");
            }
        }

        return cleanOutput.toString()
            .replace("[Pascal Error]", "")
            .replace("^1", "Lib: ")
            .replace("^2", "")
            .replace("^3", "")
            .trim();
    }

    private boolean createManifest(String path) {
        int midp = getCanvasType() < 1 ? 1 : 2;
        int cldc = midp == 2 ? 1 : 0;

        return utils.createTextFile(
            path + "/MANIFEST.MF",
            String.format(
                context.getString(R.string.template_manifest),
                getMidletName(),
                getMidletVendor(),
                getMidletName(),
                getMidletVersion(),
                cldc,
                midp
            )
        );
    }

    private boolean addResToZip(String resDir, String zipFilename) {
        if (isDirEmpty(resDir)) {
            return true;
        }

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
        } catch (ZipException exception) {
            logger.showLoggerMessage(
                context.getString(R.string.err_failed_create_archive) + ": " + dirPath
                    + " (" + exception.getMessage() + ")",
                LoggerMessageType.ERROR
            );
        }

        return false;
    }
}

