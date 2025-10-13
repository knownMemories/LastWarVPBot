package com.km.lastwar.vpbot.constants;

public class GameProcess {

    // TODO we should not use hardcoded paths. Move to App GUI instead
    public static final String EMULATOR_NAME = "LDPlayer";

    private static final int SHORT_PAUSE_MS = 500;

    private static final int MEDIUM_PAUSE_MS = 2000;

    public static String EMU_PROCESS_NAME = "dnconsole.exe";

    public static String GAME_PACKAGE_NAME = "com.fun.lastwar.gp";

    public static String ADB_EXE_PATH = "C:\\LDPlayer\\LDPlayer9\\adb.exe";

    public static String BOT_VP_PROCESS = "C:\\LDPlayer\\LDPlayer9\\" + EMU_PROCESS_NAME + " launchex --index 0 --packagename " + GAME_PACKAGE_NAME;
}
