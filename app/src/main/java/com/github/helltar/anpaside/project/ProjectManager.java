package com.github.helltar.anpaside.project;

import static com.github.helltar.anpaside.Consts.DATA_PKG_PATH;
import static com.github.helltar.anpaside.Consts.DIR_BIN;
import static com.github.helltar.anpaside.Consts.DIR_LIBS;
import static com.github.helltar.anpaside.Consts.DIR_PREBUILD;
import static com.github.helltar.anpaside.Consts.DIR_RES;
import static com.github.helltar.anpaside.Consts.DIR_SRC;
import static com.github.helltar.anpaside.Utils.copyFileToDir;
import static com.github.helltar.anpaside.Utils.createTextFile;
import static com.github.helltar.anpaside.Utils.getFileNameOnly;
import static com.github.helltar.anpaside.Utils.mkdir;

import android.content.Context;

import com.github.helltar.anpaside.R;
import com.github.helltar.anpaside.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

public class ProjectManager extends ProjectConfig {
    protected Context context;
    private String projectPath = "";
    private String projectConfigFilename = "";
    private String mainModuleFilename = "";
    private String projLibsDir = "";
    
    public ProjectManager(Context context) {
        super();
        this.context = context;
    }
    
    private boolean createConfigFile(String filename, String midletName) {
        setMidletName(midletName);
        setMainModuleName(midletName.toLowerCase());
        setMidletVendor("vendor");
        setVersion("1.0");

        try {
            save(filename);
            return true;
        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }

    public boolean createProject(String path, String name) {
        projectPath = path + name + "/";
        projectConfigFilename = projectPath + name + context.getString(R.string.extension_proj);

        if (
            mkProjectDirs(projectPath)
            && createConfigFile(
                projectConfigFilename,
                name
            )
            && createHelloWorld(projectPath + DIR_SRC + name.toLowerCase() + context.getString(R.string.extension_pas))) {
                createGitignore(projectPath);
                copyFileToDir(
                    DATA_PKG_PATH + context.getString(R.string.assets_directory_files) + "/icon.png",
                    projectPath + DIR_RES
                );
                return true;
        }

        return false;
    }

    public boolean openProject(String filename) {
        try {
            open(filename);

            projectPath = FilenameUtils.getFullPath(filename);
            projectConfigFilename = filename;
            mainModuleFilename = projectPath + DIR_SRC + getMainModuleName()
                + context.getString(R.string.extension_pas);
            projLibsDir = projectPath + DIR_LIBS;

            return true;

        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }

    public boolean mkProjectDirs(String path) {
        if (mkdir(path + DIR_BIN)
            && mkdir(path + DIR_SRC)
            && mkdir(path + DIR_PREBUILD)
            && mkdir(path + DIR_RES)
            && mkdir(path + DIR_LIBS)) {
            return true;
        }

        return false;
    }

    public boolean isProjectOpen() {
        return !projectPath.isEmpty();
    }

    public String getProjectConfigFilename() {
        return projectConfigFilename;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getProjLibsDir() {
        return projLibsDir;
    }

    public String getMainModuleFilename() {
        return mainModuleFilename;
    }

    public String getMidletVersion() {
        return getVersion();
    }

    private boolean createGitignore(String path) {
        return createTextFile(
            path + ".gitignore",
            context.getString(R.string.template_gitignore)
        );
    }

    private boolean createHelloWorld(String filename) {
        return createTextFile(
            filename,
            String.format(
                context.getString(R.string.template_hello_world),
                getFileNameOnly(filename)
            )
        );
    }

    public boolean createModule(String filename) {
        return createTextFile(
            filename,
            String.format(
                context.getString(R.string.template_module),
                getFileNameOnly(filename)
            )
        );
    }
}

