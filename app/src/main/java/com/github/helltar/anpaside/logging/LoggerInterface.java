package com.github.helltar.anpaside.logging;

public interface LoggerInterface {
    void showLoggerMessage(String msg, int msgType);
    
    void showLoggerErrorMessage(Exception exception);
}
