package com.github.helltar.anpaside.logging;

import com.github.helltar.anpaside.MainActivity;
import com.github.helltar.anpaside.MainApp;

public class Logger {
    private static void addGuiLog(String msg, int msgType) {
        MainActivity.addGuiLog(msg, msgType);
    }

    public static void addLog(String msg) {
        addGuiLog(msg, LoggerMessageType.TEXT.ordinal());
    }

    public static void addLog(String msg, int msgType) {
        addGuiLog(msg, msgType);
    }

    public static void addLog(Exception exception) {
        addGuiLog(
            exception.getMessage(),
            LoggerMessageType.ERROR.ordinal()
        );
        RoboErrorReporter.reportError(MainApp.getContext(), exception);
    }
}

