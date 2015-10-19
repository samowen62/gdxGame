package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "lol ur a gey";
		config.width = 560;
		config.height = 400;
		config.foregroundFPS = 30;
		
		
		new LwjglApplication(new MyGdxGame(), config);
		Gdx.app.log("The Time", "starting...");
	}
}