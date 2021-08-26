package com.github.helltar.anpaside.logging;

public interface LoggerInterface {
    void showLoggerMessage(
        String message,
        LoggerMessageType messageType
    );
    
    void showLoggerErrorMessage(Exception exception);
}
