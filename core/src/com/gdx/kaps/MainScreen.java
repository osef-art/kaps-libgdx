package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.kaps.contoller.InputHandler;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.Sidekick;
import com.gdx.kaps.renderer.Dimensions;
import com.gdx.kaps.renderer.ShapeRendererAdaptor;
import com.gdx.kaps.renderer.TextRendererAdaptor;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class MainScreen extends ApplicationAdapter {
	private InputHandler controller;
	private Level level;

	public static OrthographicCamera camera;
	public static ShapeRendererAdaptor sra;
	public static TextRendererAdaptor tra;
	public static SpriteBatch batch;
	public static Dimensions dim;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		sra = new ShapeRendererAdaptor();
		tra = new TextRendererAdaptor();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		// TODO: handle all sidekicks (when they have powers) (strategy)
		level = new Level(
			Path.of("android/assets/levels/level" + new Random().nextInt(21)),
			new HashSet<>(Arrays.asList(Sidekick.ZYRAME, Sidekick.SEAN))
		);

		controller = new InputHandler(level);
		Gdx.input.setInputProcessor(controller);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.8f, 0.85f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		controller.update();
		level.update();
		level.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		sra.dispose();
	}
}
