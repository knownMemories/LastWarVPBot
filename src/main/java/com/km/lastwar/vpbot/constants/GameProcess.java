package com.km.lastwar.vpbot.constants;

public class GameProcess {

    // TODO we should not hardcoded paths. Move to App GUI instead
    public static String EMU_PROCESS_NAME = "dnconsole.exe";

    public static String GAME_PACKAGE_NAME = "com.fun.lastwar.gp";

    public static String ADB_EXE_PATH = "C:\\LDPlayer\\LDPlayer9\\adb.exe";

    public static String BOT_VP_PROCESS = "C:\\LDPlayer\\LDPlayer9\\" + EMU_PROCESS_NAME + " launchex --index 0 --packagename " + GAME_PACKAGE_NAME;
}
