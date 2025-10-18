package com.km.lastwar.vpbot.application.routine.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@ApplicationScoped
public class BotControlService implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

    private static final Logger logger = LoggerFactory.getLogger(BotControlService.class);

    private final AtomicBoolean shouldStop = new AtomicBoolean(false);

    private final AtomicBoolean isPaused = new AtomicBoolean(false);

    @PostConstruct
    public void registerHook() {
        // Disable ALL JNativeHook internal logging
        java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName())
                .setLevel(Level.OFF);

//        try {
//            GlobalScreen.registerNativeHook();
//            GlobalScreen.addNativeKeyListener(this);
//
//            // Disable mouse listener
//            GlobalScreen.addNativeMouseListener(this);
//            GlobalScreen.addNativeMouseMotionListener(this);
//
//            logger.info("Global hook registered");
//        } catch (NativeHookException e) {
//            logger.error("Failed to register global hook", e);
//        }
    }

    @PreDestroy
    public void unregisterHook() {

        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreen.removeNativeMouseMotionListener(this);
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            logger.warn("Failed to unregister hook", e);
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        int code = e.getKeyCode();
        if (code == NativeKeyEvent.VC_ESCAPE) {
            shouldStop.set(true);
        } else if (code == NativeKeyEvent.VC_SPACE) {
            isPaused.set(!isPaused.get());
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) { /* noop */ }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) { /* noop */ }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) { /* noop */ }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) { /* noop */ }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) { /* noop */ }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) { /* noop */ }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) { /* noop */ }

    public boolean shouldStop() {

        return shouldStop.get();
    }

    public boolean isPaused() {

        return isPaused.get();
    }

    public void reset() {

        shouldStop.set(false);
        isPaused.set(false);
    }
}