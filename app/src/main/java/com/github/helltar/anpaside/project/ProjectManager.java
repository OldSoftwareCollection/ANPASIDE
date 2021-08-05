package com.github.helltar.anpaside.project;

import android.content.Context;

import com.github.helltar.anpaside.R;
import com.github.helltar.anpaside.Utils;
import com.github.helltar.anpaside.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

public class ProjectManager extends ProjectConfig {
    protected Context context;
    protected Utils utils;
    private String projectPath = "";
    private String projectConfigFilename = "";
    private String mainModuleFilename = "";
    private String projLibsDir = "";
    
    public ProjectManager(
        Context context,
        Utils utils
    ) {
        super();
        this.context = context;
        this.utils = utils;
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
    
        final String dataPackagePath = context.getApplicationInfo().dataDir + "/";
    
        if (mkProjectDirs(projectPath)
            && createConfigFile(
                projectConfigFilename,
                name
            )
            && createHelloWorld(
                projectPath + context.getString(R.string.directory_src) + name.toLowerCase() + context.getString(R.string.extension_pas)
            )
        ) {
            createGitignore(projectPath);
            utils.copyFileToDir(
                dataPackagePath + context.getString(R.string.assets_directory_files)
                    + "/icon.png",
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
        return utils.mkdir(path + context.getString(R.string.directory_bin))
            && utils.mkdir(path + context.getString(R.string.directory_src))
            && utils.mkdir(path + context.getString(R.string.directory_prebuild))
            && utils.mkdir(path + context.getString(R.string.directory_res))
            && utils.mkdir(path + context.getString(R.string.directory_libs));
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
        return utils.createTextFile(
            path + ".gitignore",
            context.getString(R.string.template_gitignore)
        );
    }

    private boolean createHelloWorld(String filename) {
        return utils.createTextFile(
            filename,
            String.format(
                context.getString(R.string.template_hello_world),
                utils.getFileNameOnly(filename)
            )
        );
    }

    public boolean createModule(String filename) {
        return utils.createTextFile(
            filename,
            String.format(
                context.getString(R.string.template_module),
                utils.getFileNameOnly(filename)
            )
        );
    }
}

