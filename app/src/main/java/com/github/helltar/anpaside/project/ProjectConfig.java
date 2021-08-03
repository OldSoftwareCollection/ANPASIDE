package com.github.helltar.anpaside.project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ProjectConfig {

    private final Properties properties = new Properties();

    public void open(String filename) throws IOException {
        properties.load(new FileInputStream(filename));
    }

    public void save(String filename) throws IOException {
        properties.store(new FileOutputStream(filename), null);
    }

    public void setMainModuleName(String mainModule) {
        properties.setProperty("MainModule", mainModule);
    }

    public String getMainModuleName() {
        return properties.getProperty("MainModule", "");
    }

    public void setMathType(int mathType) {
        properties.setProperty("MathType", Integer.toString(mathType));
    }

    public int getMathType() {
        return Integer.parseInt(properties.getProperty("MathType", "0"));
    }

    public void setCanvasType(int canvasType) {
        properties.setProperty("CanvasType", Integer.toString(canvasType));
    }

    public int getCanvasType() {
        return Integer.parseInt(properties.getProperty("CanvasType", "1"));
    }

    public void setMidletName(String midletName) {
        properties.setProperty("Name", midletName);
    }

    public String getMidletName() {
        return properties.getProperty("Name", "app");
    }

    public void setMidletVendor(String midletVendor) {
        properties.setProperty("Vendor", midletVendor);
    }

    public String getMidletVendor() {
        return properties.getProperty("Vendor", "vendor");
    }

    public void setMidletIcon(String midletIcon) {
        properties.setProperty("Icon", midletIcon);
    }

    public String getMidletIcon() {
        return properties.getProperty("Icon", "/icon.png");
    }
    
    public void setVersion(String version) {
        properties.setProperty("Version", version);
    }

    public String getVersion() {
        return properties.getProperty("Version", "1");
    }
}
