package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.kaps.contoller.InputHandler;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.Sidekick;

import java.util.Set;

public class MainScreen extends ApplicationAdapter {
	private static final int GRID_WIDTH=6, GRID_HEIGHT=13;
	private InputHandler controller;

	public static OrthographicCamera camera;
	public static SpriteBatch batch;
	public static Dimensions dim;
	public static Level level;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		dim = new Dimensions(GRID_WIDTH, GRID_HEIGHT, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		level = new Level(GRID_WIDTH, GRID_HEIGHT, Set.of(Sidekick.ZYRAME, Sidekick.SEAN));

		controller = new InputHandler();
		Gdx.input.setInputProcessor(controller);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.8f, 0.85f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		level.render();
		level.update();
		controller.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
