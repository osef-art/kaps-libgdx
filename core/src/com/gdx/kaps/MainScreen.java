package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.kaps.contoller.InputHandler;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.Sidekick;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

public class MainScreen extends ApplicationAdapter {
	static int GRID_WIDTH, GRID_HEIGHT;
	private InputHandler controller;
	private Level level;

	public static OrthographicCamera camera;
	public static ShapeRendererAdaptor sra;
	public static SpriteBatch batch;
	public static Dimensions dim;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		sra = new ShapeRendererAdaptor();

		// TODO: handle all sidekicks (when they have powers) (strategy)
		level = new Level(
			Path.of("levels/level0"),
			new HashSet<>(Arrays.asList(Sidekick.ZYRAME, Sidekick.SEAN))
		);

		GRID_WIDTH = level.grid().width();
		GRID_HEIGHT = level.grid().height();
		dim = new Dimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
	}
}
