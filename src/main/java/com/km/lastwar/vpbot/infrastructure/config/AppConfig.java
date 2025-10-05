package com.km.lastwar.vpbot.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppConfig {

    // Pfade als Methoden – später austauschbar durch Properties
    public String getProjectResourcesPath() {
        return "/";
    }

    public String getProjectImagesPath() {
        return getProjectResourcesPath() + "images/";
    }

    public String getSandboxPath() {
        return "C://tmp/";
    }

    public String getMouseScreenPath() {
        return getSandboxPath() + "mouse/";
    }

    public String getDebugScreenPath() {
        return getSandboxPath() + "debug/";
    }

    public String getTmpScreenPath() {
        return getSandboxPath() + "tmp/";
    }

    public String getKickScreenPath() {
        return getSandboxPath() + "kick/";
    }

    public String getStatsPath() {
        return getSandboxPath() + "stats.ser";
    }

    public int getSimilarityThreshold() {
        return 50;
    }

    public int getScreenIndexPrincipal() {
        return 0;
    }

    public int getScreenIndexSecondary() {
        return 1;
    }
}