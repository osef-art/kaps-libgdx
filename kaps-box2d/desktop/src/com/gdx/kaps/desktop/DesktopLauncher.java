package com.gdx.kaps.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.kaps.MainScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "KAPS";
		config.width = 480;
		config.height = 800;
		config.pauseWhenBackground = true;
		config.addIcon("img/icons/icon.png", Files.FileType.Classpath);
		new LwjglApplication(new MainScreen(), config);
	}
}
