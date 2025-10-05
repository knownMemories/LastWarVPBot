package com.km.lastwar.vpbot.data;

public class Constants {

	public static final String PROJECT_RESOURCES_PATH = "/";
	public static final String PROJECT_IMAGES_PATH = PROJECT_RESOURCES_PATH + "images/";

	/**
	 * PATHS
	 */
	public static final String SANDBOX_PATH = "C://tmp/";
	public static final String MOUSE_SCREEN_PATH = SANDBOX_PATH + "mouse/";
	public static final String DEBUG_SCREEN_PATH = SANDBOX_PATH + "debug/";
	public static final String TMP_SCREEN_PATH = SANDBOX_PATH + "tmp/";
	public static final String KICK_SCREEN_PATH = SANDBOX_PATH + "kick/";
	public static final String STATS_PATH = SANDBOX_PATH + "stats.ser";

	/**
	 * Image default diff marging
	 */
	public static final int SIMILARITY_THRESHOLD = 50;

	/**
	 * 0=principal 1=secondary
	 */
	public static final int SCREEN_INDEX_PRINCIPAL = 0;
	public static final int SCREEN_INDEX_SECONDARY = 1;
}
