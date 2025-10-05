package com.km.lastwar.vpbot.domain.exception;

public class GameWindowNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameWindowNotFoundException(String message) {
        super(message);
    }
}