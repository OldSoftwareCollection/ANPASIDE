package com.github.helltar.anpaside.project;

import static com.github.helltar.anpaside.Consts.DATA_PKG_PATH;
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
            && createHelloWorld(projectPath + context.getString(R.string.directory_src)
                + name.toLowerCase() + context.getString(R.string.extension_pas))) {
                createGitignore(projectPath);
                copyFileToDir(
                    DATA_PKG_PATH + context.getString(R.string.assets_directory_files) + "/icon.png",
                    projectPath + context.getString(R.string.directory_res)
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
            mainModuleFilename = projectPath + context.getString(R.string.directory_src)
                + getMainModuleName() + context.getString(R.string.extension_pas);
            projLibsDir = projectPath + context.getString(R.string.directory_libs);

            return true;

        } catch (IOException ioe) {
            Logger.addLog(ioe);
        }

        return false;
    }

    public boolean mkProjectDirs(String path) {
        return mkdir(path + context.getString(R.string.directory_bin))
            && mkdir(path + context.getString(R.string.directory_src))
            && mkdir(path + context.getString(R.string.directory_prebuild))
            && mkdir(path + context.getString(R.string.directory_res))
            && mkdir(path + context.getString(R.string.directory_libs));
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

