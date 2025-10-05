package com.km.lastwar.vpbot.utils;

public class ThreadUtil {

	public static void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
