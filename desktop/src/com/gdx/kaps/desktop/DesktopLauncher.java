package com.gdx.kaps.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.kaps.MainScreen;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "KAPS";
		config.width = 480;
		config.height = 800;
//		config.pauseWhenBackground = true;
		config.addIcon("android/assets/img/icons/icon.png", Files.FileType.Local);

		new LwjglApplication(new MainScreen(args), config);
	}
}
